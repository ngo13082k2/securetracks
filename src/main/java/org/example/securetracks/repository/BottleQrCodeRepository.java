package org.example.securetracks.repository;

import org.example.securetracks.model.BottleQrCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BottleQrCodeRepository extends JpaRepository<BottleQrCode, Long> {
    Optional<BottleQrCode> findByQrCode(String qrCode);
    List<BottleQrCode> findByDeliveryDetail_MasterDataDelivery_Delivery_DeliveryId(Long deliveryId);


}
