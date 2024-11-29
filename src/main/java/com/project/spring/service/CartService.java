package com.project.spring.service;

import com.project.spring.dto.CartDTO;
import com.project.spring.model.Cart;
import com.project.spring.model.CartItem;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface CartService {

    Cart saveOrUpdateCart(Cart cart);

    Optional<CartDTO> getCartDTOById(Long id);
    List<CartDTO> getCartDTOByIdUser(Long idUser);


}
