package com.stock.security.service;


import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.stock.exceptions.UserAlreadyExistsException;
import com.stock.repositories.UserSubscriptionRepository;
import com.stock.security.config.RSAKeyRecord;
import com.stock.security.config.jwt.JwtTokenUtils;
import com.stock.security.dto.AuthResponseDto;
import com.stock.security.dto.TokenType;
import com.stock.security.dto.UserRegistrationDto;
import com.stock.security.entity.RefreshTokenEntity;
import com.stock.security.entity.UserInfo;
import com.stock.security.entity.UserSubscription;
import com.stock.security.mapper.UserInfoMapper;
import com.stock.security.repo.RefreshTokenRepo;
import com.stock.security.repo.UserInfoRepository;
import com.stock.security.util.CookieService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import org.springframework.http.ResponseCookie;


@Service
public class AuthService {
	
	private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserInfoRepository userInfoRepo;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final RefreshTokenRepo refreshTokenRepo;
    private final UserInfoMapper userInfoMapper;
    private final LogoutHandlerService logoutHandlerService;
    private final CookieService cookieService;
    private final UserSubscriptionRepository userSubscriptionsRepository;
    
    private final RSAKeyRecord rsaKeyRecord;
	private final JwtTokenUtils jwtTokenUtils;

	public AuthService(UserInfoRepository userInfoRepo, JwtTokenGenerator jwtTokenGenerator,
			RefreshTokenRepo refreshTokenRepo, UserInfoMapper userInfoMapper, LogoutHandlerService logoutHandlerService,
			CookieService cookieService, UserSubscriptionRepository userSubscriptionsRepository,
			RSAKeyRecord rsaKeyRecord, JwtTokenUtils jwtTokenUtils) {
		super();
		this.userInfoRepo = userInfoRepo;
		this.jwtTokenGenerator = jwtTokenGenerator;
		this.refreshTokenRepo = refreshTokenRepo;
		this.userInfoMapper = userInfoMapper;
		this.logoutHandlerService = logoutHandlerService;
		this.cookieService = cookieService;
		this.userSubscriptionsRepository = userSubscriptionsRepository;
		this.rsaKeyRecord = rsaKeyRecord;
		this.jwtTokenUtils = jwtTokenUtils;
	}


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
			
			
				log.info("[AuthService:registerUser]User Registration Started with :::{}", userRegistrationDto);

				Optional<UserInfo> user = userInfoRepo.findByEmailId(userRegistrationDto.userEmail());
				if (user.isPresent()) {
					throw new UserAlreadyExistsException();
				}

				UserInfo userInfo = userInfoMapper.convertToEntity(userRegistrationDto);
				Authentication authentication = createAuthenticationObject(userInfo);

				/* Generate a JWT token */
				String accessToken = jwtTokenGenerator.generateAccessToken(authentication);
				String refreshToken = jwtTokenGenerator.generateRefreshToken(authentication);

				UserInfo savedUserInfo = userInfoRepo.save(userInfo);
				
				log.info("#6 REGISTERED USER ID IS: " + savedUserInfo.getId());
				log.info("#7 Refresh Token: " + refreshToken);
				
				//Save the refreshToken into db
//				createUserRefreshToken(userInfo, refreshToken);
				
				createRefreshTokenCookie(response, refreshToken);
				
				/* User gets trial access for three days  */
				UserSubscription subscription = this.createUserTrialSubscription(savedUserInfo, 3);
				String sSubscriptionExpiry = subscription.getSubscriptionExpiry().toString();

				log.info("[AuthService:registerUser] User:{} Successfully registered", savedUserInfo.getUserName());
				
				HttpHeaders headers = new HttpHeaders();
		        
		        // Create the cookie and add it to the response header
		        headers.add("Set-Cookie", "username=john_doe; Max-Age=86400; HttpOnly; Secure; Path=/");
				
