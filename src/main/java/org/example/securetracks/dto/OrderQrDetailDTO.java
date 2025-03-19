package org.example.securetracks.dto;

import lombok.*;
import org.example.securetracks.response.BottleQrCodeResponse;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderQrDetailDTO {
    private Long id;
    private String qrCode;
    private LocalDateTime orderCreationDate;
    private BottleQrCodeResponse bottleInfo;
}
