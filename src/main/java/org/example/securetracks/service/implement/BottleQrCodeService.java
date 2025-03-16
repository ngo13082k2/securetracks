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
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BottleQrCodeService implements IBottleQrCodeService {

    @Autowired
    private BottleQrCodeRepository bottleQrCodeRepository;

    @Autowired
    private DeliveryDetailRepository deliveryDetailRepository;
    @Autowired
    private QRGenerator qrGenerator;

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

                    // **Tạo dữ liệu QR Code**
                    String qrData = "QR Code: " + qrCode +
                            "\nDelivery Date: " + detail.getMasterDataDelivery().getDelivery().getDeliveryDate() +
                            "\nManufacturing Date: " + detail.getMasterDataDelivery().getManufaturingDate() +
                            "\nExpiration Date: " + detail.getMasterDataDelivery().getExpirationDate() +
                            "\nBatch: " + detail.getMasterDataDelivery().getBatch() +
                            "\nProduct Name: " + detail.getMasterDataDelivery().getMasterData().getName();

                    byte[] qrImage = qrGenerator.generateQRCode(qrData, 200, 200);

                    // **Tạo đối tượng BottleQrCode**
                    BottleQrCode bottleQrCode = BottleQrCode.builder()
                            .qrCode(qrCode)
                            .qrCodeImage(qrImage)
                            .deliveryDetail(detail)
                            .build();

                    newQrCodes.add(bottleQrCode);
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
    public void saveBottleQrCode(BottleQrCode bottleQrCode) {
        bottleQrCodeRepository.save(bottleQrCode);
    }
    public List<Map<String, Object>> getQRCodesByDeliveryId(Long deliveryId) {
        // Lấy danh sách tất cả QR codes theo Delivery ID
        List<BottleQrCode> qrCodes = bottleQrCodeRepository.findByDeliveryDetail_MasterDataDelivery_Delivery_DeliveryId(deliveryId);

        // Sử dụng Map để nhóm QR codes theo từng sản phẩm
        Map<Long, Map<String, Object>> groupedItems = new HashMap<>();

        for (BottleQrCode qr : qrCodes) {
            MasterDataDelivery masterDataDelivery = qr.getDeliveryDetail().getMasterDataDelivery();
            Long itemId = masterDataDelivery.getMasterData().getItem(); // ID của sản phẩm

            // Nếu sản phẩm chưa có trong nhóm, tạo mới
            if (!groupedItems.containsKey(itemId)) {
                Map<String, Object> itemData = new HashMap<>();
                itemData.put("itemId", itemId);
                itemData.put("productName", masterDataDelivery.getMasterData().getName());
                itemData.put("qrCodes", new ArrayList<>());

                groupedItems.put(itemId, itemData);
            }

            // Tạo QR code data
            String qrData = "QR Code: " + qr.getQrCode() +
                    "\nDelivery Date: " + masterDataDelivery.getDelivery().getDeliveryDate() +
                    "\nManufacturing Date: " + masterDataDelivery.getManufaturingDate() +
                    "\nExpiration Date: " + masterDataDelivery.getExpirationDate() +
                    "\nBatch: " + masterDataDelivery.getBatch() +
                    "\nProduct Name: " + masterDataDelivery.getMasterData().getName();

            byte[] qrImage = qrGenerator.generateQRCode(qrData, 200, 200);

            Map<String, Object> qrInfo = new HashMap<>();
            qrInfo.put("qrCode", qr.getQrCode());
            qrInfo.put("qrCodeImage", Base64.getEncoder().encodeToString(qrImage)); // Chuyển ảnh thành Base64

            // Thêm QR code vào danh sách của item tương ứng
            List<Map<String, Object>> qrCodeList = (List<Map<String, Object>>) groupedItems.get(itemId).get("qrCodes");
            qrCodeList.add(qrInfo);
        }

        // Chuyển Map thành danh sách
        return new ArrayList<>(groupedItems.values());
    }

}
