package org.example.securetracks.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerMasterDataDTO {
    private Long id;
    private String phoneNumber;
    private String customerName;
    private String province;
    private String district;
    private String ward;
    private String street;
    private String detailAddress;
}
