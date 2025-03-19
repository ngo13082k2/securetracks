package org.example.securetracks.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailDTO {
    private Long id;
    private int totalProducts;
    private String customerPhoneNumber; // Thêm số điện thoại khách hàng
    private String customerName; // Thêm tên khách hàng
   private List<OrderQrDetailDTO> qrDetails;
}
