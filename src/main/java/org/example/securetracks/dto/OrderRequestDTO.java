package org.example.securetracks.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRequestDTO {
    private String phoneNumber;
    private String customerName;
    private String province;
    private String district;
    private String ward;
    private String street;
    private String addressDetail;
    private List<String> qrCodes;
}

