package org.example.securetracks.service.implement;

import org.example.securetracks.model.*;
import org.example.securetracks.repository.BottleQrCodeRepository;
import org.example.securetracks.repository.DeliveryDetailRepository;
import org.example.securetracks.repository.DeliveryRepository;
import org.example.securetracks.response.BottleQrCodeResponse;
import org.example.securetracks.service.IBottleQrCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.AccessDeniedException;
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
    @Autowired
    private UserService userService;
    @Autowired
    private DeliveryRepository deliveryRepository;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int QR_LENGTH = 10;

    public void generateQrCodesForAllDeliveries() {
        List<DeliveryDetail> deliveryDetails = deliveryDetailRepository.findAll();

        // Kiểm tra những ID đã có QR để tránh trùng
        Set<Long> existingIds = bottleQrCodeRepository.findAll()
                .stream()
                .map(b -> b.getDeliveryDetail().getMasterDataDelivery().getId())
                .collect(Collectors.toSet());

        List<BottleQrCode> batch = new ArrayList<>();
        SecureRandom random = new SecureRandom();
        int batchSize = 500;

        for (DeliveryDetail detail : deliveryDetails) {
            if (!existingIds.contains(detail.getMasterDataDelivery().getId())) {
                for (int i = 0; i < detail.getTotalBottles(); i++) {
                    String qrCode = generateRandomQrCode(random);

                    String qrData = "QR Code: " + qrCode +
                            "\nItem: " + detail.getMasterDataDelivery().getMasterData().getItem() +
                            "\nManufacturing Date: " + detail.getMasterDataDelivery().getManufaturingDate() +
                            "\nExpiration Date: " + detail.getMasterDataDelivery().getExpirationDate() +
                            "\nBatch: " + detail.getMasterDataDelivery().getBatch() +
                            "\nProduct Name: " + detail.getMasterDataDelivery().getMasterData().getName() +
                            "\nOwerName: " + detail.getMasterDataDelivery().getDelivery().getOwner().getFullName();

                    byte[] qrImage = qrGenerator.generateQRCode(qrData, 200, 200);

                    BottleQrCode bottleQrCode = BottleQrCode.builder()
                            .qrCode(qrCode)
                            .qrCodeImage(qrImage)
                            .deliveryDetail(detail)
                            .build();

                    batch.add(bottleQrCode);

                    if (batch.size() >= batchSize) {
                        bottleQrCodeRepository.saveAll(new ArrayList<>(batch));
                        batch.clear();
                    }
                }
            }
        }

        if (!batch.isEmpty()) {
            bottleQrCodeRepository.saveAll(batch);
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
                .owner(delivery.getOwner().getFullName())
                .build();
    }
    public BottleQrCode getBottleQrCodeByQrCode(String qrCode) {
        return bottleQrCodeRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new RuntimeException("QR Code không tồn tại"));
    }
    public void saveBottleQrCode(BottleQrCode bottleQrCode) {
        bottleQrCodeRepository.save(bottleQrCode);
    }
    public List<Map<String, Object>> getQRCodesByDeliveryId(Long deliveryId) throws AccessDeniedException {
        // Lấy user đang đăng nhập
        User currentUser = userService.getCurrentUser();

        // Lấy Delivery theo ID
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Delivery với ID: " + deliveryId));

        // Kiểm tra quyền truy cập (chỉ chủ sở hữu mới được xem)
        if (!delivery.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Bạn không có quyền truy cập dữ liệu này!");
        }

        // Lấy danh sách tất cả QR codes theo Delivery ID
        List<BottleQrCode> qrCodes = bottleQrCodeRepository.findByDeliveryDetail_MasterDataDelivery_Delivery_DeliveryId(deliveryId);

        // Sử dụng Map để nhóm QR codes theo (itemId + batch)
        Map<String, Map<String, Object>> groupedItems = new HashMap<>();

        for (BottleQrCode qr : qrCodes) {
            MasterDataDelivery masterDataDelivery = qr.getDeliveryDetail().getMasterDataDelivery();
            Long itemId = masterDataDelivery.getMasterData().getItem(); // ID của sản phẩm
            String batch = masterDataDelivery.getBatch(); // Batch của sản phẩm
            String key = itemId + "_" + batch; // Dùng key kết hợp itemId + batch

            // Nếu key chưa tồn tại, tạo mới
            if (!groupedItems.containsKey(key)) {
                Map<String, Object> itemData = new HashMap<>();
                itemData.put("itemId", itemId);
                itemData.put("productName", masterDataDelivery.getMasterData().getName());
                itemData.put("batch", batch);
                itemData.put("qrCodes", new ArrayList<>());

                groupedItems.put(key, itemData);
            }

            // Tạo QR code data
            String qrData = "QR Code: " + qr.getQrCode() +
                    "\nItem: " + masterDataDelivery.getMasterData().getItem() +
                    "\nManufacturing Date: " + masterDataDelivery.getManufaturingDate() +
                    "\nExpiration Date: " + masterDataDelivery.getExpirationDate() +
                    "\nBatch: " + batch +
                    "\nProduct Name: " + masterDataDelivery.getMasterData().getName() +
                    "\nOwner Name: " + masterDataDelivery.getDelivery().getOwner().getFullName();

            byte[] qrImage = qrGenerator.generateQRCode(qrData, 200, 200);

            Map<String, Object> qrInfo = new HashMap<>();
            qrInfo.put("qrCode", qr.getQrCode());
            qrInfo.put("qrCodeImage", Base64.getEncoder().encodeToString(qrImage)); // Chuyển ảnh thành Base64

            // Thêm QR code vào danh sách của batch tương ứng
            List<Map<String, Object>> qrCodeList = (List<Map<String, Object>>) groupedItems.get(key).get("qrCodes");
            qrCodeList.add(qrInfo);
        }

        // Chuyển Map thành danh sách
        return new ArrayList<>(groupedItems.values());
    }

    public List<Map<String, Object>> getQRCodesByDeliverywithItemAndBatch(Long deliveryId, Long itemId, String batch) throws AccessDeniedException {
        // Lấy user đang đăng nhập
        User currentUser = userService.getCurrentUser();

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Delivery với ID: " + deliveryId));

        if (!delivery.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Bạn không có quyền truy cập dữ liệu này!");
        }

        // Lấy danh sách tất cả QR codes theo Delivery ID
        List<BottleQrCode> qrCodes = bottleQrCodeRepository.findByDeliveryDetail_MasterDataDelivery_Delivery_DeliveryId(deliveryId);

        // Dùng Map để nhóm QR codes theo (itemId + batch)
        Map<String, Map<String, Object>> groupedItems = new HashMap<>();

        for (BottleQrCode qr : qrCodes) {
            MasterDataDelivery masterDataDelivery = qr.getDeliveryDetail().getMasterDataDelivery();
            Long currentItemId = masterDataDelivery.getMasterData().getItem(); // ID của sản phẩm
            String currentBatch = masterDataDelivery.getBatch(); // Batch của sản phẩm

            // Chỉ lấy dữ liệu khi itemId và batch trùng khớp với request
            if (!currentItemId.equals(itemId) || !currentBatch.equals(batch)) {
                continue; // Bỏ qua nếu không khớp
            }

            // Nếu key chưa tồn tại, tạo mới
            String key = currentItemId + "_" + currentBatch;
            if (!groupedItems.containsKey(key)) {
                Map<String, Object> itemData = new HashMap<>();
                itemData.put("itemId", currentItemId);
                itemData.put("productName", masterDataDelivery.getMasterData().getName());
                itemData.put("batch", currentBatch);
                itemData.put("manufacturingDate", masterDataDelivery.getManufaturingDate());
                itemData.put("expirationDate", masterDataDelivery.getExpirationDate());
                itemData.put("qrCodes", new ArrayList<>());

                groupedItems.put(key, itemData);
            }

            // Tạo QR code data
            String qrData = "QR Code: " + qr.getQrCode() +
                    "\nItem: " + masterDataDelivery.getMasterData().getItem() +
                    "\nManufacturing Date: " + masterDataDelivery.getManufaturingDate() +
                    "\nExpiration Date: " + masterDataDelivery.getExpirationDate() +
                    "\nBatch: " + currentBatch +
                    "\nProduct Name: " + masterDataDelivery.getMasterData().getName() +
                    "\nOwner Name: " + masterDataDelivery.getDelivery().getOwner().getFullName();

            byte[] qrImage = qrGenerator.generateQRCode(qrData, 200, 200);

            Map<String, Object> qrInfo = new HashMap<>();
            qrInfo.put("qrCode", qr.getQrCode());
            qrInfo.put("qrCodeImage", Base64.getEncoder().encodeToString(qrImage));

            // Thêm QR code vào danh sách của batch tương ứng
            List<Map<String, Object>> qrCodeList = (List<Map<String, Object>>) groupedItems.get(key).get("qrCodes");
            qrCodeList.add(qrInfo);
        }

        // Chuyển Map thành danh sách
        return new ArrayList<>(groupedItems.values());
    }




}
