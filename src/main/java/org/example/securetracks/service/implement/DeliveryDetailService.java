package org.example.securetracks.service.implement;

import lombok.RequiredArgsConstructor;
import org.example.securetracks.dto.DeliveryDetailDto;
import org.example.securetracks.model.DeliveryDetail;
import org.example.securetracks.model.MasterDataDelivery;
import org.example.securetracks.repository.DeliveryDetailRepository;
import org.example.securetracks.repository.MasterDataDeliveryRepository;
import org.example.securetracks.repository.MasterDataRepository;
import org.example.securetracks.service.IDeliveryDetailService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryDetailService implements IDeliveryDetailService {

    private final DeliveryDetailRepository deliveryDetailRepository;
    private final MasterDataDeliveryRepository masterDataDeliveryRepository;
    private final MasterDataRepository masterDataRepository;

    public String generateDeliveryDetails(Long deliveryId) {
        List<MasterDataDelivery> masterDataDeliveries = masterDataDeliveryRepository.findByDelivery_DeliveryId(deliveryId);

        if (masterDataDeliveries.isEmpty()) {
            return "Không tìm thấy dữ liệu MasterDataDelivery cho Delivery ID: " + deliveryId;
        }

        List<DeliveryDetail> details = masterDataDeliveries.stream().map(mdd -> {
            int totalBottles = mdd.getMasterData().getSpec() * mdd.getMasterData().getPer() * mdd.getQuantity();
            return DeliveryDetail.builder()
                    .masterDataDelivery(mdd)
                    .totalBottles(totalBottles)
                    .build();
        }).collect(Collectors.toList());

        deliveryDetailRepository.saveAll(details);
        return "Đã tạo DeliveryDetail thành công cho Delivery ID: " + deliveryId;
    }

    public List<DeliveryDetailDto> getDeliveryDetailsByDeliveryId(Long deliveryId) {
        return deliveryDetailRepository.findByMasterDataDelivery_Delivery_DeliveryId(deliveryId)
                .stream()
                .map(detail -> {
                    MasterDataDelivery masterDataDelivery = detail.getMasterDataDelivery();
                    return DeliveryDetailDto.builder()
                            .item(masterDataDelivery.getMasterData().getItem())
                            .quantity(detail.getTotalBottles()) // Số lượng
                            .masterDataName(masterDataDelivery.getMasterData().getName()) // Tên sản phẩm
                            .manufacturingDate(masterDataDelivery.getManufaturingDate().toString()) // Ngày sản xuất
                            .expireDate(masterDataDelivery.getExpirationDate().toString()) // Hạn sử dụng
                            .batch(masterDataDelivery.getBatch()) // Số lô
                            .build();
                })
                .collect(Collectors.toList());
    }

}
