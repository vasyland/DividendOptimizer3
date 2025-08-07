package com.stock.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.stock.data.HoldingDto;
import com.stock.data.PortfolioSummaryDTO;
import com.stock.data.PortfolioUnrealizedPnLDto;
import com.stock.model.FmpCurrentPriceProjection;
import com.stock.model.Holding;
import com.stock.model.Portfolio;
import com.stock.model.PortfolioDto;
import com.stock.model.Transaction;
import com.stock.model.TransactionType;
import com.stock.repositories.FmpCurrentPriceRepository;
import com.stock.repositories.HoldingRepository;
import com.stock.repositories.PortfolioRepository;
import com.stock.repositories.TransactionRepository;

@Service
public class PortfolioSummaryService {
	
	private static final Logger log = LoggerFactory.getLogger(PortfolioSummaryService.class);

	private PortfolioRepository portfolioRepository;
	private TransactionRepository transactionRepository;
	private HoldingRepository holdingRepository;
	private final FmpCurrentPriceRepository fmpCurrentPriceRepository;

	public PortfolioSummaryService(PortfolioRepository portfolioRepository, TransactionRepository transactionRepository,
			HoldingRepository holdingRepository, 
			FmpCurrentPriceRepository fmpCurrentPriceRepository) {
		super();
		this.portfolioRepository = portfolioRepository;
		this.transactionRepository = transactionRepository;
		this.holdingRepository = holdingRepository;
		this.fmpCurrentPriceRepository = fmpCurrentPriceRepository;
	}

	public PortfolioSummaryDTO calculatePortfolioSummary(Long portfolioId) {

		Portfolio portfolio = portfolioRepository.findById(portfolioId)
				.orElseThrow(() -> new RuntimeException("Portfolio not found"));

		BigDecimal currentCash = portfolio.getCurrentCash();

		log.info("#1 [PortfolioSummaryService:calculatePortfolioSummary] Current Cash: {}", currentCash);

		List<Transaction> transactions = transactionRepository.findByPortfolioId(portfolioId);

		List<Holding> holdings = holdingRepository.findByPortfolioId(portfolioId);

		BigDecimal realizedPnL = BigDecimal.ZERO;
		BigDecimal unrealizedPnL = BigDecimal.ZERO;
		BigDecimal totalMarketValue = BigDecimal.ZERO;

		// Group all buy transactions by symbol to estimate avg cost for realized PnL calculation
		Map<String, BigDecimal> avgCosts = holdings.stream()
				.collect(Collectors.toMap(Holding::getSymbol, Holding::getAvgCostPerShare));

		log.info("#2 [PortfolioSummaryService:calculatePortfolioSummary] Holdings size: {}", holdings.size());
		// Compute realized PnL
		for (Transaction tx : transactions) {
			if (tx.getTransactionType() == TransactionType.SELL) {
				BigDecimal proceeds = tx.getPrice().multiply(BigDecimal.valueOf(tx.getShares()))
						.subtract(tx.getCommissions());
				BigDecimal costBasis = avgCosts.getOrDefault(tx.getSymbol(), BigDecimal.ZERO)
						.multiply(BigDecimal.valueOf(tx.getShares()));
				realizedPnL = realizedPnL.add(proceeds.subtract(costBasis));
			}
		}

		log.info("#3 [PortfolioSummaryService:calculatePortfolioSummary] Realized PnL: {}", realizedPnL);

		// Compute unrealized PnL and market value
		for (Holding h : holdings) {
			
			FmpCurrentPriceProjection currentPriceData = fmpCurrentPriceRepository.findBySymbol(h.getSymbol()).get();
			
			if (currentPriceData != null) {
				
				BigDecimal marketPrice = currentPriceData.getPrice();
				BigDecimal marketValue = marketPrice.multiply(BigDecimal.valueOf(h.getShares()));
				totalMarketValue = totalMarketValue.add(marketValue);

				BigDecimal bookCost = h.getBookCost();
				unrealizedPnL = unrealizedPnL.add(marketValue.subtract(bookCost));
			}
		}

		BigDecimal totalValue = currentCash.add(totalMarketValue);
		log.info("#4 [PortfolioSummaryService:calculatePortfolioSummary] Total Market Value: {}", totalMarketValue);

		PortfolioSummaryDTO ps = new PortfolioSummaryDTO();
		ps.setPortfolioId(portfolioId);
		ps.setCash(currentCash.setScale(2, RoundingMode.HALF_UP));
		ps.setMarketValue(totalMarketValue.setScale(2, RoundingMode.HALF_UP));
		ps.setTotalValue(totalValue.setScale(2, RoundingMode.HALF_UP));
		ps.setRealizedPnL(realizedPnL.setScale(2, RoundingMode.HALF_UP));
		ps.setUnrealizedPnL(unrealizedPnL.setScale(2, RoundingMode.HALF_UP));
		log.info("#5 [PortfolioSummaryService:calculatePortfolioSummary] Portfolio Summary: {}", ps);

		return ps;
	}

