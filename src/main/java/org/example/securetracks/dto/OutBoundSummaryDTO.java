package org.example.securetracks.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OutBoundSummaryDTO {
    private Long item;
    private String itemName;
    private Long totalQuantity;
}
