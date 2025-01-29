package com.stock.security.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import com.stock.security.config.RSAKeyRecord;
import com.stock.security.config.SecurityConfig;
import com.stock.security.config.jwt.JwtTokenUtils;
import com.stock.security.config.user.UserInfoManagerConfig;
import com.stock.security.repo.RefreshTokenRepo;
import com.stock.security.util.CookieService;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomLogoutHandler implements LogoutHandler {

	private final CookieService cookieService;

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		// TODO Auto-generated method stub
		// Custom logic during logout
		System.out.println("\n\n Custom logout logic executed");

		if(authentication != null) {
			log.info("CUSTOM LOGOUT SUCCESS HANDLER 2: " + authentication.getName());
		} else {
			log.info("CUSTOM LOGOUT SUCCESS HANDLER 2 IS NULL");
		}
				
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {

			String searchedToken = cookieService.findCookieByName(cookies, "refresh_token");
			log.info("Custom #5 Searched Token with name refresh_token = " + searchedToken);

			System.out.println("--------------- #5-1 SHOW ALL COOKIES -------------------------");
			for (Cookie c : cookies) {
				System.out.println(c.getName() + " = " + c.getValue());
			}
			System.out.println("--------------- #5-2 END OF ALL COOKIES ---------------------------");
		} else {
			log.info("Custom #5 - Cookies are NULL");
		}

		// You can perform actions like:
		// - Invalidating tokens
		// - Clearing session data
		// - Logging the logout event

		// Example: Invalidate session
		if (request.getSession() != null) {
			request.getSession().invalidate();
		}

		// Example: Log the username of the user who logged out
		if (authentication != null) {
			System.out.println("#5 User " + authentication.getName() + " is logging out.");
		}
	}

}
