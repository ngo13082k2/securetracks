package org.example.securetracks.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class BottleQrCodeResponse {
    private String qrCode;
    private Long deliveryId;
    private Long masterDataId;
    private String masterDataName;
    private LocalDate manufacturingDate;
    private LocalDate expirationDate;
    private String batch;
    private LocalDate deliveryDate;
//    private int totalBottles;
}
