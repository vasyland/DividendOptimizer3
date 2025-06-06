package com.stock.services;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stock.exceptions.DuplicatePortfolioException;
import com.stock.exceptions.UnauthorizedPortfolioAccessException;
import com.stock.model.Portfolio;
import com.stock.model.PortfolioSummary;
import com.stock.repositories.HoldingRepository;
import com.stock.repositories.PortfolioRepository;
import com.stock.repositories.PortfolioSummaryRepository;
import com.stock.repositories.TransactionRepository;
import com.stock.security.config.jwt.JwtTokenUtils;
import com.stock.security.entity.UserInfo;
import com.stock.security.repo.UserInfoRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PortfolioService {
	
	private static final Logger log = LoggerFactory.getLogger(PortfolioService.class);

    private final PortfolioRepository portfolioRepository;
    private final PortfolioSummaryRepository portfolioSummaryRepository;
    private final HoldingRepository holdingRepository;
    private final TransactionRepository transactionRepository;
    private final UserInfoRepository userInfoRepository;
    private final UserService userService;
    private JwtTokenUtils jwtTokenUtils;
    
    
    
public PortfolioService(PortfolioRepository portfolioRepository,
			PortfolioSummaryRepository portfolioSummaryRepository, HoldingRepository holdingRepository,
			TransactionRepository transactionRepository, UserInfoRepository userInfoRepository, UserService userService,
			JwtTokenUtils jwtTokenUtils) {
		super();
		this.portfolioRepository = portfolioRepository;
		this.portfolioSummaryRepository = portfolioSummaryRepository;
		this.holdingRepository = holdingRepository;
		this.transactionRepository = transactionRepository;
		this.userInfoRepository = userInfoRepository;
		this.userService = userService;
		this.jwtTokenUtils = jwtTokenUtils;
	}


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
    public Portfolio createPortfolio(String name, 
    		double initialCash, 
    		double currentCash) {
    	
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
        portfolio.setInitialCash(new BigDecimal(initialCash));
        portfolio.setCurrentCash(new BigDecimal(currentCash));
        
        Portfolio savedPortfolio = portfolioRepository.save(portfolio); // generates ID
        
     // Create corresponding PortfolioSummary
        PortfolioSummary summary = new PortfolioSummary();
        summary.setPortfolio(savedPortfolio);
//        summary.setPortfolioId(savedPortfolio.getId());
        summary.setCash(BigDecimal.valueOf(currentCash)); // Start with currentCash
        summary.setRealizedPnL(BigDecimal.ZERO);
        summary.setUnrealizedPnL(BigDecimal.ZERO);
        summary.setTotalMarketValue(BigDecimal.ZERO);
        summary.setTotalValue(BigDecimal.valueOf(currentCash)); // Initial total value
        summary.setUpdatedAt(LocalDateTime.now());

        portfolioSummaryRepository.save(summary);
        
        return savedPortfolio;
    }

    
    // Edit an existing portfolio for a user
    @Transactional
    public Portfolio editPortfolio(
    		Long portfolioId, 
    		String name, 
    		BigDecimal initialCash,
    		BigDecimal currentCash) {
    	
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
        portfolio.setInitialCash(initialCash);
        portfolio.setCurrentCash(currentCash);

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

        // Delete corresponding trades and portfolio summary
        portfolioSummaryRepository.deleteByPortfolioId(portfolioId);
       //Delete holdings associated with this portfolio
        holdingRepository.deleteByPortfolioId(portfolioId);
        // Delete all transactions associated with this portfolio
        transactionRepository.deleteByPortfolioId(portfolioId);
        // Finally, delete the portfolio itself                
        portfolioRepository.delete(existingPortfolio.get());
    }
}
