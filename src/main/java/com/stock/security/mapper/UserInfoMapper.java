package com.stock.security.mapper;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.stock.security.dto.UserRegistrationDto;
import com.stock.security.entity.UserInfoEntity;

import lombok.RequiredArgsConstructor;

/**
 * Transferring data from a New User object into UserInfoEntity for further processing
 */
@Component
@RequiredArgsConstructor
public class UserInfoMapper {

    private final PasswordEncoder passwordEncoder;
    
    public UserInfoEntity convertToEntity(UserRegistrationDto userRegistrationDto) {
    	
        UserInfoEntity userInfoEntity = new UserInfoEntity();
        userInfoEntity.setUserName(userRegistrationDto.userName());
        userInfoEntity.setEmailId(userRegistrationDto.userEmail());
        userInfoEntity.setMobileNumber(userRegistrationDto.userMobileNo());
        userInfoEntity.setRoles(userRegistrationDto.userRole());
        userInfoEntity.setPassword(passwordEncoder.encode(userRegistrationDto.userPassword()));
        
        return userInfoEntity;
    }
}

