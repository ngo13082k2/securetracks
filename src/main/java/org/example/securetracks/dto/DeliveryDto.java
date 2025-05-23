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
    private List<MasterDataDeliveryDto> masterDataItems;
    private int quantity;
}
