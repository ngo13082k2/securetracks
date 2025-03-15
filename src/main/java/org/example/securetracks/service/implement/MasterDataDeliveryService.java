package org.example.securetracks.service.implement;


import org.example.securetracks.dto.MasterDataDeliveryDto;
import org.example.securetracks.model.MasterDataDelivery;
import org.example.securetracks.repository.MasterDataDeliveryRepository;
import org.example.securetracks.service.IMasterDataDeliveryservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MasterDataDeliveryService implements IMasterDataDeliveryservice {

    @Autowired
    private MasterDataDeliveryRepository masterDataDeliveryRepository;

    public List<MasterDataDeliveryDto> getAllByDeliveryId(Long deliveryId) {
        List<MasterDataDelivery> masterDataDeliveries = masterDataDeliveryRepository.findByDelivery_DeliveryId(deliveryId);
        return masterDataDeliveries.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private MasterDataDeliveryDto mapToDto(MasterDataDelivery masterDataDelivery) {
        return MasterDataDeliveryDto.builder()
                .quantity(masterDataDelivery.getQuantity())
                .ManufacturingDate(masterDataDelivery.getManufaturingDate())
                .expirationDate(masterDataDelivery.getExpirationDate())
                .batch(masterDataDelivery.getBatch())
                .build();
    }
}
