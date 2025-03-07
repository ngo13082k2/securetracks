package org.example.securetracks.service;

import org.example.securetracks.model.BottleQrCode;
import org.example.securetracks.response.BottleQrCodeResponse;

public interface IBottleQrCodeService {
    void generateQrCodesForAllDeliveries();
    BottleQrCodeResponse getBottleInfoByQrCode(String qrCode);
    BottleQrCode getBottleQrCodeByQrCode(String qrCode);
}
