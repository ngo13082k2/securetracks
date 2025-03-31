package org.example.securetracks.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.securetracks.model.enums.InboundStatus;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Inbound {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "import_date", nullable = false)
    private LocalDate importDate;

    @ManyToOne
    @JoinColumn(name = "delivery_id", nullable = false)
    private Delivery delivery;

    @Column(name = "supplier", nullable = false)
    private String supplier;

    @Column(name = "item", nullable = false)
    private Long item;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "qr_code", nullable = false)
    private String qrCode;

    @Column(name = "manufacturing_date", nullable = false)
    private LocalDate manufacturingDate;

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @Column(name = "batch", nullable = false)
    private String batch;
    @Column(name = "quantity", nullable = false)
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InboundStatus status;
}
