package com.stock.security.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.stock.security.dto.AuthResponseDto;
import com.stock.security.dto.UserRegistrationDto;
import com.stock.security.service.AuthService;
import com.stock.security.service.LogoutHandlerService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author atquil
 */
@RestController
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final LogoutHandlerService logoutHandlerService;
    
    @CrossOrigin
    @PostMapping("/sign-up")
//    @CrossOrigin(origins = "https://localhost:5003", allowedHeaders = "*", allowCredentials = "true")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDto userRegistrationDto,
                                          BindingResult bindingResult, HttpServletResponse response){

        log.info("[AuthController:registerUser]Signup Process Started for user:{}",userRegistrationDto.userName());

        if (bindingResult.hasErrors()) {
            List<String> errorMessage = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            log.error("[AuthController:registerUser]Errors in user:{}",errorMessage);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }
        return ResponseEntity.ok(authService.registerUser(userRegistrationDto, response));
    }    


    @GetMapping("/sign-in")
    public ResponseEntity<?> authenticateUser(Authentication authentication, HttpServletResponse response){
    	log.info("#1 /sigh-in authentication.getName() = " + authentication.getName());
    	log.info("#2 /sigh-in authentication.getCredentials() = " + authentication.getCredentials());
        return ResponseEntity.ok(authService.getJwtTokensAfterAuthentication(authentication, response));
    }    
    

//  @PreAuthorize("hasAnyRole('ROLE_MANAGER','ROLE_ADMIN','ROLE_USER')")
    @CrossOrigin
//    @PreAuthorize("hasAuthority('SCOPE_REFRESH_TOKEN')")
    @PostMapping ("/refresh-token")
    public ResponseEntity<?> getAccessToken( @CookieValue(value = "refreshToken", required = false) String refreshToken,
    		@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
    	log.info("# 100 Refresh token is " + refreshToken);
    	if (refreshToken == null || refreshToken.isEmpty()) {
    		log.info("# 100 Refresh token is " + refreshToken);
    		return ResponseEntity.badRequest().body("Refresh token is missing");
    	}
        return ResponseEntity.ok(authService.getAccessTokenUsingRefreshToken(authorizationHeader, refreshToken));
    }
    
    
  //@PreAuthorize("hasAuthority('SCOPE_REFRESH_TOKEN')")
//    @PostMapping ("/logout")
//    public ResponseEntity<?> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, HttpServletRequest request, HttpServletResponse response){
//      return ResponseEntity.ok(logoutHandlerService.logout(request, response, authorizationHeader));
//    }
    
    @PreAuthorize("hasAuthority('SCOPE_REFRESH_TOKEN')")
    @PostMapping ("/log-out")
    public ResponseEntity<?> getOut(HttpServletRequest request, HttpServletResponse response){
    	
    	 Cookie[] cookies = request.getCookies();
         if (cookies != null) {
             String cook =  Arrays.stream(cookies)
                     .map(c -> c.getName() + "=" + c.getValue()).collect(Collectors.joining(", "));
             log.info("#1 Logout Cookies: " + cook);
         }
         
        return ResponseEntity.ok(authService.logoutUser(null, request, response));
    }
    
    @PreAuthorize("hasAuthority('SCOPE_REFRESH_TOKEN')")
    @PostMapping ("/log-out0")
    public ResponseEntity<?> getOut0(HttpServletRequest request, HttpServletResponse response){
    	
    	 Cookie[] cookies = request.getCookies();
         if (cookies != null) {
             String cook =  Arrays.stream(cookies)
                     .map(c -> c.getName() + "=" + c.getValue()).collect(Collectors.joining(", "));
             log.info("#1 Logout Cookies: " + cook);
         }
         
        return ResponseEntity.ok(authService.logoutUser(null, request, response));
    }   
    
    /**
     * Getting a new access token based on existing refresh token when refresh token present in browser
     * It is funny that if someone has access to the computer can copy refresh token and design an app
     * to make a call to get a new access token and vu a lja!
     * Also, it is a good idea to generate a new refresh token too.
     * @param request
     * @return
     */
//    @PreAuthorize("hasAuthority('SCOPE_REFRESH_TOKEN')")
    @CrossOrigin
    @GetMapping("/free/refresh-user")
    public ResponseEntity<?> refreshUserAccessToken(HttpServletRequest request, HttpServletResponse response) {
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
        	return ResponseEntity.ok(authService.getAccessTokenFromRequestWithCookies(refreshToken, response));
        }
        return null;
    }

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
    
    @GetMapping("/all-cookies0")
    public @ResponseBody String readAllCookies0(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            return Arrays.stream(cookies)
                    .map(c -> c.getName() + "=" + c.getValue()).collect(Collectors.joining(", "));
        }
        return "No cookies";
    }

}


//@GetMapping("/sign-in")
//public ResponseEntity<?> authenticateUser(Authentication authentication, HttpServletResponse response){
//	log.info("#1 /sigh-in authentication.getName() = " + authentication.getName());
//  return ResponseEntity.ok(authService.getJwtTokensAfterAuthentication(authentication, response));
//}

//// Before OCt 3
//@PreAuthorize("hasAuthority('SCOPE_REFRESH_TOKEN')")
//@PostMapping ("/refresh-token")
//public ResponseEntity<?> getAccessTokenOrig(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
//  return ResponseEntity.ok(authService.getAccessTokenUsingRefreshToken(authorizationHeader));
//}

//@GetMapping("/us-buy-list")
//public @ResponseBody List<SymbolStatus> getUsRecommendedBuySymbols() {
//return symbolService.getUsRecomendedBuySymbols();
//}


//@PreAuthorize("hasAuthority('SCOPE_REFRESH_TOKEN')")
//@PostMapping ("/log-out")
//public ResponseEntity<?> getOut(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, HttpServletRequest request, HttpServletResponse response){
//	
//	 Cookie[] cookies = request.getCookies();
//   if (cookies != null) {
//       String cook =  Arrays.stream(cookies)
//               .map(c -> c.getName() + "=" + c.getValue()).collect(Collectors.joining(", "));
//       log.info("#1 Logout COokies: " + cook);
//       
//   }
//   
//  return ResponseEntity.ok(authService.logoutUser(authorizationHeader, request, response));
//}


