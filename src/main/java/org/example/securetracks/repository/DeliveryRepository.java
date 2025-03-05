package org.example.securetracks.repository;

import org.example.securetracks.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
}
