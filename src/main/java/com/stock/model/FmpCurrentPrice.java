package com.stock.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "fmp_current_price", indexes = {
		@Index(name = "idx_symbol", columnList = "symbol")
})
public class FmpCurrentPrice {

	@Id
	@Column(name="symbol")
	private String symbol;
	
	@Column(name="name")
	private String name;
	
	@Column(name="price")
	private BigDecimal price;
	
	@Column(name="changes_percentage")
	private BigDecimal changesPercentage;
	
	@Column(name="price_change")
	private BigDecimal priceChange;
	
	@Column(name="day_low")
	private BigDecimal dayLow;
	
	@Column(name="day_high")
	private BigDecimal dayHigh;
	
	@Column(name="year_high")
	private BigDecimal yearHigh;
	
	@Column(name="year_low")
	private BigDecimal yearLow;
	
	@Column(name="market_cap")
	private Double marketCap;
	
	@Column(name="price_avg50")
	private BigDecimal priceAvg50;
	
	@Column(name="price_avg200")
	private BigDecimal priceAvg200;
	
	@Column(name="exchange")
	private String exchange;
	
	@Column(name="volume")
	private Long volume;
	
	@Column(name="avg_volume")
	private Long avgVolume;
	
	@Column(name="open_price")
	private BigDecimal openPrice;
	
	@Column(name="previous_close")
	private BigDecimal previousClose;
	
	@Column(name="eps")
	private BigDecimal eps;
	
	@Column(name="pe")
	private BigDecimal pe;
	
	@Column(name="earnings_announcement")
	private LocalDateTime earningsAnnouncement;
	
	@Column(name="shares_outstanding")
	private Double sharesOutstanding;
	
	@Column(name = "created_on", columnDefinition = "DATETIME")
	private LocalDateTime createdOn;

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getChangesPercentage() {
		return changesPercentage;
	}

	public void setChangesPercentage(BigDecimal changesPercentage) {
		this.changesPercentage = changesPercentage;
	}

	public BigDecimal getPriceChange() {
		return priceChange;
	}

	public void setPriceChange(BigDecimal priceChange) {
		this.priceChange = priceChange;
	}

	public BigDecimal getDayLow() {
		return dayLow;
	}

	public void setDayLow(BigDecimal dayLow) {
		this.dayLow = dayLow;
	}

	public BigDecimal getDayHigh() {
		return dayHigh;
	}

	public void setDayHigh(BigDecimal dayHigh) {
		this.dayHigh = dayHigh;
	}

	public BigDecimal getYearHigh() {
		return yearHigh;
	}

	public void setYearHigh(BigDecimal yearHigh) {
		this.yearHigh = yearHigh;
	}

	public BigDecimal getYearLow() {
		return yearLow;
	}

	public void setYearLow(BigDecimal yearLow) {
		this.yearLow = yearLow;
	}

	public Double getMarketCap() {
		return marketCap;
	}

	public void setMarketCap(Double double1) {
		this.marketCap = double1;
	}

	public BigDecimal getPriceAvg50() {
		return priceAvg50;
	}

	public void setPriceAvg50(BigDecimal priceAvg50) {
		this.priceAvg50 = priceAvg50;
	}

	public BigDecimal getPriceAvg200() {
		return priceAvg200;
	}

	public void setPriceAvg200(BigDecimal priceAvg200) {
		this.priceAvg200 = priceAvg200;
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public Long getVolume() {
		return volume;
	}

	public void setVolume(Long volume) {
		this.volume = volume;
	}

	public Long getAvgVolume() {
		return avgVolume;
	}

	public void setAvgVolume(Long avgVolume) {
		this.avgVolume = avgVolume;
	}

	public BigDecimal getOpenPrice() {
		return openPrice;
	}

	public void setOpenPrice(BigDecimal openPrice) {
		this.openPrice = openPrice;
	}

	public BigDecimal getPreviousClose() {
		return previousClose;
	}

	public void setPreviousClose(BigDecimal previousClose) {
		this.previousClose = previousClose;
	}

	public BigDecimal getEps() {
		return eps;
	}

	public void setEps(BigDecimal eps) {
		this.eps = eps;
	}

	public BigDecimal getPe() {
		return pe;
	}

	public void setPe(BigDecimal pe) {
		this.pe = pe;
	}

	public LocalDateTime getEarningsAnnouncement() {
		return earningsAnnouncement;
	}

	public void setEarningsAnnouncement(LocalDateTime earningsAnnouncement) {
		this.earningsAnnouncement = earningsAnnouncement;
	}

	public Double getSharesOutstanding() {
		return sharesOutstanding;
	}

	public void setSharesOutstanding(Double sharesOutstanding) {
		this.sharesOutstanding = sharesOutstanding;
	}

	public LocalDateTime getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(LocalDateTime createdOn) {
		this.createdOn = createdOn;
	}

	@Override
	public String toString() {
		return "FmpCurrentPrice [symbol=" + symbol + ", name=" + name + ", price=" + price + ", changesPercentage="
				+ changesPercentage + ", priceChange=" + priceChange + ", dayLow=" + dayLow + ", dayHigh=" + dayHigh
				+ ", yearHigh=" + yearHigh + ", yearLow=" + yearLow + ", marketCap=" + marketCap + ", priceAvg50="
				+ priceAvg50 + ", priceAvg200=" + priceAvg200 + ", exchange=" + exchange + ", volume=" + volume
				+ ", avgVolume=" + avgVolume + ", openPrice=" + openPrice + ", previousClose=" + previousClose
				+ ", eps=" + eps + ", pe=" + pe + ", earningsAnnouncement=" + earningsAnnouncement
				+ ", sharesOutstanding=" + sharesOutstanding + ", createdOn=" + createdOn + "]";
	}
}
