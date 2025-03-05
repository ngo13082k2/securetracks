package org.example.securetracks.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MasterDataDeliveryDto {
    private Long item;
    private Integer quantity;
}
