package org.example.securetracks.model;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bottle_qr_codes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BottleQrCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "qr_code", unique = true, nullable = false, length = 255)
    private String qrCode;

    @ManyToOne
    @JoinColumn(name = "master_data_delivery_cetail_id", nullable = false)
    private DeliveryDetail deliveryDetail;
}
