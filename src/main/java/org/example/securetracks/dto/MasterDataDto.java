package org.example.securetracks.dto;

import lombok.*;
import org.example.securetracks.model.enums.CalculationUnit;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterDataDto {
    private Long item;
    private String name;
    private Integer spec;
    private Integer per;
    private String calculationUnit;

}
