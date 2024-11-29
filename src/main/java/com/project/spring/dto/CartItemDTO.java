package com.project.spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {
	private Long productId;
	private String productName;
	private double productPrice;
    private int quantity;
	private String productOriginalPicture;
	public CartItemDTO(Long productId, String productName, double productPrice, int quantity,
			String productOriginalPicture) {
		super();
		this.productId = productId;
		this.productName = productName;
		this.productPrice = productPrice;
		this.quantity = quantity;
		this.productOriginalPicture = productOriginalPicture;
	}
	public CartItemDTO() {
		super();
	}
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public double getProductPrice() {
		return productPrice;
	}
	public void setProductPrice(double productPrice) {
		this.productPrice = productPrice;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public String getProductOriginalPicture() {
		return productOriginalPicture;
	}
	public void setProductOriginalPicture(String productOriginalPicture) {
		this.productOriginalPicture = productOriginalPicture;
	}

}
