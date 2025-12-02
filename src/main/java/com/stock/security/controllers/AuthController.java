package com.stock.security.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpHeaders;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import com.stock.security.dto.UserRegistrationDto;
import com.stock.security.service.AuthService;
import com.stock.security.service.LogoutHandlerService;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@CrossOrigin
public class AuthController {

	private static final Logger log = LoggerFactory.getLogger(AuthController.class);
	
    private final AuthService authService;
    private final LogoutHandlerService logoutHandlerService;
    
    
    public AuthController(AuthService authService, LogoutHandlerService logoutHandlerService) {
		super();
		this.authService = authService;
		this.logoutHandlerService = logoutHandlerService;
	}

    // login
	@PostMapping("/sign-in")
    public ResponseEntity<?> authenticateUser(Authentication authentication, HttpServletResponse response){
    	log.info("#1 /sigh-in authentication.getName() = " + authentication.getName());
    	log.info("#2 /sigh-in authentication.getCredentials() = " + authentication.getCredentials());
        return ResponseEntity.ok(authService.getJwtTokensAfterAuthentication(authentication, response));
    }    
    
    
    @CrossOrigin
    @PostMapping("/sign-up")
//    @CrossOrigin(origins = "https://localhost:5003", allowedHeaders = "*", allowCredentials = "true")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDto userRegistrationDto,
                                          BindingResult bindingResult, HttpServletResponse response){

        log.info("[AuthController:registerUser] Signup Process Started for user:{}", userRegistrationDto.userName());

        if (bindingResult.hasErrors()) {
            List<String> errorMessage = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            log.error("[AuthController:registerUser 2] Errors in user:{}", errorMessage);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }
        return ResponseEntity.ok(authService.registerUser(userRegistrationDto, response));
    }


    /**
     * Login
     * @param authentication
     * @param response
     * @return
     */
//    @PostMapping("/sign-in")
//    public ResponseEntity<?> authenticateUser(Authentication authentication, HttpServletResponse response){
//    	log.info("#1 /sigh-in authentication.getName() = " + authentication.getName());
//    	log.info("#2 /sigh-in authentication.getCredentials() = " + authentication.getCredentials());
//        return ResponseEntity.ok(authService.getJwtTokensAfterAuthentication(authentication, response));
//    }    

    
    
    
    @CrossOrigin
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshUserAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = "";
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            refreshToken = Arrays.stream(cookies)
                    .filter(cookie -> "refresh_token".equals(cookie.getName()))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElse(null);

            if (refreshToken == null || refreshToken.isEmpty()) {
                return ResponseEntity.badRequest().body("Refresh token is missing");
            }

            return ResponseEntity.ok(authService.getNewTokensFromRequestWithCookies(refreshToken, response));
        }
        return ResponseEntity.badRequest().body("No cookies present");
    }

    
//    @PreAuthorize("hasAuthority('SCOPE_REFRESH_TOKEN')")
    @CrossOrigin
    @GetMapping("/refresh-token")  //refresh-token
    public ResponseEntity<?> refreshUserAccessToken0(HttpServletRequest request, HttpServletResponse response) {
    	//AuthResponseDto
    	String refreshToken = "";
        Cookie[] cookies = request.getCookies();
        
        log.info("#20 REFRESH TOKEN cookies: " + cookies);
        if (cookies != null) {
        	refreshToken = Arrays.stream(cookies)
        			.filter(cookie -> "refresh_token".equals(cookie.getName()))
        			.findFirst()
        			.map(Cookie::getValue)
                    .orElse(null);
        	
        	log.info("#21 REFRESH TOKEN: " + refreshToken);
        	
        	if (refreshToken == null || refreshToken.isEmpty()) {
        		log.info("# 100 Refresh token is " + refreshToken);
        		return ResponseEntity.badRequest().body("Refresh token is missing");
        	}
        	return ResponseEntity.ok(authService.getNewTokensFromRequestWithCookies(refreshToken, response));
        }
        return null;
    }

    
//    @PostMapping("/logout")
//    public void logout(HttpServletResponse response) {
//        // Create a cookie with the same name as the JWT cookie, set value to empty, and Max-Age to 0
//        Cookie cookie = new Cookie("refresh_token", null); // Replace with your cookie name
//        cookie.setHttpOnly(true);
//        cookie.setSecure(true); // Ensure it's secure if using HTTPS
//        cookie.setPath("/");
//        cookie.setMaxAge(0); // Delete the cookie immediately
//        response.addCookie(cookie);
//    }
    
    /**
     * Shows cookies when a request contains a valid access token
     * @param request
     * @return
     */
    @CrossOrigin
    @GetMapping("/all-cookies")
    public @ResponseBody String readAllCookies(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();
        log.info("#22 ALL COOKIES: " + cookies);
        if (cookies != null) {
            return Arrays.stream(cookies)
                    .map(c -> c.getName() + "=" + c.getValue()).collect(Collectors.joining(", "));
        }
        return "No cookies";
    }


