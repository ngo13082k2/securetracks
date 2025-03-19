package org.example.securetracks.repository;

import org.example.securetracks.model.OrderDetail;
import org.example.securetracks.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findByCustomer_User(User user);

    List<OrderDetail> findByUser(User currentUser);
}