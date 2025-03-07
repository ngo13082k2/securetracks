package org.example.securetracks.repository;

import org.example.securetracks.model.DeliveryDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DeliveryDetailRepository extends JpaRepository<DeliveryDetail, Long> {
    List<DeliveryDetail> findByMasterDataDelivery_Delivery_DeliveryId(Long deliveryId);
}
