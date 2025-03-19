package org.example.securetracks.repository;

import org.example.securetracks.model.Delivery;
import org.example.securetracks.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    List<Delivery> findByOwner(User owner);
}
