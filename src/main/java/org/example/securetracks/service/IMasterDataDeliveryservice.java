package org.example.securetracks.service;

import org.example.securetracks.dto.MasterDataDeliveryDto;

import java.util.List;
import java.util.Map;

public interface IMasterDataDeliveryservice {
    List<MasterDataDeliveryDto> getAllByDeliveryId(Long deliveryId);
    List<Map<String, Object>> getItemsAndBatchByDelivery(Long deliveryId);
}
