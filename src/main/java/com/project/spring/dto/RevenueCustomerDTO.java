package com.project.spring.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RevenueCustomerDTO {
    private Long id;
    private String name;
    private int quantity;
    private BigDecimal total;
    private BigDecimal priceMin;
    private BigDecimal priceMax;
    private BigDecimal gpa;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public BigDecimal getTotal() {
		return total;
	}
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	public BigDecimal getPriceMin() {
		return priceMin;
	}
	public void setPriceMin(BigDecimal priceMin) {
		this.priceMin = priceMin;
	}
	public BigDecimal getPriceMax() {
		return priceMax;
	}
	public void setPriceMax(BigDecimal priceMax) {
		this.priceMax = priceMax;
	}
	public BigDecimal getGpa() {
		return gpa;
	}
	public void setGpa(BigDecimal gpa) {
		this.gpa = gpa;
	}
}
