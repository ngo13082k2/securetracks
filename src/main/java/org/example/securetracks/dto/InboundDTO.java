package org.example.securetracks.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class InboundDTO {
    private Long id;
    private Long item;
    private Long deliveryId;
    private String itemName;
    private String supplier;
    private String qrCode;
    private LocalDate importDate;
    private LocalDate manufacturingDate;
    private LocalDate expirationDate;
    private String batch;
    private String status;
    private String userName;
}
