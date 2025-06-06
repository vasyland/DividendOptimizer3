package com.stock.security.config.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.stock.security.entity.UserInfo;

import lombok.RequiredArgsConstructor;

//import lombok.RequiredArgsConstructor;

/**
 * @author atquil
 * This is Authentication Object
 */
public class UserInfoConfig implements UserDetails {

	private static final long serialVersionUID = 1L;
	
	private final UserInfo userInfoEntity;
	
	public UserInfoConfig(UserInfo userInfoEntity) {
		super();
		this.userInfoEntity = userInfoEntity;
	}

	/**
	 * Extreacting User ROles as a Collection
	 */
	@Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
    	
    	List<? extends GrantedAuthority> r = new ArrayList<>();
    	
    	r = Arrays
                .stream(userInfoEntity.getRoles().split(","))
                .map(SimpleGrantedAuthority::new)
                .toList();
    	
    	System.out.println("=================== AUTHORITIES OR ROLES ========================");
    	r.forEach(t -> System.out.println(t.getAuthority()));
    	
    	return r;
//        return Arrays
//                .stream(userInfoEntity
//                        .getRoles()
//                        .split(","))
//                .map(SimpleGrantedAuthority::new)
//                .toList();
    }

    @Override
    public String getPassword() {
        return userInfoEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return userInfoEntity.getEmailId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