	public void updatePortfolioCash(Long portfolioId) {
		List<Transaction> transactions = transactionRepository.findByPortfolioId(portfolioId);

		BigDecimal updatedCash = BigDecimal.ZERO;

		for (Transaction tx : transactions) {
			BigDecimal totalAmount = tx.getPrice().multiply(BigDecimal.valueOf(tx.getShares()))
					.add(tx.getCommissions());
			if (tx.getTransactionType() == TransactionType.BUY) {
				updatedCash = updatedCash.subtract(totalAmount);
			} else if (tx.getTransactionType() == TransactionType.SELL) {
				BigDecimal proceeds = tx.getPrice().multiply(BigDecimal.valueOf(tx.getShares()))
						.subtract(tx.getCommissions());
				updatedCash = updatedCash.add(proceeds);
			}
		}

		Portfolio portfolio = portfolioRepository.findById(portfolioId)
				.orElseThrow(() -> new RuntimeException("Portfolio not found"));
		portfolio.setCurrentCash(portfolio.getInitialCash().add(updatedCash));
		portfolioRepository.save(portfolio);
	}

	
	/**
	 * This method retrieves the portfolio data for a given user ID. It calculates
	 * the unrealized and realized PnL for each holding, and summarizes the total
	 * PnL by portfolio.
	 *
	 * @param userId The ID of the user whose portfolio data is to be retrieved.
	 * @return A list of PortfolioDto objects containing the portfolio data.
	 */
	public List<PortfolioDto> getUserPortfoliosData(long userId) {
		
		// Get all portfolios for the user
		List<Portfolio> portfolioList = portfolioRepository.findByUserId(userId);
		
		if (portfolioList.isEmpty()) {
			log.warn("[PortfolioSummaryService:getUserPortfoliosData] No portfolios found for user ID: {}", userId);
			return List.of(); // Return an empty list if no portfolios found
		}
		
		//Loop through each portfolio and calculate its summary
		List<PortfolioDto> portfolioDtoList = new ArrayList<>();
		
		for (Portfolio p: portfolioList) {
			
			BigDecimal portfolioBookCost = BigDecimal.ZERO;
			BigDecimal realizedPnL = BigDecimal.ZERO;
			BigDecimal unrealizedPnL = BigDecimal.ZERO;
			BigDecimal marketValue = BigDecimal.ZERO;
			int numberOfholdings = 0;
			
			var portfolioHoldings = getPortfolioHoldingsData(p.getId());
			for (HoldingDto h: portfolioHoldings) {
				portfolioBookCost.add(h.getBookCost() != null ? h.getBookCost() : BigDecimal.ZERO);
				realizedPnL = realizedPnL.add(h.getRealizedPnL() != null ? h.getRealizedPnL() : BigDecimal.ZERO);
				unrealizedPnL = unrealizedPnL.add(h.getUnrealizedPnL() != null ? h.getUnrealizedPnL() : BigDecimal.ZERO);
				marketValue = marketValue.add(h.getMarketValue() != null ? h.getMarketValue() : BigDecimal.ZERO);
				numberOfholdings++;
			}
			
			// Calculate total portfolio state = current cash + current cost + PnL
			BigDecimal initialCash = p.getInitialCash() != null ? p.getInitialCash() : BigDecimal.ZERO;
			BigDecimal currentCash = p.getCurrentCash() != null ? p.getCurrentCash() : BigDecimal.ZERO;
			
			BigDecimal totalValue = initialCash.add(currentCash).add(unrealizedPnL).add(marketValue).add(realizedPnL);
			BigDecimal pnl = totalValue.subtract(initialCash);
			
			BigDecimal pnlPercent = BigDecimal.ZERO;
			if (initialCash.compareTo(BigDecimal.ZERO) > 0) {
			    pnlPercent = pnl.divide(initialCash, 4, RoundingMode.HALF_UP)
			                    .multiply(BigDecimal.valueOf(100))
			                    .setScale(2, RoundingMode.HALF_UP);
			}
			
			var portfolioDto = new PortfolioDto();
			portfolioDto.setId(p.getId());
			portfolioDto.setUserId(p.getUser().getId());
			portfolioDto.setName(p.getName());
			portfolioDto.setInitialCash(initialCash);
			portfolioDto.setCurrentCash(currentCash);
			portfolioDto.setCurrentCost(marketValue);
			portfolioDto.setRealizedPnL(realizedPnL);
			portfolioDto.setUnrealizedPnL(unrealizedPnL);
			portfolioDto.setTotalValue(totalValue);
			portfolioDto.setPnl(pnl.setScale(2, RoundingMode.HALF_UP));
			portfolioDto.setPnlPercent(pnlPercent);
			portfolioDto.setNumberOfholdings(numberOfholdings);
			portfolioDto.setCreatedAt(p.getCreatedAt());
			portfolioDto.setUpdatedAt(p.getUpdatedAt());
			portfolioDtoList.add(portfolioDto);
			log.info("[PortfolioSummaryService:getUserPortfoliosData] Portfolio ID: {}, Name: {}, Initial Cash: {}, "
					+ "Current Cash: {}, Current Cost: {}, Realized PnL: {}, Unrealized PnL: {}, Total Value: {}, "
					+ "Number of Holdings: {}, PnL: {}, PnL Percent: {}", portfolioDto.getId(), portfolioDto.getName(),
					portfolioDto.getInitialCash(), portfolioDto.getCurrentCash(), portfolioDto.getCurrentCost(),
					portfolioDto.getRealizedPnL(), portfolioDto.getUnrealizedPnL(), portfolioDto.getTotalValue(),
					portfolioDto.getNumberOfholdings(), portfolioDto.getPnl(), portfolioDto.getPnlPercent());
			  
		}
		
		return portfolioDtoList;
	}
	
	
	
