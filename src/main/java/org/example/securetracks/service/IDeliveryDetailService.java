package org.example.securetracks.service;

import org.example.securetracks.dto.DeliveryDetailDto;

import java.util.List;

public interface IDeliveryDetailService {
    String generateDeliveryDetails(Long deliveryId);
    List<DeliveryDetailDto> getDeliveryDetailsByDeliveryId(Long deliveryId);
}
