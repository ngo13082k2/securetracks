package org.example.securetracks.repository;

import org.example.securetracks.model.Delivery;
import org.example.securetracks.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    List<Delivery> findByOwner(User owner);
    @Query("SELECT d.deliveryId FROM Delivery d WHERE d.deliveryId IN :deliveryIds")
    List<Long> findExistingDeliveryIds(@Param("deliveryIds") List<Long> deliveryIds);;
}
