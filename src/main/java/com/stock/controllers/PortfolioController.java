package com.stock.controllers;

import java.util.List;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import com.stock.data.PortfolioSummaryDTO;
import com.stock.model.Portfolio;
import com.stock.model.PortfolioCreateRequest;
import com.stock.model.PortfolioUpdateRequest;
import com.stock.services.HoldingsService;
import com.stock.services.PortfolioService;
import com.stock.services.PortfolioSummaryService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "https://localhost:5004")
@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
@Slf4j
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final HoldingsService holdingsService;
    private final PortfolioSummaryService portfolioSummaryService;
    
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
                portfolioRequest.getInitialCash(),
                portfolioRequest.getCurrentCash()
                );
      
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
    			p.getInitialCash(),
    			p.getCurrentCash());  

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
    
    /** Portfolio Summary: calculation of portfolio summary
     * Genarated by ChatGpt 
     * */
    @PutMapping("/{portfolioId}/summary")
	public ResponseEntity<PortfolioSummaryDTO> getPortfolioSumamry(@PathVariable("portfolioId") Long portfolioId) {
		log.info("[PortfolioController:getPortfolioSumamry] Portfolio ID: {}", portfolioId);

		PortfolioSummaryDTO p = portfolioSummaryService.calculatePortfolioSummary(portfolioId);
		
		
		return ResponseEntity.ok(p);
	}
    
}