//@GetMapping("/sign-in")
//public ResponseEntity<?> authenticateUser(Authentication authentication, HttpServletResponse response){
//	log.info("#1 /sigh-in authentication.getName() = " + authentication.getName());
//  return ResponseEntity.ok(authService.getJwtTokensAfterAuthentication(authentication, response));
//}


//@PreAuthorize("hasAuthority('SCOPE_REFRESH_TOKEN')")

    @PostMapping("/logout")
    public ResponseEntity<?> getOut(
        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
        HttpServletRequest request, 
        HttpServletResponse response) {

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            String cook = Arrays.stream(cookies)
                .map(c -> c.getName() + "=" + c.getValue())
                .collect(Collectors.joining(", "));
            log.info("#1 Logout Cookies: " + cook);
        }
        return ResponseEntity.ok(authService.logoutUser(authorizationHeader, request, response));
    }
    
}


///**
// * 1. Actually we need to get a full set of a new tokens (access and refresh) for the user as during login, 
// * but instead of using user id and password we use a refresh token.
// * The only thing tha we need to validate a refresh token and if it is invalid than we need to return 
// * a message for user to re-login. 
// * 2. This end- point should be open to public in the same way as a login end-point without any authorization in the header.
// * 
// * @param refreshToken
// * @param authorizationHeader
// * @return
// */
////@PreAuthorize("hasAnyRole('ROLE_MANAGER','ROLE_ADMIN','ROLE_USER')")
//@CrossOrigin
////@PreAuthorize("hasAuthority('SCOPE_REFRESH_TOKEN')")
//@PostMapping ("/refresh-token")
//public ResponseEntity<?> getAccessToken( @CookieValue(value = "refreshToken", required = false) String refreshToken,
//		@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
//	log.info("# 100 Refresh token is " + refreshToken);
//	if (refreshToken == null || refreshToken.isEmpty()) {
//		log.info("# 100 Refresh token is " + refreshToken);
//		return ResponseEntity.badRequest().body("Refresh token is missing");
//	}
//    return ResponseEntity.ok(authService.getAccessTokenUsingRefreshToken(authorizationHeader, refreshToken));
//}


////@PreAuthorize("hasAnyRole('ROLE_MANAGER','ROLE_ADMIN','ROLE_USER')")
//@CrossOrigin
////@PreAuthorize("hasAuthority('SCOPE_REFRESH_TOKEN')")
//@PostMapping ("/refresh-token")
//public ResponseEntity<?> getAccessToken( @CookieValue(value = "refreshToken", required = false) String refreshToken,
//		@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
//	log.info("# 100 Refresh token is " + refreshToken);
//	if (refreshToken == null || refreshToken.isEmpty()) {
//		log.info("# 100 Refresh token is " + refreshToken);
//		return ResponseEntity.badRequest().body("Refresh token is missing");
//	}
//  return ResponseEntity.ok(authService.getAccessTokenUsingRefreshToken(authorizationHeader, refreshToken));
//}    

//@PreAuthorize("hasAuthority('SCOPE_REFRESH_TOKEN')")
//@PostMapping ("/logout")
//public ResponseEntity<?> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, HttpServletRequest request, HttpServletResponse response){
//return ResponseEntity.ok(logoutHandlerService.logout(request, response, authorizationHeader));
//}


//@PreAuthorize("hasAuthority('SCOPE_REFRESH_TOKEN')")
//@PostMapping ("/log-out")
//public ResponseEntity<?> getOut(HttpServletRequest request, HttpServletResponse response){
//	
//	 Cookie[] cookies = request.getCookies();
//   if (cookies != null) {
//       String cook =  Arrays.stream(cookies)
//               .map(c -> c.getName() + "=" + c.getValue()).collect(Collectors.joining(", "));
//       log.info("#1 Logout Cookies: " + cook);
//   }
//   
//  return ResponseEntity.ok(authService.logoutUser(null, request, response));
//}

//@PreAuthorize("hasAuthority('SCOPE_REFRESH_TOKEN')")
//@PostMapping ("/log-out0")
//public ResponseEntity<?> getOut0(HttpServletRequest request, HttpServletResponse response){
//	
//	 Cookie[] cookies = request.getCookies();
//   if (cookies != null) {
//       String cook =  Arrays.stream(cookies)
//               .map(c -> c.getName() + "=" + c.getValue()).collect(Collectors.joining(", "));
//       log.info("#1 Logout Cookies: " + cook);
//   }
//   
//  return ResponseEntity.ok(authService.logoutUser(null, request, response));
//}   