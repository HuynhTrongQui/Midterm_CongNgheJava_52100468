package com.project.spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO {
    private Long idCart;
    private Long idUser;
    private List<CartItemDTO> cartItems;
    private Double total;
	public Long getIdCart() {
		return idCart;
	}
	public void setIdCart(Long idCart) {
		this.idCart = idCart;
	}
	public Long getIdUser() {
		return idUser;
	}
	public void setIdUser(Long idUser) {
		this.idUser = idUser;
	}
	public List<CartItemDTO> getCartItems() {
		return cartItems;
	}
	public void setCartItems(List<CartItemDTO> cartItems) {
		this.cartItems = cartItems;
	}
	public Double getTotal() {
		return total;
	}
	public void setTotal(Double total) {
		this.total = total;
	}
	public CartDTO(Long idCart, Long idUser, List<CartItemDTO> cartItems, Double total) {
		super();
		this.idCart = idCart;
		this.idUser = idUser;
		this.cartItems = cartItems;
		this.total = total;
	}
	public CartDTO() {
		super();
	}
}
