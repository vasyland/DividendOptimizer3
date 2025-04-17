package com.stock.security.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author atquil
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDto {
	
	@JsonProperty("id")
	private Long id;

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("access_token_expiry")
    private int accessTokenExpiry;

    @JsonProperty("token_type")
    private TokenType tokenType;

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("subscriptionExpiry")
    private String subscriptionExpiry;

//    @JsonProperty("refresh_token")
//    private String refreshToken;
//    
//    @JsonProperty("refresh_token_expiry")
//    private int refreshTokenExpiry;
}