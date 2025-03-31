package org.example.securetracks.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OutboundDetailDTO {
    private Long id;
    private String masterDataName;
    private LocalDate expirationDate;
    private String batch;
    private String dealer;
}
