package com.stock.security.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author atquil
 */
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
    
	public AuthResponseDto() {
	}

	public AuthResponseDto(Long id, String accessToken, int accessTokenExpiry, TokenType tokenType, String userName,
			String subscriptionExpiry) {
		super();
		this.id = id;
		this.accessToken = accessToken;
		this.accessTokenExpiry = accessTokenExpiry;
		this.tokenType = tokenType;
		this.userName = userName;
		this.subscriptionExpiry = subscriptionExpiry;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public int getAccessTokenExpiry() {
		return accessTokenExpiry;
	}

	public void setAccessTokenExpiry(int accessTokenExpiry) {
		this.accessTokenExpiry = accessTokenExpiry;
	}

	public TokenType getTokenType() {
		return tokenType;
	}

	public void setTokenType(TokenType tokenType) {
		this.tokenType = tokenType;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getSubscriptionExpiry() {
		return subscriptionExpiry;
	}

	public void setSubscriptionExpiry(String subscriptionExpiry) {
		this.subscriptionExpiry = subscriptionExpiry;
	}

	// ✅ Manual Builder
    public static class Builder {
        private Long id;
        private String accessToken;
        private int accessTokenExpiry;
        private TokenType tokenType;
        private String userName;
        private String subscriptionExpiry;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public Builder accessTokenExpiry(int accessTokenExpiry) {
            this.accessTokenExpiry = accessTokenExpiry;
            return this;
        }

        public Builder tokenType(TokenType tokenType) {
            this.tokenType = tokenType;
            return this;
        }

        public Builder userName(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder subscriptionExpiry(String subscriptionExpiry) {
            this.subscriptionExpiry = subscriptionExpiry;
            return this;
        }

        public AuthResponseDto build() {
            return new AuthResponseDto(id, accessToken, accessTokenExpiry,
                    tokenType, userName, subscriptionExpiry);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

}