package com.project.spring.service.impl;

import com.project.spring.dto.CartDTO;
import com.project.spring.dto.CartItemDTO;
import com.project.spring.dto.ProductDTO;
import com.project.spring.model.Cart;
import com.project.spring.model.CartItem;
import com.project.spring.repositories.CartItemRepository;
import com.project.spring.repositories.CartRepository;
import com.project.spring.service.CartService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CartServiceImpl implements CartService {

    @Autowired
    CartRepository cartRepository;
    
    @Autowired
    ModelMapper modelMapper;
    
    @Override
    public Cart saveOrUpdateCart(Cart cart) {
        return this.cartRepository.save(cart);
    }

    @Override
    public Optional<CartDTO> getCartDTOById(Long id) {
        Optional<Cart> carts = this.cartRepository.findById(id);
        if ( carts.isPresent()){
            CartDTO cartDTO = modelMapper.map(carts.get(),CartDTO.class);
            return Optional.of(cartDTO);
        }
        return Optional.empty();
    }

    @Override
    public List<CartDTO> getCartDTOByIdUser(Long idUser) {
        List<Cart> carts = this.cartRepository.findByUserId(idUser);
        return carts.stream().map(t -> modelMapper.map(t, CartDTO.class) ).toList();
    }




	/*
	 * public CartDTO toCartDTO(Cart cart){ CartDTO cartDTO = new CartDTO();
	 * cartDTO.setCart_Id(cart.getId()); cartDTO.setUser_Id(cart.getUser().getId());
	 * List<CartItemDTO> cartItemDTOS = cart.getCartItems().stream().map(cartItem ->
	 * new CartItemDTO( cartItem.getId(), cartItem.getProduct().getId(),
	 * cartItem.getQuantity(), cartItem.getProduct().getPrice(),
	 * cartItem.getProduct().getName(),
	 * cartItem.getProduct().getOriginalPicture())).toList();
	 * cartDTO.setCartItems(cartItemDTOS); cartDTO.setTotal(cart.getTotal()); return
	 * cartDTO; }
	 */
}
