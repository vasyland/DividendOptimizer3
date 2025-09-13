package com.stock.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "accuweather")
public class Accuweather {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private BigDecimal temperature;
    private String weatherConditions;
    private Integer pressure;
    private Integer humidity;
    private Integer indoorHumidity;
    private String windDirection;
    private BigDecimal windSpeed;
    private BigDecimal windGusts;
    private BigDecimal visibility;
    private Integer cloudCover;
    private BigDecimal dewPoint;
    private Integer cloudCeiling;

    @Column(name = "created_on", updatable = false, insertable = false)
    private LocalDateTime createdOn;

    @Column(name = "updated_on", insertable = false)
    private LocalDateTime updatedOn;

    @Transient // not stored in DB
    private Integer dayOfMonth;
    
    public Integer getDayOfMonth() {
        return (createdOn != null) ? createdOn.getDayOfMonth() : null;
    }
    
    @Transient
    public String getReadTime() {
        return (createdOn != null) 
            ? createdOn.format(DateTimeFormatter.ofPattern("HH:mm")) 
            : null;
    }
    
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public BigDecimal getTemperature() {
		return temperature;
	}

	public void setTemperature(BigDecimal temperature) {
		this.temperature = temperature;
	}

	public String getWeatherConditions() {
		return weatherConditions;
	}

	public void setWeatherConditions(String weatherConditions) {
		this.weatherConditions = weatherConditions;
	}

	public Integer getPressure() {
		return pressure;
	}

	public void setPressure(Integer pressure) {
		this.pressure = pressure;
	}

	public Integer getHumidity() {
		return humidity;
	}

	public void setHumidity(Integer humidity) {
		this.humidity = humidity;
	}

	public Integer getIndoorHumidity() {
		return indoorHumidity;
	}

	public void setIndoorHumidity(Integer indoorHumidity) {
		this.indoorHumidity = indoorHumidity;
	}

	public String getWindDirection() {
		return windDirection;
	}

	public void setWindDirection(String windDirection) {
		this.windDirection = windDirection;
	}

	public BigDecimal getWindSpeed() {
		return windSpeed;
	}

	public void setWindSpeed(BigDecimal windSpeed) {
		this.windSpeed = windSpeed;
	}

	public BigDecimal getWindGusts() {
		return windGusts;
	}

	public void setWindGusts(BigDecimal windGusts) {
		this.windGusts = windGusts;
	}

	public BigDecimal getVisibility() {
		return visibility;
	}

	public void setVisibility(BigDecimal visibility) {
		this.visibility = visibility;
	}

	public Integer getCloudCover() {
		return cloudCover;
	}

	public void setCloudCover(Integer cloudCover) {
		this.cloudCover = cloudCover;
	}

	public BigDecimal getDewPoint() {
		return dewPoint;
	}

	public void setDewPoint(BigDecimal dewPoint) {
		this.dewPoint = dewPoint;
	}

	public Integer getCloudCeiling() {
		return cloudCeiling;
	}

	public void setCloudCeiling(Integer cloudCeiling) {
		this.cloudCeiling = cloudCeiling;
	}

	public LocalDateTime getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(LocalDateTime createdOn) {
		this.createdOn = createdOn;
	}

	public LocalDateTime getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(LocalDateTime updatedOn) {
		this.updatedOn = updatedOn;
	}
}
