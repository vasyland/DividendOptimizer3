package com.stock.security.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "REFRESH_TOKENS")
public class RefreshTokenEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "REFRESH_TOKEN", nullable = false, length = 10000)
    private String refreshToken;

    @Column(name = "REVOKED")
    private boolean revoked;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserInfo user;

    public RefreshTokenEntity() {
    }

    public RefreshTokenEntity(Long id, String refreshToken, boolean revoked, UserInfo user) {
        this.id = id;
        this.refreshToken = refreshToken;
        this.revoked = revoked;
        this.user = user;
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    // ✅ Manual Builder
    public static class Builder {
        private Long id;
        private String refreshToken;
        private boolean revoked;
        private UserInfo user;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public Builder revoked(boolean revoked) {
            this.revoked = revoked;
            return this;
        }

        public Builder user(UserInfo user) {
            this.user = user;
            return this;
        }

        public RefreshTokenEntity build() {
            return new RefreshTokenEntity(id, refreshToken, revoked, user);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "RefreshTokenEntity{" +
                "id=" + id +
                ", refreshToken='" + refreshToken + '\'' +
                ", revoked=" + revoked +
                ", user=" + (user != null ? user.getId() : null) +
                '}';
    }
}
