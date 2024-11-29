package com.project.spring.dto;

import com.project.spring.model.Order;
import com.project.spring.model.Product;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderDetailDTO {
    private Long idOrderDetail;
    private Long idProduct;
    private Long orderId;
    private String nameProduct;
    private double priceProduct;
    private int quantity;
    private String url;
    private String color;
	public Long getIdOrderDetail() {
		return idOrderDetail;
	}
	public void setIdOrderDetail(Long idOrderDetail) {
		this.idOrderDetail = idOrderDetail;
	}
	public Long getIdProduct() {
		return idProduct;
	}
	public void setIdProduct(Long idProduct) {
		this.idProduct = idProduct;
	}
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public String getNameProduct() {
		return nameProduct;
	}
	public void setNameProduct(String nameProduct) {
		this.nameProduct = nameProduct;
	}
	public double getPriceProduct() {
		return priceProduct;
	}
	public void setPriceProduct(double priceProduct) {
		this.priceProduct = priceProduct;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
}
