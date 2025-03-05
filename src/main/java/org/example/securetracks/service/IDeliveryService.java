package org.example.securetracks.service;

import org.example.securetracks.dto.DeliveryDto;

import java.util.List;

public interface IDeliveryService {
    DeliveryDto create(DeliveryDto dto);
    List<DeliveryDto> saveAll(List<DeliveryDto> deliveryDtos);
}
