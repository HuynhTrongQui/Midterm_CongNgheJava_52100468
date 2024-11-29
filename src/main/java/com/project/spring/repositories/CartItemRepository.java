package com.project.spring.repositories;

import com.project.spring.model.Cart;
import com.project.spring.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface CartItemRepository  extends JpaRepository<CartItem,Long>{
    List<CartItem> findByCartIn(List<Cart> cart);
}
