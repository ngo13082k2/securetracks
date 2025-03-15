package org.example.securetracks.service;

import org.example.securetracks.dto.MasterDataDeliveryDto;

import java.util.List;

public interface IMasterDataDeliveryservice {
    List<MasterDataDeliveryDto> getAllByDeliveryId(Long deliveryId);
}
