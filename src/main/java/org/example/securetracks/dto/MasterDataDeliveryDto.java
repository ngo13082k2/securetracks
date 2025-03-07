package org.example.securetracks.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MasterDataDeliveryDto {
    private Long item;
    private Integer quantity;
    private String masterDataName;
    private LocalDate ManufacturingDate;
    private LocalDate expirationDate;
    private String batch;

    public MasterDataDeliveryDto(Long id, int i, LocalDate manufaturingDate, LocalDate expirationDate, String batch) {
    }
}
