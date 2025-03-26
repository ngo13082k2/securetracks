package org.example.securetracks.service.implement;


import org.example.securetracks.dto.MasterDataDeliveryDto;
import org.example.securetracks.model.Delivery;
import org.example.securetracks.model.MasterDataDelivery;
import org.example.securetracks.model.User;
import org.example.securetracks.repository.DeliveryRepository;
import org.example.securetracks.repository.MasterDataDeliveryRepository;
import org.example.securetracks.service.IMasterDataDeliveryservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MasterDataDeliveryService implements IMasterDataDeliveryservice {

    @Autowired
    private MasterDataDeliveryRepository masterDataDeliveryRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private DeliveryRepository deliveryRepository;

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
                .item(masterDataDelivery.getMasterData().getItem())
                .masterDataName(masterDataDelivery.getMasterData().getName())
                .build();
    }
    public List<Map<String, Object>> getItemsAndBatchByDelivery(Long deliveryId) throws AccessDeniedException {
        // Lấy user đang đăng nhập
        User currentUser = userService.getCurrentUser();

        // Lấy Delivery theo ID
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Delivery với ID: " + deliveryId));

        // Kiểm tra quyền truy cập (chỉ chủ sở hữu mới được xem)
        if (!delivery.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Bạn không có quyền truy cập dữ liệu này!");
        }

        // Lấy danh sách MasterDataDelivery thuộc về Delivery ID
        List<MasterDataDelivery> masterDataDeliveries = masterDataDeliveryRepository.findByDelivery_DeliveryId(deliveryId);

        // Dùng Set để tránh trùng lặp
        Set<String> uniqueItems = new HashSet<>();
        List<Map<String, Object>> result = new ArrayList<>();

        for (MasterDataDelivery masterDataDelivery : masterDataDeliveries) {
            Long itemId = masterDataDelivery.getMasterData().getItem();
            String batch = masterDataDelivery.getBatch();
            String key = itemId + "_" + batch;

            if (!uniqueItems.contains(key)) {
                Map<String, Object> itemData = new HashMap<>();
                itemData.put("itemId", itemId);
                itemData.put("productName", masterDataDelivery.getMasterData().getName());
                itemData.put("batch", batch);

                result.add(itemData);
                uniqueItems.add(key);
            }
        }

        return result;
    }


}
