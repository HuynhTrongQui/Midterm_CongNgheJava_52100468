package com.project.spring.repositories;

import com.project.spring.model.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepositopry extends JpaRepository<OrderDetail, Long> {
    @Query("select c from OrderDetail c where c.product.id = :id")
    List<OrderDetail> findByProduct(Long id);

}
