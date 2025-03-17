package org.example.securetracks.service;

import org.example.securetracks.model.BottleQrCode;
import org.example.securetracks.response.BottleQrCodeResponse;

import java.util.List;
import java.util.Map;

public interface IBottleQrCodeService {
    void generateQrCodesForAllDeliveries();
    BottleQrCodeResponse getBottleInfoByQrCode(String qrCode);
    BottleQrCode getBottleQrCodeByQrCode(String qrCode);
    List<Map<String, Object>> getQRCodesByDeliverywithItemAndBatch(Long deliveryId, Long itemId, String batch);

    List<Map<String, Object>> getQRCodesByDeliveryId(Long deliveryId);
}
