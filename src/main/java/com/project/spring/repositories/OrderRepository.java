package com.project.spring.repositories;


import com.project.spring.model.Order;
import com.project.spring.model.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(AppUser user);
    Page<Order> findByUser_Id(Long userId, Pageable pageable);
    @Query("select o from Order o  where EXTRACT(year from o.date) = :year")
    List<Order> findOrderByYear(Integer year);
    List<Order> findByDate(Date date);

    @Query("SELECT o FROM Order o WHERE YEAR(o.date) = :year AND MONTH(o.date) = :month")
    List<Order> findByYearAndMonth(int year, int month);

}
