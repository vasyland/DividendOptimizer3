package com.stock.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stock.model.Holding;
import com.stock.model.Transaction;
import com.stock.model.TransactionType;
import com.stock.repositories.HoldingRepository;
import com.stock.repositories.TransactionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class HoldingsService {

    private final TransactionRepository transactionRepository;
    private final HoldingRepository holdingRepository;

    /**
     * Recalculate and update the holding for a specific symbol in a portfolio
     */
    @Transactional
    public void recalculateHoldingForSymbol(Long portfolioId, String symbol) {
        List<Transaction> transactions = transactionRepository.findByPortfolioIdAndSymbol(portfolioId, symbol);

        if (transactions.isEmpty()) {
            holdingRepository.deleteByPortfolioIdAndSymbol(portfolioId, symbol);
            return;
        }

        int totalShares = 0;
        BigDecimal totalCost = BigDecimal.ZERO;
        String currency = transactions.get(0).getCurrency();

        for (Transaction tx : transactions) {
            int shares = tx.getShares();
            BigDecimal commissions = tx.getCommissions() != null ? tx.getCommissions() : BigDecimal.ZERO;
            BigDecimal tradeValue = tx.getPrice().multiply(BigDecimal.valueOf(shares));

            if (tx.getTransactionType() == TransactionType.BUY) {
                totalShares += shares;
                totalCost = totalCost.add(tradeValue).add(commissions); // ✅ add commission to cost
            } else if (tx.getTransactionType() == TransactionType.SELL) {
                totalShares -= shares;
                // ❌ DO NOT modify totalCost!
                // Optionally track realized profit/loss separately
            }
        }

        if (totalShares <= 0) {
            holdingRepository.deleteByPortfolioIdAndSymbol(portfolioId, symbol);
            return;
        }

        BigDecimal avgCost = totalCost.divide(BigDecimal.valueOf(totalShares), 2, RoundingMode.HALF_UP);
        BigDecimal bookCost = totalCost.setScale(2, RoundingMode.HALF_UP);

        Holding holding = holdingRepository.findByPortfolioIdAndSymbol(portfolioId, symbol)
            .orElseGet(() -> {
                Holding h = new Holding();
                h.setPortfolio(transactions.get(0).getPortfolio());
                h.setSymbol(symbol);
                return h;
            });

        holding.setShares(totalShares);
        holding.setAvgCostPerShare(avgCost);
        holding.setBookCost(bookCost);
        holding.setCurrency(currency);

        holdingRepository.save(holding);
    }


    /**
     * Recalculate holdings for all symbols in a given portfolio.
     */
    @Transactional
    public void recalculateHoldingsForPortfolio(Long portfolioId) {
        holdingRepository.deleteByPortfolioId(portfolioId);

        List<Transaction> transactions = transactionRepository.findByPortfolioId(portfolioId);

        // Group transactions by symbol
        Map<String, List<Transaction>> symbolTransactions = transactions.stream()
            .collect(Collectors.groupingBy(Transaction::getSymbol));

        for (Map.Entry<String, List<Transaction>> entry : symbolTransactions.entrySet()) {
            String symbol = entry.getKey();
            List<Transaction> txs = entry.getValue();

            BigDecimal totalCost = BigDecimal.ZERO;
            int totalShares = 0;

            for (Transaction tx : txs) {
                int shares = tx.getShares();
                BigDecimal price = tx.getPrice();
                BigDecimal commissions = tx.getCommissions() != null ? tx.getCommissions() : BigDecimal.ZERO;
                BigDecimal amount = price.multiply(BigDecimal.valueOf(shares)).add(commissions);

                if (tx.getTransactionType() == TransactionType.BUY) {
                    totalCost = totalCost.add(amount);
                    totalShares += shares;
                } else if (tx.getTransactionType() == TransactionType.SELL) {
                    if (totalShares == 0) continue; // skip if nothing to sell

                    BigDecimal avgCostPerShare = totalCost.divide(BigDecimal.valueOf(totalShares), 10, RoundingMode.HALF_UP);
                    BigDecimal costReduction = avgCostPerShare.multiply(BigDecimal.valueOf(shares));
                    totalCost = totalCost.subtract(costReduction);
                    totalShares -= shares;
                }
            }

            if (totalShares > 0) {
                BigDecimal avgCostPerShare = totalCost.divide(BigDecimal.valueOf(totalShares), 2, RoundingMode.HALF_UP);
                Holding holding = new Holding();
                holding.setPortfolio(txs.get(0).getPortfolio());
                holding.setSymbol(symbol);
                holding.setShares(totalShares);
                holding.setAvgCostPerShare(avgCostPerShare);
                holding.setBookCost(totalCost.setScale(2, RoundingMode.HALF_UP));
                holding.setCurrency(txs.get(0).getCurrency());

                holdingRepository.save(holding);
            }
        }
    }


}
