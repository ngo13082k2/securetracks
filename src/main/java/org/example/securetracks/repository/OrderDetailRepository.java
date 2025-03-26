package org.example.securetracks.repository;

import org.example.securetracks.model.OrderDetail;
import org.example.securetracks.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    List<OrderDetail> findByUser(User currentUser);
    List<OrderDetail> findByUserAndCustomerPhoneNumber(User user, String phoneNumber);


    List<OrderDetail> findByUserAndDateCreateBetween(User user, LocalDate startDate, LocalDate endDate);

}