	/**
	 * This method retrieves the portfolio data for a given user ID.
	 * It calculates the unrealized and realized PnL for each holding,
	 * and summarizes the total PnL by portfolio.
	 *
	 * @param userId The ID of the user whose portfolio data is to be retrieved.
	 * @return A list of PortfolioDto objects containing the portfolio data.
	 */
	public List<PortfolioDto> getUserPortfoliosDataORIG(long userId) {

		// Get a list of all user holdings
		List<Holding> holdings = holdingRepository.findByUserId(userId);
		
		// Get distinct symbols from the list of holdings
		Set<String> symbols = this.extractDistinctSymbols(holdings);
		
		// Get current prices for the symbols
		List<FmpCurrentPriceProjection> priceList = fmpCurrentPriceRepository.findBySymbolIn(symbols);

		var holdingPnLList = new ArrayList<HoldingDto>();
		
		// Calculate Holding PnL
		for (Holding h : holdings) {

			// Getting current price data
			FmpCurrentPriceProjection price = priceList.stream()
					.filter(m -> m.getSymbol().equals(h.getSymbol()))
					.findFirst()
					.orElse(null);			
			
			if (price != null) {
				var holdingPnL = new HoldingDto();

				holdingPnL.setId(h.getId());
				holdingPnL.setPortfolioId(h.getPortfolio().getId());
				holdingPnL.setSymbol(h.getSymbol());

				BigDecimal unrealizedPnL = price.getPrice().subtract(h.getAvgCostPerShare())
						.multiply(new BigDecimal(h.getShares()));
				BigDecimal currentCost = h.getAvgCostPerShare().multiply(new BigDecimal(h.getShares()));

				holdingPnL.setUnrealizedPnL(unrealizedPnL);
				holdingPnL.setMarketValue(currentCost);
				holdingPnL.setRealizedPnL(h.getRealizedPnL());
				holdingPnLList.add(holdingPnL);
			}
		}

		// Calculate PnL by Portfolio
		List<PortfolioUnrealizedPnLDto> summary = this.calculateUnrealizedPnLByPortfolio(holdingPnLList);
		log.info("[PortfolioSummaryService:getUserPortfoliosData] Summary of Unrealized PnL by Portfolio: {}", summary);
		
		// Log the summary
		for (PortfolioUnrealizedPnLDto s : summary) {
			System.out.println("[PortfolioSummaryService:getUserPortfoliosData] Portfolio ID: " + s.getId()
					+ ", Unrealized PnL: " + s.getUnrealizedPnL()
					+ ", Current Cost: " + s.getCurrentCost());
		}

		// Get all portfolios for the user
		List<Portfolio> portfolioList = portfolioRepository.findByUserId(userId);
		List<PortfolioDto> portfolioDtoList = new ArrayList<>();

		for (Portfolio p : portfolioList) {

			int portfolioId = p.getId().intValue();
			BigDecimal initialCash = p.getInitialCash()	!= null ? p.getInitialCash() : BigDecimal.ZERO;
			BigDecimal currentCash = p.getCurrentCash() != null ? p.getCurrentCash() : BigDecimal.ZERO;

			// Find PnL and Current Cost from summary
			Optional<PortfolioUnrealizedPnLDto> match = summary.stream()
					.filter(t -> t.getId().intValue() == portfolioId)
					.findFirst();

			BigDecimal unrealizedPnL = match.map(PortfolioUnrealizedPnLDto::getUnrealizedPnL).orElse(BigDecimal.ZERO);
			BigDecimal currentCost = match.map(PortfolioUnrealizedPnLDto::getCurrentCost).orElse(BigDecimal.ZERO);
			int numberOfHoldings = match.map(PortfolioUnrealizedPnLDto::getNumberOfHoldings).orElse(0);
			BigDecimal realizedPnL = match.map(PortfolioUnrealizedPnLDto::getRealizedPnL).orElse(BigDecimal.ZERO);

			// Calculate total portfolio state = current cash + current cost + PnL
			BigDecimal totalValue;
			if (realizedPnL.equals(BigDecimal.ZERO) && unrealizedPnL.equals(BigDecimal.ZERO)) {
				totalValue = initialCash;
			} else {
				totalValue = currentCash.add(currentCost).add(unrealizedPnL);
			}
//		    System.out.println("Portfolio " + portfolioId 
//		        + ": Initial Cash = " + initialCash 
//		        + ", Current Cash = " + currentCash 
//		        + ", Current Cost = " + currentCost
//		        + ", UnrealizedPnL PnL = " + unrealizedPnL 
//		        + ", Total Value = " + totalValue);

			PortfolioDto pDto = new PortfolioDto();
			pDto.setId(p.getId());
			pDto.setUserId(p.getUser().getId());
			pDto.setName(p.getName());
			pDto.setInitialCash(initialCash);
			pDto.setCurrentCash(currentCash);
			pDto.setCurrentCost(currentCost);
			pDto.setRealizedPnL(realizedPnL);
			pDto.setUnrealizedPnL(unrealizedPnL);
			pDto.setTotalValue(totalValue);

			BigDecimal pnl = totalValue.subtract(initialCash);
			BigDecimal pnlPercent = pnl.divide(initialCash, 10, RoundingMode.HALF_UP) // 10 is the intermediate scale
				    .setScale(4, RoundingMode.HALF_UP); // Final scale for display

			
			pDto.setNumberOfholdings(numberOfHoldings);
			pDto.setCreatedAt(p.getCreatedAt());
			pDto.setUpdatedAt(p.getUpdatedAt());
			
			
            pDto.setPnl(pnl.setScale(2, RoundingMode.HALF_UP));
            pDto.setPnlPercent(pnlPercent);
			portfolioDtoList.add(pDto);
		}
		// Log the portfolio DTOs
		for (PortfolioDto pdto : portfolioDtoList) {
			log.info("[PortfolioSummaryService:getUserPortfoliosData] Portfolio ID: {}, Name: {}, Initial Cash: {}, "
					+ "Current Cash: {}, Current Cost: {}, Realized PnL: {}, Unrealized PnL: {}, Total Value: {}, "
					+ "Number of Holdings: {}, PnL: {}, PnL Percent: {}", pdto.getId(), pdto.getName(),
					pdto.getInitialCash(), pdto.getCurrentCash(), pdto.getCurrentCost(), pdto.getRealizedPnL(),
					pdto.getUnrealizedPnL(), pdto.getTotalValue(), pdto.getNumberOfholdings(), pdto.getPnl(),
					pdto.getPnlPercent());
		}
		return portfolioDtoList;
	}

	
	/**
	 * 
	 * @param portfolioId
	 * @return
	 */
	public PortfolioDto getPortfolioData(long portfolioId) {
		
		// Get a list of all user holdings
		List<Holding> holdings = holdingRepository.findByPortfolioId(portfolioId);
		// Get distinct symbols from the list of holdings
		Set<String> symbols = this.extractDistinctSymbols(holdings);
		// Get current prices for the symbols
		List<FmpCurrentPriceProjection> priceList = fmpCurrentPriceRepository.findBySymbolIn(symbols);

		// Calculate Holding PnL
		var holdingPnLList = new ArrayList<HoldingDto>();
		for (Holding h : holdings) {

			FmpCurrentPriceProjection price = priceList.stream()
					.filter(m -> m.getSymbol().equals(h.getSymbol()))
					.findFirst()
					.orElse(null);			
			
			if (price != null) {
				var holdingPnL = new HoldingDto();

				holdingPnL.setId(h.getId());
				holdingPnL.setPortfolioId(h.getPortfolio().getId());
				holdingPnL.setSymbol(h.getSymbol());

				BigDecimal unrealizedPnL = price.getPrice().subtract(h.getAvgCostPerShare())
						.multiply(new BigDecimal(h.getShares()));
				BigDecimal currentCost = h.getAvgCostPerShare().multiply(new BigDecimal(h.getShares()));

				holdingPnL.setUnrealizedPnL(unrealizedPnL);
				holdingPnL.setMarketValue(currentCost);
				holdingPnL.setRealizedPnL(h.getRealizedPnL());
				holdingPnLList.add(holdingPnL);
			}
		}

		// Calculate total PnL by Portfolio
		PortfolioUnrealizedPnLDto summary = this.calculatePortfolioPnL(holdingPnLList);
		// Log the summary
		System.out.println("Portfolio ID: " + summary.getId()
				+ ", Unrealized PnL: " + summary.getUnrealizedPnL()
				+ ", Current Cost: " + summary.getCurrentCost()
				+ ", Realized PnL: " + summary.getRealizedPnL());

		// Get portfolio data
		Portfolio origPortfolio = portfolioRepository.findById(portfolioId)
				.orElseThrow(() -> new RuntimeException("Portfolio not found"));
		PortfolioDto pdto = new PortfolioDto();
		pdto.setUserId(origPortfolio.getUser().getId());
		pdto.setName(origPortfolio.getName());
		pdto.setInitialCash(origPortfolio.getInitialCash());
		pdto.setCurrentCash(origPortfolio.getCurrentCash());
		pdto.setCurrentCost(summary.getCurrentCost());
		pdto.setRealizedPnL(summary.getRealizedPnL());
		pdto.setUnrealizedPnL(summary.getUnrealizedPnL());
		pdto.setTotalValue(origPortfolio.getCurrentCash().add(summary.getCurrentCost())
				.add(summary.getUnrealizedPnL()));
		pdto.setNumberOfholdings(summary.getNumberOfHoldings());
		
		// Log the portfolio DTO
		System.out.println("Single Case: Portfolio ID: " + pdto.getId()
				+ ", Name: " + pdto.getName()
				+ ", Initial Cash: " + pdto.getInitialCash()
				+ ", Current Cash: " + pdto.getCurrentCash()
				+ ", Current Cost: " + pdto.getCurrentCost()
				+ ", Realized PnL: " + pdto.getRealizedPnL()
				+ ", Unrealized PnL: " + pdto.getUnrealizedPnL()
				+ ", Total Value: " + pdto.getTotalValue()
				+ ", Number of Holdings: " + pdto.getNumberOfholdings());

		return pdto;
	}

