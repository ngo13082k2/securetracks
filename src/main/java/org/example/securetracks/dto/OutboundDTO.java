package org.example.securetracks.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OutboundDTO {
    private Long id;
    private Long item;
    private String itemName;
    private String customerName;
    private Long orderId;
    private String qrCode;
    private LocalDate saleDate;
    private LocalDate importDate;
    private LocalDate manufacturingDate;
    private LocalDate expirationDate;
    private String batch;
    private String status;
}
