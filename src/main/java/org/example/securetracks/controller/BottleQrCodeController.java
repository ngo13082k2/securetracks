package org.example.securetracks.controller;

import org.example.securetracks.model.BottleQrCode;
import org.example.securetracks.response.BottleQrCodeResponse;
import org.example.securetracks.service.implement.BottleQrCodeService;
import org.example.securetracks.service.implement.QRGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/qrcode")
public class BottleQrCodeController {

    @Autowired
    private BottleQrCodeService bottleQrCodeService;
    @Autowired
    private QRGenerator qrGenerator;

    @PostMapping("/generate")
    public String generateCodes() {
        bottleQrCodeService.generateQrCodesForAllDeliveries();
        return "QR Codes generated successfully!";
    }
    @GetMapping("/{code}")
    public ResponseEntity<BottleQrCodeResponse> getBottleInfo(@PathVariable String code) {
        BottleQrCodeResponse response = bottleQrCodeService.getBottleInfoByQrCode(code);
        return ResponseEntity.ok(response);
    }
    @GetMapping("generateQR/{code}")
    public ResponseEntity<byte[]> generateQRCode(@PathVariable String code) {
        BottleQrCode bottleQrCode = bottleQrCodeService.getBottleQrCodeByQrCode(code);
        if (bottleQrCode.getQrCodeImage() != null) {
            return ResponseEntity.ok().contentType(org.springframework.http.MediaType.IMAGE_PNG).body(bottleQrCode.getQrCodeImage());
        }
        String qrData = "QR Code: " + bottleQrCode.getQrCode() +
                "\nDelivery Date: " + bottleQrCode.getDeliveryDetail().getMasterDataDelivery().getDelivery().getDeliveryDate() +
                "\nManufacturing Date: " + bottleQrCode.getDeliveryDetail().getMasterDataDelivery().getManufaturingDate() +
                "\nExpiration Date: " + bottleQrCode.getDeliveryDetail().getMasterDataDelivery().getExpirationDate() +
                "\nBatch: " + bottleQrCode.getDeliveryDetail().getMasterDataDelivery().getBatch() +
                "\nProduct Name: " + bottleQrCode.getDeliveryDetail().getMasterDataDelivery().getMasterData().getName();

        byte[] qrImage = qrGenerator.generateQRCode(qrData, 200, 200);
        bottleQrCode.setQrCodeImage(qrImage);
        bottleQrCodeService.saveBottleQrCode(bottleQrCode);


        return ResponseEntity.ok().contentType(org.springframework.http.MediaType.IMAGE_PNG).body(qrImage);
    }
    @GetMapping("/delivery/{deliveryId}")
    public ResponseEntity<List<Map<String, Object>>> getQRCodesByDeliveryId(@PathVariable Long deliveryId) throws AccessDeniedException {
        List<Map<String, Object>> qrCodes = bottleQrCodeService.getQRCodesByDeliveryId(deliveryId);

        if (qrCodes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(qrCodes);
    }
    @GetMapping("/delivery/getByItemAndBatch/{deliveryId}")
    public ResponseEntity<List<Map<String, Object>>> getQRCodesByDelivery(
            @PathVariable Long deliveryId,
            @RequestParam Long itemId,
            @RequestParam String batch) throws AccessDeniedException {

        List<Map<String, Object>> qrCodesByBatch = bottleQrCodeService.getQRCodesByDeliverywithItemAndBatch(deliveryId, itemId, batch);

        if (qrCodesByBatch.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(qrCodesByBatch);
    }



}
