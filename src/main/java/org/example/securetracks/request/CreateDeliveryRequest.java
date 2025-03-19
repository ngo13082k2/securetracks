package org.example.securetracks.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateDeliveryRequest {
    private Long deliveryId;
    private String calculationUnit;
    private LocalDate deliveryDate;
    private List<ItemRequest> items;
}
