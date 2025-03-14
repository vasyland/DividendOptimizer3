package com.stock.security.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import com.stock.security.config.RSAKeyRecord;
import com.stock.security.config.jwt.JwtTokenUtils;
import com.stock.security.dto.TokenType;
import com.stock.security.entity.RefreshTokenEntity;
import com.stock.security.entity.UserInfo;
import com.stock.security.repo.RefreshTokenRepo;
import com.stock.security.repo.UserInfoRepo;
import com.stock.security.util.CookieService;

import jakarta.servlet.http.Cookie;

//import com.atquil.dto.TokenType;
//import com.atquil.repo.RefreshTokenRepo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author atquil
 * 
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LogoutHandlerService implements LogoutHandler {

	private final RefreshTokenRepo refreshTokenRepo;
	private final CookieService cookieService;
	private final UserInfoRepo userInfoRepo;

	private final RSAKeyRecord rsaKeyRecord;
	private final JwtTokenUtils jwtTokenUtils;

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

		log.info("[LogoutHandlerService.logout] LOGOUT Handler: ");

		final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

		log.info("LOGOUT Handler authHeader = " + authHeader);

		if (!authHeader.startsWith(TokenType.Bearer.name())) {
			return;
		}

		String accessToken = authHeader.substring(7);
		log.info("[LogoutHandlerService => accessToken] " + accessToken);

		JwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(rsaKeyRecord.rsaPublicKey()).build();
		final Jwt jwtToken = jwtDecoder.decode(accessToken);
		String userEmail = jwtTokenUtils.getUserName(jwtToken);

		log.info("jwtTokenUtils.getUserName() = " + userEmail);

		/** Here add refresh token removal */
		// Find user id using email
		UserInfo userInfoEntity = userInfoRepo.findByEmailId(userEmail).get();
		if (userInfoEntity != null) {
			log.info("Found User ID: " + userInfoEntity.getId());

			List<RefreshTokenEntity> refreshTokens = refreshTokenRepo.findByUserId(userInfoEntity.getId());
			RefreshTokenEntity currentRefreshToken = refreshTokens.getFirst();
			currentRefreshToken.setRefreshToken("User logged out");
			refreshTokenRepo.deleteAll(refreshTokens);
			refreshTokenRepo.save(currentRefreshToken);
		}

		// Clear all cookies
		clearCookies(request, response);

		/* Update refresh token cookie with empty value in the response */
        Cookie cookie = new Cookie("refresh_token", null);
        cookie.setMaxAge(0);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        /* add cookie to response */
        response.addCookie(cookie);
		
		// Return a success response
		response.setStatus(HttpServletResponse.SC_OK);
		try {
			response.getWriter().write("{\"message\": \"Logout successful from LogoutHandlerService\"}");
			response.getWriter().flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	/**
	 * 
	 * @param response
	 */
	private void clearHeaders(HttpServletResponse response) {
		// Remove sensitive headers from the response
		response.setHeader("Authorization", null);
		response.setHeader("Set-Cookie", null);
	}

	/**
	 * 
	 * @param request
	 * @param response
	 */
	private void clearCookies(HttpServletRequest request, HttpServletResponse response) {
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				Cookie clearedCookie = new Cookie(cookie.getName(), "");
				clearedCookie.setMaxAge(0);
				clearedCookie.setPath("/");
				clearedCookie.setHttpOnly(true);
				clearedCookie.setSecure(true); // Set secure if using HTTPS
				response.addCookie(clearedCookie);
			}
		}
	}

	/**
	 * 
	 */
	// @Override
	public void logoutMine(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

		/**
		 * We need to get a refresh token from the request in order revoke it in the
		 * database
		 */
		Cookie[] cookies = request.getCookies();

		String refreshToken = "";
		/* Show all cookies */
		log.info("\n\n#800 Started LogoutHandlerService.logout()");

		log.info("# 801 Authentication data");
		if (authentication == null) {
			log.info("#802 Authentication data is NULL");
		}
//        log.info("#802 Authentication data - authentication.isAuthenticated() " + authentication.isAuthenticated());
//        log.info("#802 Authentication data - authentication.getPrincipal() " + authentication.getPrincipal().toString());

		if (cookies != null) {

			String searchedToken = cookieService.findCookieByName(cookies, "refresh_token");
			log.info("#803 Searched Token with name refresh_token = " + searchedToken);

			System.out.println(
					"--------------- SHOW ALL COOKIES -------------------------------------------------------");
			for (Cookie c : cookies) {
				System.out.println(c.getName() + " = " + c.getValue());
			}
			System.out.println(
					"--------------- END OF ALL COOKIES -------------------------------------------------------");

//        	allCookies = Arrays.stream(cookies)
//                    .map(c -> c.getName() + "=" + c.getValue()).collect(Collectors.joining(", "));

			Optional<Cookie> el = Arrays.stream(cookies)
					.filter(e -> e.getName().contains("refresh_token"))
					.findAny();

			System.out.println("Refresh Token: " + el.get());

			if (el != null) {
				refreshToken = el.get().getValue();
//    			int inx = token.indexOf("=");
//    			oldRefreshToken = token.substring(inx+1);
				System.out.println("Token Value: " + refreshToken);
			}

		} else {
			log.error("Cookies are NULL");
		}

//        if(!authHeader.startsWith(TokenType.Bearer.name())){
//            return;
//        }

		// final String refreshToken = authHeader.substring(7);

		var storedRefreshToken = refreshTokenRepo.findByRefreshToken(refreshToken)
				.map(token -> {
					token.setRevoked(true);
					refreshTokenRepo.save(token);
					return token;
				})
				.orElse(null);
	}
}
