package com.stock.controllers;

import com.stock.model.Portfolio;
import com.stock.model.PortfolioUpdateRequest;
import com.stock.model.PortfolioCreateRequest;
import com.stock.services.PortfolioService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "https://localhost:5004")
@RestController
@RequestMapping("/api/portfolios")
@Slf4j
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }
   
    
    /**
     * Create a new Portfolio for the user (userId is now in the body)
     * @param portfolioRequest
     * @return
     */
    @CrossOrigin(origins = "https://localhost:5004")
    @PostMapping
    public ResponseEntity<?> createPortfolio(
    		@RequestBody PortfolioCreateRequest portfolioRequest,
    		BindingResult bindingResult, 
    		HttpServletResponse response) {
        
    	log.info("[PortfolioController:createPortfolio] New Portfolio: {}",portfolioRequest.getName());
    	
    	 if (bindingResult.hasErrors()) {
             List<String> errorMessage = bindingResult.getAllErrors().stream()
                     .map(DefaultMessageSourceResolvable::getDefaultMessage)
                     .toList();
             log.error("[PortfolioController:createPortfolio]Errors in the portfolio:{}", errorMessage);
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
         }
    	
    	// PortfolioService now handles portfolio creation with the data in the request body
        Portfolio portfolio = portfolioService.createPortfolio(
               
                portfolioRequest.getName(), 
                portfolioRequest.getInitialAmount());
      
        return new ResponseEntity<>(portfolio, HttpStatus.CREATED);
//        return ResponseEntity.ok(portfolio);
    }
       
    
    @CrossOrigin(origins = "https://localhost:5004")
    @GetMapping("/{userId}")
    public ResponseEntity<List<Portfolio>> getUserPortfolios(@PathVariable("userId") Long userId) {
        List<Portfolio> portfolios = portfolioService.getUserPortfolios(userId);
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
    	
    	log.info("[PortfolioController:editPortfolio] Portfolio to update: {}",p);
    
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
}