	/**
	 * This method retrieves the portfolio holdings data for a given portfolio ID.
	 * It calculates the unrealized and realized PnL for each holding.
	 *
	 * @param portfolioId The ID of the portfolio whose holdings data is to be
	 *                    retrieved.
	 * @return A list of HoldingPnLDto objects containing the holdings data.
	 */
	public List<HoldingDto> getPortfolioHoldingsData(long portfolioId) {

		// Get a list of all user holdings
		List<Holding> holdings = holdingRepository.findByPortfolioId(portfolioId);
		
		// Get distinct symbols from the list of holdings
		Set<String> symbols = this.extractDistinctSymbols(holdings);
		
		// Get current prices for the symbols
		List<FmpCurrentPriceProjection> priceListNew = fmpCurrentPriceRepository.findBySymbolIn(symbols);
		
		// Calculate Holding PnL
		var holdingPnLList = new ArrayList<HoldingDto>();
		
		for (Holding h : holdings) {

			// Set the current price for each transaction
			FmpCurrentPriceProjection price = priceListNew.stream()
					.filter(m -> m.getSymbol().equals(h.getSymbol()))
					.findFirst()
					.orElse(null);			
			
			if (price != null) {
				var holdingPnL = new HoldingDto();

				holdingPnL.setId(h.getId());
				holdingPnL.setPortfolioId(h.getPortfolio().getId());
				holdingPnL.setSymbol(h.getSymbol());
				holdingPnL.setShares(h.getShares());
				holdingPnL.setAvgCostPerShare(h.getAvgCostPerShare());
				holdingPnL.setBookCost(h.getBookCost());
				holdingPnL.setCurrency(h.getCurrency());

				BigDecimal unrealizedPnL = price.getPrice().subtract(h.getAvgCostPerShare())
						.multiply(new BigDecimal(h.getShares()));
				BigDecimal currentCost = h.getAvgCostPerShare().multiply(new BigDecimal(h.getShares()));

				holdingPnL.setUnrealizedPnL(unrealizedPnL);
				holdingPnL.setMarketValue(currentCost);
				holdingPnL.setRealizedPnL(h.getRealizedPnL());
				holdingPnLList.add(holdingPnL);
			}
		}
		return holdingPnLList;
	}

	
	/**
	 * Extract distinct symbols from a list of holdings
	 * 
	 * @param holdings
	 * @return
	 */
	public Set<String> extractDistinctSymbols(List<Holding> holdings) {
		return holdings.stream()
				.map(Holding::getSymbol)
				.filter(symbol -> symbol != null && !symbol.isBlank())
				.collect(Collectors.toSet()); // Set ensures distinct values
	}

