package org.example.securetracks.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryDetailDto {
    private Long item; // masterDataId
    private Integer quantity; // số lượng
    private String masterDataName; // tên sản phẩm
    private String manufacturingDate; // ngày sản xuất
    private String expireDate; // hạn sử dụng
    private String batch; // số lô
}
