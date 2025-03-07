package org.example.securetracks.service.implement;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.securetracks.dto.DeliveryDto;
import org.example.securetracks.dto.MasterDataDeliveryDto;
import org.example.securetracks.dto.MasterDataDto;
import org.example.securetracks.model.Delivery;
import org.example.securetracks.model.MasterData;
import org.example.securetracks.model.MasterDataDelivery;
import org.example.securetracks.repository.DeliveryRepository;
import org.example.securetracks.repository.MasterDataRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final MasterDataRepository masterDataRepository;

    @Transactional
    public DeliveryDto create(DeliveryDto dto) {
        List<MasterData> masterDataList = masterDataRepository.findAllById(
                dto.getMasterDataItems().stream().map(MasterDataDeliveryDto::getItem).collect(Collectors.toList())
        );

        if (masterDataList.size() != dto.getMasterDataItems().size()) {
            throw new IllegalArgumentException("Một hoặc nhiều MasterData không tồn tại!");
        }

        int totalQuantity = dto.getMasterDataItems().stream()
                .mapToInt(MasterDataDeliveryDto::getQuantity)
                .sum();

        Delivery delivery = Delivery.builder()
                .quantity(totalQuantity)
                .calculationUnit(dto.getCalculationUnit())
                .deliveryDate(dto.getDeliveryDate())
                .build();

        // Chuyển đổi Set<MasterData> thành List<MasterDataDelivery>
        List<MasterDataDelivery> masterDataDeliveries = new ArrayList<>();
        for (MasterDataDeliveryDto masterDataDto : dto.getMasterDataItems()) {
            MasterData masterData = masterDataList.stream()
                    .filter(md -> md.getItem().equals(masterDataDto.getItem()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("MasterData không tồn tại: " + masterDataDto.getItem()));

            MasterDataDelivery masterDataDelivery = MasterDataDelivery.builder()
                    .delivery(delivery)
                    .masterData(masterData)
                    .quantity(masterDataDto.getQuantity())
                    .build();

            masterDataDeliveries.add(masterDataDelivery);
        }

        delivery.setMasterDataDeliveries(masterDataDeliveries);

        Delivery savedDelivery = deliveryRepository.save(delivery);

        return mapToDto(savedDelivery);
    }


    @Transactional
    public List<DeliveryDto> saveAll(List<DeliveryDto> deliveryDtos) {
        List<Delivery> deliveries = deliveryDtos.stream().map(this::mapToEntity).collect(Collectors.toList());
        List<Delivery> savedDeliveries = deliveryRepository.saveAll(deliveries);
        return savedDeliveries.stream().map(this::mapToDto).collect(Collectors.toList());
    }


    private DeliveryDto mapToDto(Delivery delivery) {
        List<MasterDataDeliveryDto> masterDataDtos = delivery.getMasterDataDeliveries().stream()
                .map(masterData -> new MasterDataDeliveryDto(
                        masterData.getId(),
                        0,
                        masterData.getManufaturingDate(),
                        masterData.getExpirationDate(),
                        masterData.getBatch()
                ))
                .collect(Collectors.toList());

        return new DeliveryDto(
                delivery.getDeliveryId(),
                delivery.getCalculationUnit(),
                delivery.getDeliveryDate(),
                masterDataDtos
        );
    }


    private Delivery mapToEntity(DeliveryDto dto) {
        // Lấy danh sách MasterData từ DB theo item ID
        List<MasterData> masterDataList = masterDataRepository.findAllById(
                dto.getMasterDataItems().stream().map(MasterDataDeliveryDto::getItem).collect(Collectors.toList())
        );

        // Tạo danh sách MasterDataDelivery từ MasterData và quantity trong DTO
        List<MasterDataDelivery> masterDataDeliveries = dto.getMasterDataItems().stream()
                .map(masterDataDto -> {
                    MasterData masterData = masterDataList.stream()
                            .filter(md -> md.getItem().equals(masterDataDto.getItem()))
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("MasterData không tồn tại: " + masterDataDto.getItem()));

                    return MasterDataDelivery.builder()
                            .masterData(masterData)
                            .quantity(masterDataDto.getQuantity())
                            .build();
                })
                .collect(Collectors.toList());

        int totalQuantity = masterDataDeliveries.stream()
                .mapToInt(MasterDataDelivery::getQuantity)
                .sum();

        Delivery delivery = Delivery.builder()
                .quantity(totalQuantity)
                .calculationUnit(dto.getCalculationUnit())
                .deliveryDate(dto.getDeliveryDate())
                .masterDataDeliveries(masterDataDeliveries)
                .build();

        masterDataDeliveries.forEach(mdDelivery -> mdDelivery.setDelivery(delivery));

        return delivery;
    }

}









