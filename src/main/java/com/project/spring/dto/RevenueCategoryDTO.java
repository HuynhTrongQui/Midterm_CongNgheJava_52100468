package com.project.spring.dto;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class RevenueCategoryDTO {
    private String nameCategory;
    private Integer quantity;
    private BigDecimal total;
    private Double priceMin;
    private Double priceMax;
    private BigDecimal gpa;
	public String getNameCategory() {
		return nameCategory;
	}
	public void setNameCategory(String nameCategory) {
		this.nameCategory = nameCategory;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public BigDecimal getTotal() {
		return total;
	}
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	public Double getPriceMin() {
		return priceMin;
	}
	public void setPriceMin(Double priceMin) {
		this.priceMin = priceMin;
	}
	public Double getPriceMax() {
		return priceMax;
	}
	public void setPriceMax(Double priceMax) {
		this.priceMax = priceMax;
	}
	public BigDecimal getGpa() {
		return gpa;
	}
	public void setGpa(BigDecimal gpa) {
		this.gpa = gpa;
	}
}
