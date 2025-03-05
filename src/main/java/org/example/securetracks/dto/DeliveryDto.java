package org.example.securetracks.dto;

import lombok.*;
import org.example.securetracks.model.enums.CalculationUnit;

import java.time.LocalDate;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DeliveryDto {
    private Long deliveryId;
    private CalculationUnit calculationUnit;
    private LocalDate deliveryDate;
    private String batch;
    private LocalDate manufacturingDate;
    private LocalDate expireDate;
    private List<MasterDataDeliveryDto> masterDataItems;
}
