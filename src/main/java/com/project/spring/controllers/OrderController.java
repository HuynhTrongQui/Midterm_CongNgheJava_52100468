package com.project.spring.controllers;

import com.project.spring.dto.CartDTO;
import com.project.spring.dto.CartItemDTO;
import com.project.spring.dto.OrderDTO;
import com.project.spring.dto.OrderDetailDTO;
import com.project.spring.model.AppUser;
import com.project.spring.model.Cart;
import com.project.spring.model.Order;
import com.project.spring.repositories.CartRepository;
import com.project.spring.repositories.OrderRepository;
import com.project.spring.repositories.UserRepository;
import com.project.spring.service.impl.UserDetailsServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    CartRepository cartRepository;

    @GetMapping
    public String order(Model model,
                        @RequestParam(name = "currentPage", defaultValue = "1") Integer currentPage,
                        @RequestParam(name = "pageSize", defaultValue = "3") Integer pageSize,
                        @RequestParam(name = "sortBy", defaultValue = "date") String sortBy,
                        @RequestParam(name = "orderField", defaultValue = "desc") String orderField) {
        AppUser user = this.userRepository.getUserByUsername(this.userDetailsService.getCurrentUserId());
        if (user != null) {
            List<Cart> carts = this.cartRepository.findByUserId(user.getId());
            if (carts.isEmpty()) {
                Cart cart = new Cart();
                cart.setUser(user);
                cart.setTotal(0.0);
                cart.setCartItems(new ArrayList<>());
                Cart newCart = this.cartRepository.save(cart);
                model.addAttribute("numberItems", newCart.getCartItems().size());
                model.addAttribute("idCart", newCart.getId());
            } else {
                Cart cart = carts.get(0);
                CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
                List<CartItemDTO> cartItemDTOs = cartDTO.getCartItems();
                model.addAttribute("numberItems", cartItemDTOs.size());
                model.addAttribute("idCart", cart.getId());
            }
            model.addAttribute("isLogin", user.getName());
        }

        Sort.Direction direction = orderField.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort.Order order = new Sort.Order(direction, sortBy);


        Pageable pageable = PageRequest.of((currentPage < 1 ? 0 : currentPage-1), pageSize, Sort.by(order));

        Page<OrderDTO> page = this.orderRepository.findByUser_Id(user.getId(), pageable)
                .map(order1 ->  {
                    OrderDTO orderDTO = new OrderDTO();
                    orderDTO.setIdOrder(order1.getId());
                    orderDTO.setDate(order1.getDate());
                    orderDTO.setTotal(order1.getTotal());
                    orderDTO.setStatus(order1.getStatus());
                    return orderDTO;
                });


        model.addAttribute("orders", page.getContent());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("orderField", orderField);
        return "order";
    }
    @GetMapping("/{id}")
    public String orderdetail(@PathVariable("id") Long id,
                              Model model) {
        Order order = this.orderRepository.findById(id).get();

        List<OrderDetailDTO> orderDetailDTOS = order.getOrderDetails().stream()
                .map(orderDetail -> modelMapper.map(orderDetail, OrderDetailDTO.class))
                .toList();

        OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);

        model.addAttribute("order", orderDTO);
        model.addAttribute("time", orderDTO.getDate());
        model.addAttribute("orderdetails", orderDetailDTOS);
        return "orderdetail";
    }
}
