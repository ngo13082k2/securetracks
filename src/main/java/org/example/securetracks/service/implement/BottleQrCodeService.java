package org.example.securetracks.service.implement;

import org.example.securetracks.model.*;
import org.example.securetracks.repository.BottleQrCodeRepository;
import org.example.securetracks.repository.DeliveryDetailRepository;
import org.example.securetracks.response.BottleQrCodeResponse;
import org.example.securetracks.service.IBottleQrCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BottleQrCodeService implements IBottleQrCodeService {

    @Autowired
    private BottleQrCodeRepository bottleQrCodeRepository;

    @Autowired
    private DeliveryDetailRepository deliveryDetailRepository;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int QR_LENGTH = 10;

    public void generateQrCodesForAllDeliveries() {
        List<DeliveryDetail> deliveryDetails = deliveryDetailRepository.findAll();

        // Lấy danh sách master_data_delivery_id đã có QR Code
        Set<Long> existingIds = bottleQrCodeRepository.findAll()
                .stream()
                .map(b -> b.getDeliveryDetail().getMasterDataDelivery().getId())
                .collect(Collectors.toSet());

        List<BottleQrCode> newQrCodes = new ArrayList<>();
        SecureRandom random = new SecureRandom();

        for (DeliveryDetail detail : deliveryDetails) {
            if (!existingIds.contains(detail.getMasterDataDelivery().getId())) { // Kiểm tra nếu chưa tạo QR
                for (int i = 0; i < detail.getTotalBottles(); i++) {
                    String qrCode = generateRandomQrCode(random);
                    newQrCodes.add(BottleQrCode.builder()
                            .qrCode(qrCode)
                            .deliveryDetail(detail)
                            .build());
                }
            }
        }

        if (!newQrCodes.isEmpty()) {
            bottleQrCodeRepository.saveAll(newQrCodes);
        }
    }

    private String generateRandomQrCode(SecureRandom random) {
        StringBuilder sb = new StringBuilder(QR_LENGTH);
        for (int i = 0; i < QR_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
    public BottleQrCodeResponse getBottleInfoByQrCode(String qrCode) {
        BottleQrCode bottleQrCode = bottleQrCodeRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "QR Code not found"));

        DeliveryDetail deliveryDetail = bottleQrCode.getDeliveryDetail();
        MasterDataDelivery masterDataDelivery = deliveryDetail.getMasterDataDelivery();
        Delivery delivery = masterDataDelivery.getDelivery();
        MasterData masterData = masterDataDelivery.getMasterData();

        return BottleQrCodeResponse.builder()
                .qrCode(qrCode)
                .deliveryId(delivery.getDeliveryId())
                .masterDataId(masterData.getItem())
                .masterDataName(masterData.getName())
                .manufacturingDate(masterDataDelivery.getManufaturingDate())
                .expirationDate(masterDataDelivery.getExpirationDate())
                .batch(masterDataDelivery.getBatch())
                .deliveryDate(delivery.getDeliveryDate())
//                .totalBottles(deliveryDetail.getTotalBottles())
                .build();
    }
    public BottleQrCode getBottleQrCodeByQrCode(String qrCode) {
        return bottleQrCodeRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new RuntimeException("QR Code không tồn tại"));
    }
}
