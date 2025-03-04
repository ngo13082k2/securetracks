package org.example.securetracks.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterDataDto {
    private String item;
    private String name;
    private Integer spec;
    private Integer per;
    private String calculationUnit;
}
