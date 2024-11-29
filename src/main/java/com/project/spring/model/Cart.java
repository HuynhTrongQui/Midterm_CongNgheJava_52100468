package com.project.spring.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cart")
@Data
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class Cart{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private AppUser user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    @Column(columnDefinition = "double default 0")
    private Double total;

    public Double getCost(){
        double sum = 0.0;
        for(CartItem cartItem : cartItems){
            sum += cartItem.getProduct().getPrice()*cartItem.getQuantity();
        }
        return sum;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public AppUser getUser() {
		return user;
	}

	public void setUser(AppUser user) {
		this.user = user;
	}

	public List<CartItem> getCartItems() {
		return cartItems;
	}

	public void setCartItems(List<CartItem> cartItems) {
		this.cartItems = cartItems;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public Cart(Long id, AppUser user, List<CartItem> cartItems, Double total) {
		super();
		this.id = id;
		this.user = user;
		this.cartItems = cartItems;
		this.total = total;
	}

	public Cart() {
		super();
	}
}