	/**
	 * Calculate unrealized PnL for all user portfolios
	 * 
	 * @param holdingPnLs - list of portfolio holdings
	 * @return
	 */
	public static List<PortfolioUnrealizedPnLDto> calculateUnrealizedPnLByPortfolio(List<HoldingDto> holdingPnLs) {
		
		Map<Long, BigDecimal> portfolioUnrealizedPnLMap = new HashMap<>();
		Map<Long, BigDecimal> portfolioRealizedPnLMap = new HashMap<>();
		Map<Long, BigDecimal> portfolioCurrentCostMap = new HashMap<>();
		Map<Long, Integer> portfolioHoldingsCountMap = new HashMap<>();
		// Map<Integer, BigDecimal> portfolioMarketValueMap = new HashMap<>();

		for (HoldingDto holding : holdingPnLs) {
			long portfolioId = holding.getPortfolioId();

			BigDecimal unrealizedPnL = holding.getUnrealizedPnL() != null ? holding.getUnrealizedPnL()
					: BigDecimal.ZERO;
			BigDecimal realizedPnL = holding.getRealizedPnL() != null ? holding.getRealizedPnL() : BigDecimal.ZERO;
			BigDecimal currentCost = holding.getMarketValue() != null ? holding.getMarketValue() : BigDecimal.ZERO;

			portfolioUnrealizedPnLMap.merge(portfolioId, unrealizedPnL, BigDecimal::add);
			portfolioRealizedPnLMap.merge(portfolioId, realizedPnL, BigDecimal::add);
			portfolioCurrentCostMap.merge(portfolioId, currentCost, BigDecimal::add);
			portfolioHoldingsCountMap.merge(portfolioId, 1, Integer::sum);
		}

		List<PortfolioUnrealizedPnLDto> result = new ArrayList<>();

		for (Long portfolioId : portfolioUnrealizedPnLMap.keySet()) {
			PortfolioUnrealizedPnLDto summary = new PortfolioUnrealizedPnLDto();
			summary.setId(Long.valueOf(portfolioId));
			summary.setUnrealizedPnL(portfolioUnrealizedPnLMap.getOrDefault(portfolioId, BigDecimal.ZERO));
			summary.setRealizedPnL(portfolioRealizedPnLMap.getOrDefault(portfolioId, BigDecimal.ZERO));
			summary.setCurrentCost(portfolioCurrentCostMap.getOrDefault(portfolioId, BigDecimal.ZERO));
			summary.setNumberOfHoldings(portfolioHoldingsCountMap.getOrDefault(portfolioId, 0));
			result.add(summary);
		}

		return result;
	}

