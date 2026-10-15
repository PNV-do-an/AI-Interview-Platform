package com.aiinterview.platform.model.repository;

import com.aiinterview.platform.model.entity.Order;
import com.aiinterview.platform.model.entity.ServicePackage;
import com.aiinterview.platform.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserOrderByCreatedAtDesc(User user);

    long countByServicePackage(ServicePackage servicePackage);
}
