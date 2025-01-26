package com.stock.security.service;


import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.stock.model.UserSubscription;
import com.stock.repositories.UserSubscriptionsRepository;
import com.stock.security.dto.AuthResponseDto;
import com.stock.security.dto.TokenType;
import com.stock.security.dto.UserRegistrationDto;
import com.stock.security.entity.RefreshTokenEntity;
import com.stock.security.entity.UserInfoEntity;
import com.stock.security.mapper.UserInfoMapper;
import com.stock.security.repo.RefreshTokenRepo;
import com.stock.security.repo.UserInfoRepo;
import com.stock.security.util.CookieService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserInfoRepo userInfoRepo;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final RefreshTokenRepo refreshTokenRepo;
    private final UserInfoMapper userInfoMapper;
    private final LogoutHandlerService logoutHandlerService;
    private final CookieService cookieService;
    private final UserSubscriptionsRepository userSubscriptionsRepository;
    
    
	 /**
	  * Registering a New User
	  * 1. a new user created, 
	  * 2. an access and refresh tokens generated
	  * 3. a trial subscription created
	  * @param userRegistrationDto
	  * @param httpServletResponse
	  * @return
	  */
		public AuthResponseDto registerUser(UserRegistrationDto userRegistrationDto,
				HttpServletResponse response) {
			try {
				log.info("[AuthService:registerUser]User Registration Started with :::{}", userRegistrationDto);

				Optional<UserInfoEntity> user = userInfoRepo.findByEmailId(userRegistrationDto.userEmail());
				if (user.isPresent()) {
					throw new Exception("User already exists");
				}

				UserInfoEntity userDetailsEntity = userInfoMapper.convertToEntity(userRegistrationDto);
				Authentication authentication = createAuthenticationObject(userDetailsEntity);

				/* Generate a JWT token */
				String accessToken = jwtTokenGenerator.generateAccessToken(authentication);
				String refreshToken = jwtTokenGenerator.generateRefreshToken(authentication);

				UserInfoEntity savedUserDetails = userInfoRepo.save(userDetailsEntity);
				
				log.info("#6 REGISTERED USER ID IS: " + savedUserDetails.getId());
				log.info("#7 Refresh Token: " + refreshToken);
				
				//Let's save the refreshToken as well
				createUserRefreshToken(userDetailsEntity, refreshToken);
				createRefreshTokenCookie(response, refreshToken);
				
				/* User gets trial access for three days  */
				UserSubscription subscription = this.createUserTrialSubscription(savedUserDetails.getId(), 3);
				String sSubscriptionEndDate = subscription.getSubscriptionEndDate().toString();

				log.info("[AuthService:registerUser] User:{} Successfully registered", savedUserDetails.getUserName());
				
				HttpHeaders headers = new HttpHeaders();
		        
		        // Create the cookie and add it to the response header
		        headers.add("Set-Cookie", "username=john_doe; Max-Age=86400; HttpOnly; Secure; Path=/");
				
				return AuthResponseDto.builder()
						.accessToken(accessToken)
						.accessTokenExpiry(5 * 60)
						.id(savedUserDetails.getId())
						.userName(savedUserDetails.getUserName())
						.tokenType(TokenType.Bearer)
						.subscripitonEndDate(sSubscriptionEndDate)
						.build();

			} catch (Exception e) {
				log.error("[AuthService:registerUser]Exception while registering the user due to :" + e.getMessage());
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
			}
		}    
    
		
		/** 
		 * Creating a trial subscription for a new user for n days
		 * @param id - user id gotten from db
		 * @param days - numbers of subscription days
		 * 
		 * return User Subscription
		 */
		public UserSubscription createUserTrialSubscription(Long userId, int days) {
			
			UserSubscription s = new UserSubscription();
			s.setUserId(userId);
			
			// Get the current date
	        LocalDate currentDate = LocalDate.now();
	        LocalDate endDate = currentDate.plusDays(days);
	        s.setSubscriptionEndDate(endDate);
	        
			return userSubscriptionsRepository.save(s);
		}
		
		
		/**
		 * Login Service
		 * @param authentication
		 * @param response
		 * @return
		 */
		public AuthResponseDto getJwtTokensAfterAuthentication(Authentication authentication, 
		        HttpServletResponse response) {
		    log.info("#09 AuthResponseDto getJwtTokensAfterAuthentication: " + authentication.getName());
		    try {
		        // Getting user authentication data from the database
		        var userInfoEntity = userInfoRepo.findByEmailId(authentication.getName())
		                .orElseThrow(() -> {
		                    log.error("[AuthService:userSignInAuth] User :{} not found", authentication.getName());
		                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication failed: Invalid credentials");
		                });

		        log.info("#10 USER EMAIL: " + userInfoEntity.getEmailId());
		        log.info("#20 USER PASSWORD: " + userInfoEntity.getPassword());
		        
		        String accessToken = jwtTokenGenerator.generateAccessToken(authentication);
		        String refreshToken = jwtTokenGenerator.generateRefreshToken(authentication);

		        // Save the refresh token
		        // TODO: Delete old refresh token or simply update it
		        updateUserRefreshToken(userInfoEntity, refreshToken);
		        
		        /* Get User Subscription end date*/
		        UserSubscription subscription = userSubscriptionsRepository.findByUserId(userInfoEntity.getId()).get(0);
		        String subscriptionEndDate = subscription.getSubscriptionEndDate().toString();
		        if(subscription == null) {
		        	LocalDate currentDate = LocalDate.now();
		        	subscriptionEndDate = currentDate.toString();
		        }
		        
		        log.info("#30 USER SUBSCRITION: ", subscriptionEndDate);
		        
		        // Create cookies
		        createRefreshTokenCookie(response, refreshToken);
		        
		        log.info("[AuthService:userSignInAuth] Access token for user:{}, has been generated", userInfoEntity.getUserName());
		        return AuthResponseDto.builder()
		                .accessToken(accessToken)
		                .accessTokenExpiry(15 * 60)
		                .id(userInfoEntity.getId())
		                .userName(userInfoEntity.getUserName())
		                .tokenType(TokenType.Bearer)
		                .subscripitonEndDate(subscriptionEndDate)
		                .build();
		        
		    } catch (ResponseStatusException ex) {
		        // Handle known exceptions with precise messages
		        log.error("[AuthService:userSignInAuth] Authentication error: {}", ex.getReason());
		        throw ex; // Retain the specific HTTP status and message
		    } catch (Exception e) {
		        // Handle unexpected errors
		        log.error("[AuthService:userSignInAuth] Exception while authenticating the user due to: {}", e.getMessage());
		        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred. Please try again later.");
		    }
		}

	/**
	 * Wrking copy of Login
	 * @param authentication
	 * @param response
	 * @return
	 */
    public AuthResponseDto getJwtTokensAfterAuthenticationOriginal(Authentication authentication, 
    		HttpServletResponse response) {
    	log.info("#09 AuthResponseDto getJwtTokensAfterAuthentication: " + authentication.getName());
    	try {
           
        	/* Getting user authentication data form the db. We need a user name because we are using email for authentication */
        	var userInfoEntity = userInfoRepo.findByEmailId(authentication.getName())
                    .orElseThrow(()->{
                        log.error("[AuthService:userSignInAuth] User :{} not found",authentication.getName());
                        return new ResponseStatusException(HttpStatus.NOT_FOUND,"USER NOT FOUND ");});

        	log.info("#10 USER EMAIL: " + userInfoEntity.getEmailId());
        	log.info("#20 USER PASSWORD: " + userInfoEntity.getPassword());
        	
            String accessToken = jwtTokenGenerator.generateAccessToken(authentication);
            String refreshToken = jwtTokenGenerator.generateRefreshToken(authentication);
            //Let's save the refreshToken as well
            updateUserRefreshToken(userInfoEntity, refreshToken);
           
            //Creating cookies
            createRefreshTokenCookie(response, refreshToken);
            
            log.info("[AuthService:userSignInAuth] Access token for user:{}, has been generated",userInfoEntity.getUserName());
            return  AuthResponseDto.builder()
                    .accessToken(accessToken)
                    .accessTokenExpiry(15 * 60)
                    .id(userInfoEntity.getId())
                    .userName(userInfoEntity.getUserName())
                    .tokenType(TokenType.Bearer)
//                    .refreshToken(refreshToken)
                    .build();
            
        }catch (Exception e){
            log.error("[AuthService:userSignInAuth]Exception while authenticating the user due to :"+e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Please Try Again");
        }
    }
    
    
    /**
     * Create a Refresh Token in Database during a User sign-up
     * @param userInfoEntity
     * @param refreshToken
     */
	private void createUserRefreshToken(UserInfoEntity userInfoEntity, String refreshToken) {
		var refreshTokenEntity = RefreshTokenEntity.builder()
				.user(userInfoEntity)
				.refreshToken(refreshToken)
				.revoked(false).build();

		refreshTokenRepo.save(refreshTokenEntity);
	}
	
	
	/**
     * Update a Refresh Token in the Database during a User login or reentering 
     * into a browser when access token exists (live or expired)
     * @param userInfoEntity
     * @param refreshToken
     */
	private void updateUserRefreshToken(UserInfoEntity userInfoEntity, String refreshToken) {
		
		/* Find existing user refresh token by user id */
		RefreshTokenEntity existingRefreshToken = refreshTokenRepo.findByUserId(userInfoEntity.getId()).get(0);
		
		if (existingRefreshToken == null) {
			
			var refreshTokenEntity = RefreshTokenEntity.builder()
					.user(userInfoEntity)
					.refreshToken(refreshToken)
					.revoked(false).build();
			refreshTokenRepo.save(refreshTokenEntity);
		}
		/* Update existing and save */
		existingRefreshToken.setRefreshToken(refreshToken);
		refreshTokenRepo.save(existingRefreshToken);
	}	
	
	
	/**
	 * https://dzone.com/articles/how-to-use-cookies-in-spring-boot
	 * Saving Refresh token as a cookie
	 * @param response
	 * @param refreshToken
	 * @return can be void
	 */
	private Cookie createRefreshTokenCookie(HttpServletResponse response, String refreshToken) {

		Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
		refreshTokenCookie.setHttpOnly(true);  // change to false in prod
        refreshTokenCookie.setSecure(false);   // change to true in prod
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(15 * 24 * 60 * 60 ); // in seconds
        
        response.addCookie(refreshTokenCookie);
//        response.addHeader("refresh_token", refreshToken);
        
        Cookie cookie2 = new Cookie("Cookie2", "IGotRidofMyCar");
        cookie2.setHttpOnly(true);  // change to false in prod
        cookie2.setSecure(false);   // change to true in prod
        cookie2.setMaxAge(15 * 24 * 60 * 60 ); // in seconds
        cookie2.setPath("/");
        response.addCookie(cookie2);
        
        return refreshTokenCookie;
    }
	
	
	
	
	/***
	 * This publicly open point is used to refresh user access token from existing refresh token which is in cookies.
	 * @param refreshToken
	 * @return 
	 */
	public AuthResponseDto getAccessTokenFromRequestWithCookies(String refreshToken, HttpServletResponse response) {
		
		log.info("#4 Refresh Token in: ", refreshToken);
		
	    try {
	        //Find refreshToken from database and should not be revoked : Same thing can be done through filter.  
	        var refreshTokenEntity = refreshTokenRepo.findByRefreshToken(refreshToken)
	                .filter(tokens-> !tokens.isRevoked())
	                .orElseThrow(()-> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Refresh token revoked"));
	
	        UserInfoEntity userInfoEntity = refreshTokenEntity.getUser();
	        
	        //Now create the Authentication object
	        Authentication authentication =  createAuthenticationObject(userInfoEntity);
	
	        //Use the authentication object to generate new accessToken as the Authentication object that we will have may not contain correct role. 
	        String accessToken = jwtTokenGenerator.generateAccessToken(authentication);
	        
	        /*  */
	        String newRefreshToken = jwtTokenGenerator.generateRefreshToken(authentication);
	        updateUserRefreshToken(userInfoEntity, newRefreshToken);
	        createRefreshTokenCookie(response, newRefreshToken);
	
	        return  AuthResponseDto.builder()
	                .accessToken(accessToken)
	                .accessTokenExpiry(5 * 60)
	                .id(userInfoEntity.getId())
	                .userName(userInfoEntity.getUserName())
	                .tokenType(TokenType.Bearer)
	                .build();
	        
	    } catch (Exception e) {
	        log.error("[AuthService:getAccessTokenFromRequestWithCookies] Exception while refreshing the user due to: {}", "Something wrong with refresh token");
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Problem with refresh token. Make a new login.");
		}
        
    }
	
	
	
	/**
	 * TODO: This needs to be changed that a new access token will be generated using refresh token present in cookies
	 * regardless of expiration. Request came in from the same browser then it should be generated.
	 * Thus, incoming parameter should be a refresh token from cookies.
	 * @param authorizationHeader
	 * @return
	 */
	public Object getAccessTokenUsingRefreshToken(String authorizationHeader, String refreshToken ) {
		 
        if(!authorizationHeader.startsWith(TokenType.Bearer.name())){
            return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Please verify your token type");
        }

        //final String refreshToken = authorizationHeader.substring(7);

        //Find refreshToken from database and should not be revoked : Same thing can be done through filter.  
        var refreshTokenEntity = refreshTokenRepo.findByRefreshToken(refreshToken)
                .filter(tokens-> !tokens.isRevoked())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Refresh token revoked"));

        UserInfoEntity userInfoEntity = refreshTokenEntity.getUser();
        
        //Now create the Authentication object
        Authentication authentication =  createAuthenticationObject(userInfoEntity);

        //Use the authentication object to generate new accessToken as the Authentication object that we will have may not contain correct role. 
        String accessToken = jwtTokenGenerator.generateAccessToken(authentication);

        return  AuthResponseDto.builder()
                .accessToken(accessToken)
                .accessTokenExpiry(5 * 60)
                .userName(userInfoEntity.getUserName())
                .tokenType(TokenType.Bearer)
                .build();
    }
	
	
	/**
	 * Generate an empty cookies and call for logout service to revoke refresh token in db
	 * See https://dzone.com/articles/how-to-use-cookies-in-spring-boot
	 * @param authorizationHeader
	 * @param authorization with access token, 
	 *        request to get current cookies
	 *        response to send back empty cookies
	 * @return response with empty refresh token
	 */
	public Object logoutUser(String authorizationHeader, HttpServletRequest request, HttpServletResponse response) {
		
		
		log.info("\n\n=================== #200 AuthService.logoutUser() STARTED...");
		
		/* Find current refresh token in cookies */
		Cookie[] cookies = request.getCookies();
		String activeRefreshToken = cookieService.findCookieByName(cookies, "refresh_token");
      
		log.info(" #201 Found refresh token = " + activeRefreshToken);
		
        /* Find refreshToken from database and we need a user name to generate a response with empty refresh token. */  
        var refreshTokenEntity = refreshTokenRepo.findByRefreshToken(activeRefreshToken)
                .filter(tokens-> !tokens.isRevoked())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Refresh token revoked or cannot be found"));

        UserInfoEntity userInfoEntity = refreshTokenEntity.getUser();
        
        /* Revoke refresh token in the db  */
		var storedRefreshToken = refreshTokenRepo.findByRefreshToken(activeRefreshToken)
                .map(token->{
                    token.setRevoked(true);
                    refreshTokenRepo.save(token);
                    return token;
                })
                .orElse(null);
        
        /* Create the Authentication object */
        //Authentication authentication =  createAuthenticationObject(userInfoEntity);

        /* Use the authentication object to generate new empty accessToken. */ 
        String accessToken = "LoggedOut"; // jwtTokenGenerator.generateAccessToken(authentication);

        /* Update refresh token cookie with empty value in the response */
        Cookie cookie = new Cookie("refresh_token", null);
        cookie.setMaxAge(0);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        /* add cookie to response */
        response.addCookie(cookie);
        
        log.info("#203 Added");
        
        return  AuthResponseDto.builder()
                .accessToken(accessToken)
                .accessTokenExpiry(5)
                .userName(userInfoEntity.getUserName())
                .tokenType(TokenType.Bearer)
                .build();
    }
	
	
	
	 private static Authentication createAuthenticationObject(UserInfoEntity userInfoEntity) {
         // Extract user details from UserDetailsEntity
         String username = userInfoEntity.getEmailId();
         String password = userInfoEntity.getPassword();
         String roles = userInfoEntity.getRoles();
 
         // Extract authorities from roles (comma-separated)
         String[] roleArray = roles.split(",");
         GrantedAuthority[] authorities = Arrays.stream(roleArray)
                 .map(role -> (GrantedAuthority) role::trim)
                 .toArray(GrantedAuthority[]::new);
 
         return new UsernamePasswordAuthenticationToken(username, password, Arrays.asList(authorities));
     }


	 /**
	  * Registering a New User
	  * @param userRegistrationDto
	  * @param httpServletResponse
	  * @return
	  */
		public AuthResponseDto registerUser2(UserRegistrationDto userRegistrationDto,
				HttpServletResponse response) {
			try {
				log.info("[AuthService:registerUser]User Registration Started with :::{}", userRegistrationDto);

				Optional<UserInfoEntity> user = userInfoRepo.findByEmailId(userRegistrationDto.userEmail());
				if (user.isPresent()) {
					throw new Exception("User already exists");
				}

				UserInfoEntity userDetailsEntity = userInfoMapper.convertToEntity(userRegistrationDto);
				Authentication authentication = createAuthenticationObject(userDetailsEntity);

				/* Generate a JWT token */
				String accessToken = jwtTokenGenerator.generateAccessToken(authentication);
				String refreshToken = jwtTokenGenerator.generateRefreshToken(authentication);

				UserInfoEntity savedUserDetails = userInfoRepo.save(userDetailsEntity);
				
				log.info("#6 REGISTERED USER ID IS: " + savedUserDetails.getId());
				log.info("#7 Refresh Token: " + refreshToken);
				
				createUserRefreshToken(userDetailsEntity, refreshToken);
				createRefreshTokenCookie(response, refreshToken);

				log.info("[AuthService:registerUser] User:{} Successfully registered", savedUserDetails.getUserName());
				
				return AuthResponseDto.builder()
						.accessToken(accessToken)
						.accessTokenExpiry(5 * 60)
						.id(savedUserDetails.getId())
						.userName(savedUserDetails.getUserName())
						.tokenType(TokenType.Bearer)
						.build();

			} catch (Exception e) {
				log.error("[AuthService:registerUser]Exception while registering the user due to :" + e.getMessage());
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
			}
		}
	
}
