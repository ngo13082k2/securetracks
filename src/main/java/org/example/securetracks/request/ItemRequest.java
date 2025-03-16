package org.example.securetracks.request;

import lombok.*;

import java.time.LocalDate;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class ItemRequest {
    private String batch;
    private LocalDate manufacturingDate;
    private LocalDate expireDate;
    private Long itemId;
    private int quantity;
}
