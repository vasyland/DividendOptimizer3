package com.stock.services;


import com.stock.exceptions.DuplicatePortfolioException;
import com.stock.exceptions.UnauthorizedPortfolioAccessException;
import com.stock.model.Portfolio;
import com.stock.repositories.PortfolioRepository;
import com.stock.security.config.RSAKeyRecord;
import com.stock.security.config.jwt.JwtAccessTokenFilter;
import com.stock.security.config.jwt.JwtTokenUtils;
import com.stock.security.entity.UserInfo;
import com.stock.security.repo.UserInfoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final UserInfoRepository userInfoRepository;
    private final UserService userService;
    private JwtTokenUtils jwtTokenUtils;
    
//    @Autowired
//    public PortfolioService(PortfolioRepository portfolioRepository, UserInfoRepository userInfoRepository) {
//        this.portfolioRepository = portfolioRepository;
//        this.userInfoRepository = userInfoRepository;
//    }

    
    // List all portfolios for a user
    public List<Portfolio> getUserPortfolios(Long userId) {
        return portfolioRepository.findByUserId(userId);
    }
    
    
    /**
     * Get a specific portfolio by its ID
     * @param id
     * @return Existing Portfolio
     */
	public Optional<Portfolio> getPortfolioById(Long id) {
		return portfolioRepository.findById(id);
	}
    
    
    // Create a new portfolio for a user
    @Transactional
    public Portfolio createPortfolio(String name, double initialAmount) {
    	
    	String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
    	log.info("[PortfolioService:createPortfolio] Current Username: {}", currentUsername);
    	// Get the user ID from the current user
    	Long currentUserId = userService.getCurrentUserId();
    	log.info("[PortfolioService:editPortfolio] Current User ID: {}", currentUserId);
    	
        Optional<UserInfo> userOptional = userInfoRepository.findById(currentUserId);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found.");
        }
        UserInfo user = userOptional.get();

        // Check if portfolio name already exists for the user
        if (portfolioRepository.existsByNameAndUserId(name, currentUserId)) {
            throw new DuplicatePortfolioException("Portfolio with this name already exists.");
        }

        Portfolio portfolio = new Portfolio();
        portfolio. setUser(user);
        portfolio.setName(name);
        portfolio.setInitialAmount(new BigDecimal(initialAmount));

        return portfolioRepository.save(portfolio);
    }

    
    // Edit an existing portfolio for a user
    @Transactional
    public Portfolio editPortfolio(
    		Long portfolioId, 
    		String name, 
    		BigDecimal initialAmount) {
    	
    	String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
    	log.info("[PortfolioService:editPortfolio] Current Username: {}", currentUsername);
    	
    	// Get the user ID from the current user
    	Long currentUserId = userService.getCurrentUserId();
    	log.info("[PortfolioService:editPortfolio] Current User ID: {}", currentUserId);
    	
        Optional<Portfolio> existingPortfolio = portfolioRepository.findById(portfolioId);
        Long portfolioUserId = existingPortfolio.get().getUser().getId();
        log.info("[PortfolioService:editPortfolio] Portfolio User ID: {}", portfolioUserId);

		if (!currentUserId.equals(portfolioUserId)) {
	        throw new UnauthorizedPortfolioAccessException("Portfolio does not belong to this user.");
	    }
		
        if (existingPortfolio.isEmpty()) {
            throw new IllegalArgumentException("Portfolio not found or does not belong to this user.");
        }

        Portfolio portfolio = existingPortfolio.get();
        portfolio.setName(name);
        portfolio.setInitialAmount(initialAmount);

        return portfolioRepository.save(portfolio);
    }
    

    // Delete a portfolio along with its associated trades
    @Transactional
    public void deletePortfolio(Long portfolioId) {
    	
    	String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
    	log.info("[PortfolioService:editPortfolio] Current Username: {}", currentUsername);
    	
    	// Get the user ID from the current user
    	Long currentUserId = userService.getCurrentUserId();
    	log.info("[PortfolioService:editPortfolio] Current User ID: {}", currentUserId);
    	
        Optional<Portfolio> existingPortfolio = portfolioRepository.findById(portfolioId);
        Long portfolioUserId = existingPortfolio.get().getUser().getId();
        log.info("[PortfolioService:editPortfolio] Portfolio User ID: {}", portfolioUserId);

		if (!currentUserId.equals(portfolioUserId)) {
	        throw new UnauthorizedPortfolioAccessException("Portfolio does not belong to this user.");
	    }    	

        if (existingPortfolio.isEmpty()) {
            throw new IllegalArgumentException("Portfolio not found or does not belong to this user.");
        }

        portfolioRepository.delete(existingPortfolio.get());
    }
}
