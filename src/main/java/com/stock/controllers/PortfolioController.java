package com.stock.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stock.data.HoldingDto2;
import com.stock.data.PortfolioSummaryDto2;
import com.stock.model.Portfolio;
import com.stock.model.PortfolioCreateRequest;
import com.stock.model.PortfolioUpdateRequest;
import com.stock.services.HoldingService2;
import com.stock.services.HoldingsService;
import com.stock.services.PortfolioService;
import com.stock.services.PortfolioService2;
import com.stock.services.UserService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "https://localhost:5004")
@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {
	
	private static final Logger log = LoggerFactory.getLogger(PortfolioController.class);

    private final PortfolioService portfolioService;
    private final HoldingsService holdingsService;
    private final HoldingService2 holdingService2;
    private final UserService userService;
    private final PortfolioService2 portfolioService2;

	
	public PortfolioController(PortfolioService portfolioService, HoldingsService holdingsService,
			HoldingService2 holdingService2, UserService userService, PortfolioService2 portfolioService2) {
		super();
		this.portfolioService = portfolioService;
		this.holdingsService = holdingsService;
		this.holdingService2 = holdingService2;
		this.userService = userService;
		this.portfolioService2 = portfolioService2;
	}


	/**
     * Create a new Portfolio for the user (userId is now in the body)
     * @param portfolioRequest
     * @return
     */
    @CrossOrigin(origins = "https://localhost:5004")
    @PostMapping
    public ResponseEntity<?> createPortfolio(
            @Valid @RequestBody PortfolioCreateRequest portfolioRequest,
            BindingResult bindingResult) {

        log.info("[PortfolioController:createPortfolio] New Portfolio: {}", portfolioRequest.getName());

        if (bindingResult.hasErrors()) {
            List<String> errorMessage = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            log.error("[PortfolioController:createPortfolio] Errors in the portfolio: {}", errorMessage);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }

        Portfolio portfolio = portfolioService.createPortfolio(
                portfolioRequest.getName(),
                portfolioRequest.getInitialAmount()
        );

        return new ResponseEntity<>(portfolio, HttpStatus.CREATED);
    }
       
      
	/**
	 * Get all Portfolios for the user This method returns PortfolioDto objects
	 * instead of Portfolio entities to avoid exposing sensitive data.
	 * 
	 * @return List of PortfolioDto
	 */
    @CrossOrigin(origins = "https://localhost:5004")
    @GetMapping("/user")
    public ResponseEntity<List<PortfolioSummaryDto2>> getUserPortfoliosData2(Authentication authentication) {
    	
    	// 1. Extract email from JWT (sub claim)
        String email = authentication.getName(); // by default, Spring maps "sub" to getName()
        log.info("[PortfolioController.getUserPortfolios] Email from Authentication token: " + email);
        
        Long userId = userService.getCurrentUserId();
        log.info("[PortfolioController.getUserPortfoliosA] Authentication User Id: " + userId);
        
        List<PortfolioSummaryDto2> portfolios = portfolioService2.getUserPortfoliosData(userId);
        return new ResponseEntity<>(portfolios, HttpStatus.OK);
    }   
    
    
    /**
     * Edit an existing portfolio for the user (portfolioId is in the body)
     * @param request with new data
     * @param bindingResult
     * @param response
     * @return
     */
    @PutMapping
    public ResponseEntity<?> editPortfolio(@RequestBody PortfolioUpdateRequest p,
    		BindingResult bindingResult) {
    	
    	if (p.getId() == null) {
            throw new IllegalArgumentException("#299 [PortfolioController:editPortfolio]Missing portfolio ID");
        }
    	
    	log.info("#300 [PortfolioController:editPortfolio] Portfolio to update: {}", p.getId());
    
    	if (bindingResult.hasErrors()) {
            List<String> errorMessage = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            log.error("[PortfolioController:editPortfolio] Errors in the portfolio: {}", errorMessage);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }
    	    	
    	// PortfolioService handles portfolios editing with the data in the request body
    	Portfolio updatedPortfolio = portfolioService.editPortfolio(
    			p.getId(),
    			p.getName(), 
    			p.getInitialAmount());  

        return ResponseEntity.ok(updatedPortfolio);
    }
    
    
//	final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
//	log.info("[PortfolioController:editPortfolio] AuthHeader: {}", authHeader);
	
    
    // Delete a Portfolio along with its associated trades (portfolioId in the body)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePortfolio(@PathVariable("id") Long id) {
        portfolioService.deletePortfolio(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    
     /** Portfolio Holdings: re-calculation of entire portfolio */
    @PutMapping("/{portfolioId}/recalculate")
	public ResponseEntity<Void> recalculatePortfolioHoldings(@PathVariable("portfolioId") Long portfolioId) {
		log.info("[PortfolioController:recalculatePortfolioHoldings] Portfolio ID: {}", portfolioId);

		holdingsService.recalculateHoldingsForPortfolio(portfolioId);

		return new ResponseEntity<>(HttpStatus.OK);
	}

//    @GetMapping("/{portfolioId}/holdings")
//	public ResponseEntity<?> getPortfolioHoldings(@PathVariable("portfolioId") Long portfolioId) {
//		log.info("[PortfolioController:recalculatePortfolioHoldings] Portfolio ID: {}", portfolioId);
//		List<HoldingDto> holdings = holdingsService.getPortfolioHoldings(portfolioId, true);
//		return ResponseEntity.ok(holdings);
//	}    
    

    @GetMapping("/{portfolioId}/holdings")
 	public ResponseEntity<?> getPortfolioHoldings(@PathVariable("portfolioId") Long portfolioId) {
 		log.info("[PortfolioController:getPortfolioHoldings] Portfolio ID: {}", portfolioId);
 		return ResponseEntity.ok(holdingService2.getHoldingsFromTransactions(portfolioId));
 	}  
    
}