	/**
	 * Calculate the portfolio unrealized PnL
	 * 
	 * @param holdingPnLList - list of portfolio holdings
	 * @return
	 */
	public PortfolioUnrealizedPnLDto calculatePortfolioPnL(List<HoldingDto> holdingList) {

		Map<Long, BigDecimal> portfolioUnrealizedPnLMap = new HashMap<>();
		Map<Long, BigDecimal> portfolioRealizedPnLMap = new HashMap<>();
		Map<Long, BigDecimal> portfolioCurrentCostMap = new HashMap<>();
		Map<Long, Integer> portfolioHoldingsCountMap = new HashMap<>();
		Map<Long, BigDecimal> portfolioMarketValueMap = new HashMap<>();

		long portfolioId = 0;

		for (HoldingDto holding : holdingList) {
			portfolioId = holding.getPortfolioId();

			BigDecimal unrealizedPnL = holding.getUnrealizedPnL() != null ? holding.getUnrealizedPnL()
					: BigDecimal.ZERO;
			BigDecimal realizedPnL = holding.getRealizedPnL() != null ? holding.getRealizedPnL() : BigDecimal.ZERO;
			BigDecimal currentCost = holding.getMarketValue() != null ? holding.getMarketValue() : BigDecimal.ZERO;

			portfolioUnrealizedPnLMap.merge(portfolioId, unrealizedPnL, BigDecimal::add);
			portfolioRealizedPnLMap.merge(portfolioId, realizedPnL, BigDecimal::add);
			portfolioCurrentCostMap.merge(portfolioId, currentCost, BigDecimal::add);
			portfolioHoldingsCountMap.merge(portfolioId, 1, Integer::sum);
		}

//		List<PortfolioUnrealizedPnLDto> result = new ArrayList<>();
		PortfolioUnrealizedPnLDto result = new PortfolioUnrealizedPnLDto();

		result.setId(Long.valueOf(portfolioId));
		result.setUnrealizedPnL(portfolioUnrealizedPnLMap.getOrDefault(portfolioId, BigDecimal.ZERO));
		result.setRealizedPnL(portfolioRealizedPnLMap.getOrDefault(portfolioId, BigDecimal.ZERO));
		result.setCurrentCost(portfolioCurrentCostMap.getOrDefault(portfolioId, BigDecimal.ZERO));
		result.setNumberOfHoldings(portfolioHoldingsCountMap.getOrDefault(portfolioId, 0));

		return result;
	}

}
