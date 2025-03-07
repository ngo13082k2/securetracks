package org.example.securetracks.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "delivery_detail")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "master_data_delivery_id", nullable = false)
    private MasterDataDelivery masterDataDelivery;

    private int totalBottles;
}