				return AuthResponseDto.builder()
						.accessToken(accessToken)
						.accessTokenExpiry(5 * 60)  // in seconds
						.id(savedUserInfo.getId())
						.userName(savedUserInfo.getEmailId())
						.tokenType(TokenType.Bearer)
						.subscriptionExpiry(sSubscriptionExpiry)
						.build();

		}    
    
		
		/** 
		 * Creating a trial subscription for a new user for n days
		 * @param id - user id gotten from db
		 * @param days - numbers of subscription days
		 * 
		 * return User Subscription
		 */
		public UserSubscription createUserTrialSubscription(UserInfo user, int days) {
			
			UserSubscription s = new UserSubscription();
			
			s.setUser(user);
			
			// Get the current date
	        LocalDate currentDate = LocalDate.now();
	        LocalDate endDate = currentDate.plusDays(days);
	        s.setSubscriptionExpiry(endDate);
	        
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
		                    log.error("[AuthService:userSignInAuth] User: {} not found", authentication.getName());
		                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication failed: Invalid credentials");
		                });

		        log.info("#10 USER EMAIL: " + userInfoEntity.getEmailId());
		        log.info("#20 USER PASSWORD: " + userInfoEntity.getPassword());
		        
		        String accessToken = jwtTokenGenerator.generateAccessToken(userInfoEntity.getEmailId());
		        
		        String refreshToken = jwtTokenGenerator.generateRefreshToken(authentication);

		        // Save the refresh token
		        // TODO: Delete old refresh token or simply update it
// march 14        updateUserRefreshToken(userInfoEntity, refreshToken);
		        
		        /* Get User Subscription end date*/
		        UserSubscription subscription = userSubscriptionsRepository.findByUserEmailId(userInfoEntity.getEmailId()).get(0);  //findByUserId(userInfoEntity.getId()).get(0);
		        String userSubscriptionExpiry = subscription.getSubscriptionExpiry().toString();
		        if(subscription == null) {
		        	LocalDate currentDate = LocalDate.now();
		        	userSubscriptionExpiry = currentDate.toString();
		        }
		        
		        log.info("#30 USER SUBSCRITION: ", userSubscriptionExpiry);
		        
		        // Create cookies
		        createRefreshTokenCookie(response, refreshToken);
		        
		        log.info("[AuthService:userSignInAuth] Access token for user:{}, has been generated", userInfoEntity.getUserName());
		        return AuthResponseDto.builder()
		                .accessToken(accessToken)
		                .accessTokenExpiry(1 * 60)  // in seconds
		                .id(userInfoEntity.getId())
		                .userName(userInfoEntity.getEmailId())
		                .tokenType(TokenType.Bearer)
		                .subscriptionExpiry(userSubscriptionExpiry)
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
     * Create a Refresh Token in Database during a User sign-up
     * @param userInfoEntity
     * @param refreshToken
     */
	private void createUserRefreshToken(UserInfo userInfoEntity, String refreshToken) {
		var refreshTokenEntity = RefreshTokenEntity.builder()
				.user(userInfoEntity)
				.refreshToken(refreshToken)
				.revoked(false).build();

		refreshTokenRepo.save(refreshTokenEntity);
	}
	
	
	
	
	/**
	 * https://dzone.com/articles/how-to-use-cookies-in-spring-boot
	 * Saving Refresh token as a cookie
	 * @param response
	 * @param refreshToken
	 * @return can be void
	 */
	private ResponseCookie createRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
	    ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", refreshToken)
	            .httpOnly(true)                    // secure, not accessible by JS
	            .secure(false)                      // must be true for HTTPS
	            .path("/")                         // valid for entire domain
	            .maxAge(Duration.ofDays(15))       // 15 days expiry
	            .sameSite("Lax")                  // required for cross-origin
	            .build();

	    // Add cookie to the response
	    response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

	    // Return the ResponseCookie object
	    return refreshTokenCookie;
	}
	
	
