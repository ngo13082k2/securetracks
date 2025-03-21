package org.example.securetracks.repository;

import org.example.securetracks.dto.DeliveryDetailDto;
import org.example.securetracks.model.MasterDataDelivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MasterDataDeliveryRepository extends JpaRepository<MasterDataDelivery, Long> {
    List<MasterDataDelivery> findByDelivery_DeliveryId(Long deliveryId);
    boolean existsByDelivery_DeliveryId(Long deliveryId);

}
