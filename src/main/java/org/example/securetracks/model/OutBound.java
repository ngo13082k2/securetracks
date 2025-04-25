package org.example.securetracks.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "outbound")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutBound {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sale_date", nullable = false)
    private LocalDate saleDate;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;
    @Column(name = "qrcode", nullable = false)
    private String qrcode;
    @Column(name = "item", nullable = false)
    private Long item;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "manufacturing_date", nullable = false)
    private LocalDate manufacturingDate;

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @Column(name = "batch", nullable = false)
    private String batch;

    @Column(name = "dealer", nullable = false)
    private String dealer;

    @Column(name = "quantity", nullable = false)
    private int quantity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
