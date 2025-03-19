package org.example.securetracks.service.implement;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.securetracks.dto.DeliveryDto;
import org.example.securetracks.dto.MasterDataDeliveryDto;
import org.example.securetracks.dto.MasterDataDto;
import org.example.securetracks.model.*;
import org.example.securetracks.model.enums.CalculationUnit;
import org.example.securetracks.repository.DeliveryDetailRepository;
import org.example.securetracks.repository.DeliveryRepository;
import org.example.securetracks.repository.MasterDataDeliveryRepository;
import org.example.securetracks.repository.MasterDataRepository;
import org.example.securetracks.request.CreateDeliveryRequest;
import org.example.securetracks.service.IDeliveryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class DeliveryService implements IDeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final MasterDataRepository masterDataRepository;
    private final MasterDataDeliveryRepository masterDataDeliveryRepository;
    private final DeliveryDetailRepository deliveryDetailRepository;
    private final UserService userService;
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
    public String createDelivery(CreateDeliveryRequest request) {
        // Lấy user đang đăng nhập
        User currentUser = userService.getCurrentUser();

        // Kiểm tra nếu deliveryId đã tồn tại
        if (deliveryRepository.existsById(request.getDeliveryId())) {
            throw new IllegalArgumentException("Delivery ID đã tồn tại: " + request.getDeliveryId());
        }

        // Lưu Delivery vào database với owner là user đang đăng nhập
        Delivery savedDelivery = deliveryRepository.save(
                Delivery.builder()
                        .deliveryId(request.getDeliveryId()) // Nhận ID từ request
                        .calculationUnit(CalculationUnit.valueOf(request.getCalculationUnit()))
                        .deliveryDate(request.getDeliveryDate())
                        .owner(currentUser) // Gán user hiện tại làm chủ
                        .build()
        );

        // Gán Delivery vào một biến final để dùng trong lambda
        final Delivery deliveryFinal = savedDelivery;

        // Tạo danh sách MasterDataDelivery
        List<MasterDataDelivery> masterDataDeliveries = request.getItems().stream().map(item -> {
            MasterData masterData = masterDataRepository.findById(item.getItemId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy MasterData với ID: " + item.getItemId()));

            return MasterDataDelivery.builder()
                    .delivery(deliveryFinal)  // Dùng biến final
                    .masterData(masterData)
                    .quantity(item.getQuantity())
                    .manufaturingDate(item.getManufacturingDate())
                    .expirationDate(item.getExpireDate())
                    .batch(item.getBatch())
                    .build();
        }).collect(Collectors.toList());

        int totalQuantity = masterDataDeliveries.stream().mapToInt(MasterDataDelivery::getQuantity).sum();
        savedDelivery.setQuantity(totalQuantity);

        masterDataDeliveryRepository.saveAll(masterDataDeliveries);
        deliveryRepository.save(savedDelivery);

        // Tạo DeliveryDetail từ MasterDataDelivery
        List<DeliveryDetail> details = masterDataDeliveries.stream().map(mdd -> {
            int totalBottles = mdd.getMasterData().getSpec() * mdd.getMasterData().getPer() * mdd.getQuantity();
            return DeliveryDetail.builder()
                    .masterDataDelivery(mdd)
                    .totalBottles(totalBottles)
                    .build();
        }).collect(Collectors.toList());

        deliveryDetailRepository.saveAll(details);

        return "Đã tạo Delivery thành công với ID: " + savedDelivery.getDeliveryId();
    }




    @Transactional
    public List<DeliveryDto> saveAll(List<DeliveryDto> deliveryDtos) {
        List<Delivery> deliveries = deliveryDtos.stream().map(this::mapToEntity).collect(Collectors.toList());
        List<Delivery> savedDeliveries = deliveryRepository.saveAll(deliveries);
        return savedDeliveries.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    public List<DeliveryDto> getAllDeliveries() {
        // Lấy user đang đăng nhập
        User currentUser = userService.getCurrentUser();

        // Lấy danh sách Delivery theo ownerId
        List<Delivery> deliveries = deliveryRepository.findByOwner(currentUser);

        return deliveries.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
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
                masterDataDtos,
                delivery.getQuantity()
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









