package com.project.spring.service.impl;

import com.project.spring.model.Order;
import com.project.spring.model.AppUser;
import com.project.spring.repositories.OrderRepository;
import com.project.spring.repositories.UserRepository;
import com.project.spring.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    UserRepository userRepository;

    @Override
    public Order saveOrUpdate(Order order) {
        return this.orderRepository.save(order);
    }

    @Override
    public List<Order> findByIdUser(Long idUser) {
        AppUser user = this.userRepository.findById(idUser).orElse(null);
        if (user == null) {
            return null;
        }
        List<Order> orders = this.orderRepository.findByUser(user);
        if (orders.isEmpty()) {
            return null;
        }
        return orders;
    }
}
