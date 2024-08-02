package com.stock.security.service;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.stock.security.dto.AuthResponseDto;
import com.stock.security.dto.UserRegistrationDto;
import com.stock.security.entity.RefreshTokenEntity;
import com.stock.security.entity.UserInfoEntity;
import com.stock.security.mapper.UserInfoMapper;
import com.stock.security.repo.RefreshTokenRepo;
import com.stock.security.repo.UserInfoRepo;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.stock.security.dto.TokenType;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserInfoRepo userInfoRepo;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final RefreshTokenRepo refreshTokenRepo;
    private final UserInfoMapper userInfoMapper;
    
    public AuthResponseDto getJwtTokensAfterAuthentication(Authentication authentication, HttpServletResponse response) {
       
    	try {
           
        	/* Getting user authentication data form the db. We need a user name because we are using email for authentication */
        	var userInfoEntity = userInfoRepo.findByEmailId(authentication.getName())
                    .orElseThrow(()->{
                        log.error("[AuthService:userSignInAuth] User :{} not found",authentication.getName());
                        return new ResponseStatusException(HttpStatus.NOT_FOUND,"USER NOT FOUND ");});

        	log.info("#10 USER EMAIL: " + userInfoEntity.getEmailId());
        	log.info("#20 USER PASSWORD: " + userInfoEntity.getPassword());
        	
        	
            String accessToken = jwtTokenGenerator.generateAccessToken(authentication);
            String refreshToken = jwtTokenGenerator.generateRefreshToken(authentication); //TODO: Remove AAA

            //Let's save the refreshToken as well
            saveUserRefreshToken(userInfoEntity, refreshToken);
           
            //Creating the cookie
            creatRefreshTokenCookie(response, refreshToken);
            
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
     * Saving Refresh Token into Database
     * @param userInfoEntity
     * @param refreshToken
     */
	private void saveUserRefreshToken(UserInfoEntity userInfoEntity, String refreshToken) {
		var refreshTokenEntity = RefreshTokenEntity.builder()
				.user(userInfoEntity)
				.refreshToken(refreshToken)
				.revoked(false).build();

		refreshTokenRepo.save(refreshTokenEntity);
	}
	
	
	/**
	 * Saving Refresh token as a cookie
	 * @param response
	 * @param refreshToken
	 * @return can be void
	 */
	private Cookie creatRefreshTokenCookie(HttpServletResponse response, String refreshToken) {

		Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
		refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setMaxAge(15 * 24 * 60 * 60 ); // in seconds
        
        response.addCookie(refreshTokenCookie);
        return refreshTokenCookie;
    }
	
	
	/**
	 * 
	 * @param authorizationHeader
	 * @return
	 */
	public Object getAccessTokenUsingRefreshToken(String authorizationHeader) {
		 
        if(!authorizationHeader.startsWith(TokenType.Bearer.name())){
            return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Please verify your token type");
        }

        final String refreshToken = authorizationHeader.substring(7);

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
		public AuthResponseDto registerUser(UserRegistrationDto userRegistrationDto,
				HttpServletResponse httpServletResponse) {
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
				saveUserRefreshToken(userDetailsEntity, refreshToken);

				creatRefreshTokenCookie(httpServletResponse, refreshToken);

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