//	private Cookie createRefreshTokenCookieORIG(HttpServletResponse response, String refreshToken) {
//		Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
//		refreshTokenCookie.setHttpOnly(true);  // change to false in prod
//        refreshTokenCookie.setSecure(false);   // change to true in prod
//        refreshTokenCookie.setPath("/");
//        refreshTokenCookie.setMaxAge(15 * 24 * 60 * 60 ); // in seconds
//        
//        response.addCookie(refreshTokenCookie);
//        
//        return refreshTokenCookie;
//    }	
	
	
	/***
	 * This publicly open point is used to refresh user access token from existing refresh token which is in cookies.
	 * @param refreshToken
	 * @return 
	 */
	public AuthResponseDto getNewTokensFromRequestWithCookies(String refreshToken, HttpServletResponse response) {
		
		log.info("#21-Auth REFRESH TOKEN: " + refreshToken);
		log.info("#4 Refresh Token in: ", refreshToken);
		
	    try {
	    	
	    	JwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(rsaKeyRecord.rsaPublicKey()).build();
			Jwt jwtRefreshToken = jwtDecoder.decode(refreshToken);
			
			String userName = jwtTokenUtils.getUserName(jwtRefreshToken);
	    	
			// Find UserInfo 
			Optional<UserInfo> user = userInfoRepo.findByEmailId(userName);
			if (user.isEmpty()) {
				throw new Exception("User was not found to generate a new access token.");
			}
	
	        UserInfo userInfo = user.get();
	        
	        //Now create the Authentication object
	        Authentication authentication =  createAuthenticationObject(userInfo);
	
	        //Use the authentication object to generate new accessToken as the Authentication object that we will have may not contain correct role. 
	        String accessToken = jwtTokenGenerator.generateAccessToken(authentication);
	        
	        // Generate a new refresh token
	        String newRefreshToken = jwtTokenGenerator.generateRefreshToken(authentication);
	        
	        // Saving a new refresh token into db
	        // updateUserRefreshToken(userInfo, newRefreshToken);
	        
	        createRefreshTokenCookie(response, newRefreshToken);
	        
	        /* Get User Subscription end date*/
	    	LocalDate currentDate = LocalDate.now();
	    	String subscriptionExpiry = currentDate.toString();
	    	
	        UserSubscription subscription = userSubscriptionsRepository.findByUserEmailId(userName).get(0);
	        if(subscription != null) {
	        	subscriptionExpiry = subscription.getSubscriptionExpiry().toString();
	        }

	        return  AuthResponseDto.builder()
	                .accessToken(accessToken)
	                .accessTokenExpiry(1 * 60)
	                .id(userInfo.getId())
	                .userName(userName)
	                .subscriptionExpiry(subscriptionExpiry)
	                .tokenType(TokenType.Bearer)
	                .build();
	        
	    } catch (Exception e) {
	        log.error("[AuthService:getAccessTokenFromRequestWithCookies] Exception while refreshing the user due to: {}", "Something wrong with refresh token");
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Problem with refresh token. Make a new login.");
		}
        
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
//	public Object logoutUser(String authorizationHeader, HttpServletRequest request, HttpServletResponse response) {
//		
//		log.info("\n\n=================== #200 AuthService.logoutUser() STARTED...");
//		
//		/* Find current refresh token in cookies */
//		Cookie[] cookies = request.getCookies();
//		String activeRefreshToken = cookieService.findCookieByName(cookies, "refresh_token");
//      
//		log.info(" #201 Found refresh token = " + activeRefreshToken);
//		
//		//TODO: Use refresh token from cookies
//        /* Find refreshToken from database and we need a user name to generate a response with empty refresh token. */  
//        var refreshTokenEntity = refreshTokenRepo.findByRefreshToken(activeRefreshToken)
//                .filter(tokens-> !tokens.isRevoked())
//                .orElseThrow(()-> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Refresh token revoked or cannot be found"));
//
//        UserInfo userInfoEntity = refreshTokenEntity.getUser();
//        
//        /* Revoke refresh token in the db  */
//		var storedRefreshToken = refreshTokenRepo.findByRefreshToken(activeRefreshToken)
//                .map(token->{
//                    token.setRevoked(true);
//                    refreshTokenRepo.save(token);
//                    return token;
//                })
//                .orElse(null);
//        
//        /* Create the Authentication object */
//        //Authentication authentication =  createAuthenticationObject(userInfoEntity);
//
//        /* Use the authentication object to generate new empty accessToken. */ 
//        String accessToken = "LoggedOut"; // jwtTokenGenerator.generateAccessToken(authentication);
//
//        /* Update refresh token cookie with empty value in the response */
//        ResponseCookie  cookie = new Cookie("refresh_token", null);
//        cookie.setMaxAge(0);
//        cookie.setSecure(true);
//        cookie.setHttpOnly(true);
//        cookie.setPath("/");
//
//        /* add cookie to response */
//        response.addCookie(cookie);
//        
//        log.info("#203 Added");
//        
//        return  AuthResponseDto.builder()
//                .accessToken(accessToken)
//                .accessTokenExpiry(5)
//                .userName(userInfoEntity.getUserName())
//                .tokenType(TokenType.Bearer)
//                .build();
//    }
	
	
	 private static Authentication createAuthenticationObject(UserInfo userInfoEntity) {
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


//	 /**
//	  * Registering a New User
//	  * @param userRegistrationDto
//	  * @param httpServletResponse
//	  * @return
//	  */
//		public AuthResponseDto registerUser2(UserRegistrationDto userRegistrationDto,
//				HttpServletResponse response) {
//			try {
//				log.info("[AuthService:registerUser2]User Registration Started with :::{}", userRegistrationDto);
//
//				Optional<UserInfo> user = userInfoRepo.findByEmailId(userRegistrationDto.userEmail());
//				if (user.isPresent()) {
//					throw new Exception("User already exists");
//				}
//
//				UserInfo userDetailsEntity = userInfoMapper.convertToEntity(userRegistrationDto);
//				Authentication authentication = createAuthenticationObject(userDetailsEntity);
//
//				/* Generate a JWT token */
//				String accessToken = jwtTokenGenerator.generateAccessToken(authentication);
//				String refreshToken = jwtTokenGenerator.generateRefreshToken(authentication);
//
//				UserInfo savedUserDetails = userInfoRepo.save(userDetailsEntity);
//				
//				log.info("#6 REGISTERED USER ID IS: " + savedUserDetails.getId());
//				log.info("#7 Refresh Token: " + refreshToken);
//				
//				createUserRefreshToken(userDetailsEntity, refreshToken);
//				createRefreshTokenCookie(response, refreshToken);
//
//				log.info("[AuthService:registerUser] User:{} Successfully registered", savedUserDetails.getUserName());
//				
//				return AuthResponseDto.builder()
//						.accessToken(accessToken)
//						.accessTokenExpiry(5 * 60)
//						.id(savedUserDetails.getId())
//						.userName(savedUserDetails.getUserName())
//						.tokenType(TokenType.Bearer)
//						.build();
//
//			} catch (Exception e) {
//				log.error("[AuthService:registerUser]Exception while registering the user due to :" + e.getMessage());
//				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
//			}
//		}
	
}




///**
// * Wrking copy of Login
// * @param authentication
// * @param response
// * @return
// */
//public AuthResponseDto getJwtTokensAfterAuthenticationOriginal(Authentication authentication, 
//		HttpServletResponse response) {
//	log.info("#09 AuthResponseDto getJwtTokensAfterAuthentication: " + authentication.getName());
//	try {
//       
//    	/* Getting user authentication data form the db. We need a user name because we are using email for authentication */
//    	var userInfoEntity = userInfoRepo.findByEmailId(authentication.getName())
//                .orElseThrow(()->{
//                    log.error("[AuthService:userSignInAuth] User :{} not found",authentication.getName());
//                    return new ResponseStatusException(HttpStatus.NOT_FOUND,"USER NOT FOUND ");});
//
//    	log.info("#10 USER EMAIL: " + userInfoEntity.getEmailId());
//    	log.info("#20 USER PASSWORD: " + userInfoEntity.getPassword());
//    	
//        String accessToken = jwtTokenGenerator.generateAccessToken(authentication);
//        String refreshToken = jwtTokenGenerator.generateRefreshToken(authentication);
//        //Let's save the refreshToken as well
//        updateUserRefreshToken(userInfoEntity, refreshToken);
//       
//        //Creating cookies
//        createRefreshTokenCookie(response, refreshToken);
//        
//        log.info("[AuthService:userSignInAuth] Access token for user:{}, has been generated",userInfoEntity.getUserName());
//        return  AuthResponseDto.builder()
//                .accessToken(accessToken)
//                .accessTokenExpiry(15 * 60)
//                .id(userInfoEntity.getId())
//                .userName(userInfoEntity.getUserName())
//                .tokenType(TokenType.Bearer)
////                .refreshToken(refreshToken)
//                .build();
//        
//    }catch (Exception e){
//        log.error("[AuthService:userSignInAuth]Exception while authenticating the user due to :"+e.getMessage());
//        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Please Try Again");
//    }
//}


///**
// * TODO: This needs to be changed that a new access token will be generated using refresh token present in cookies
// * regardless of expiration. Request came in from the same browser then it should be generated.
// * Thus, incoming parameter should be a refresh token from cookies.
// * @param authorizationHeader
// * @return
// */
//public Object getAccessTokenUsingRefreshToken(String authorizationHeader, String refreshToken ) {
//	 
//    if(!authorizationHeader.startsWith(TokenType.Bearer.name())){
//        return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Please verify your token type");
//    }
//
//    //final String refreshToken = authorizationHeader.substring(7);
//
//    //Find refreshToken from database and should not be revoked : Same thing can be done through filter.  
//    var refreshTokenEntity = refreshTokenRepo.findByRefreshToken(refreshToken)
//            .filter(tokens-> !tokens.isRevoked())
//            .orElseThrow(()-> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Refresh token revoked"));
//
//    UserInfo userInfoEntity = refreshTokenEntity.getUser();
//    
//    //Now create the Authentication object
//    Authentication authentication =  createAuthenticationObject(userInfoEntity);
//
//    //Use the authentication object to generate new accessToken as the Authentication object that we will have may not contain correct role. 
//    String accessToken = jwtTokenGenerator.generateAccessToken(authentication);
//
//    return  AuthResponseDto.builder()
//            .accessToken(accessToken)
//            .accessTokenExpiry(5 * 60)
//            .userName(userInfoEntity.getUserName())
//            .tokenType(TokenType.Bearer)
//            .build();
//}


///**
// * Update a Refresh Token in the Database during a User login or reentering 
// * into a browser when access token exists (live or expired)
// * @param userInfoEntity
// * @param refreshToken
// */
//private void updateUserRefreshToken(UserInfo userInfoEntity, String refreshToken) {
//	
//	/* Find existing user refresh token by user id */
//	RefreshTokenEntity existingRefreshToken = refreshTokenRepo.findByUserId(userInfoEntity.getId()).get(0);
//	
//	if (existingRefreshToken == null) {
//		
//		var refreshTokenEntity = RefreshTokenEntity.builder()
//				.user(userInfoEntity)
//				.refreshToken(refreshToken)
//				.revoked(false).build();
//		refreshTokenRepo.save(refreshTokenEntity);
//	}
//	/* Update existing and save */
////	existingRefreshToken.setRefreshToken(refreshToken);
//	
//	refreshTokenRepo.save(existingRefreshToken);
//}