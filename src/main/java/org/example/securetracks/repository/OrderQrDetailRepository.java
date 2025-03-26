package org.example.securetracks.repository;

import org.example.securetracks.model.OrderQrDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderQrDetailRepository extends JpaRepository<OrderQrDetail, Long> {
   List<OrderQrDetail> findByQrCodeIn(List<String> qrCodes);
}
