package com.stock.security.util;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.stock.security.entity.UserInfoEntity;
import com.stock.security.repo.UserInfoRepo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class InitialUserInfo implements CommandLineRunner {
	
	private final UserInfoRepo userInfoRepo;
    private final PasswordEncoder passwordEncoder;

	@Override
    public void run(String... args) throws Exception {
    	
        UserInfoEntity paidca = new UserInfoEntity();
        paidca.setUserName("paidca");
        paidca.setMobileNumber("9991114444");
        paidca.setPassword(passwordEncoder.encode("password"));
        paidca.setRoles("ROLE_PAIDCA");
        paidca.setEmailId("manager@gmail.com");

        UserInfoEntity paidus = new UserInfoEntity();
        paidus.setUserName("paidus");
        paidus.setMobileNumber("6475827412");
        paidus.setPassword(passwordEncoder.encode("password"));
        paidus.setRoles("ROLE_PAIDUS");
        paidus.setEmailId("admin@gmail.com");

        UserInfoEntity user = new UserInfoEntity();
        user.setUserName("User");
        user.setMobileNumber("2508507777");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRoles("ROLE_USER");
        user.setEmailId("user@gmail.com");
        
//        userInfoRepo.saveAll(List.of(paidca, paidus, user));
        
//        userInfoRepo.save(manager);
//        userInfoRepo.save(admin);
//        userInfoRepo.save(user);
    }

}
