package com.poc.droolspocv2.repository;

import com.poc.droolspocv2.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
