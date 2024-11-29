package com.project.spring.service;


import com.project.spring.model.Order;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrderService {
    Order saveOrUpdate(Order order);
    List<Order> findByIdUser(Long id);
}
