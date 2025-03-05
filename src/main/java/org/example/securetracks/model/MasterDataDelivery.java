package org.example.securetracks.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "master_data_delivery")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MasterDataDelivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    @ManyToOne
    @JoinColumn(name = "master_data_id")
    private MasterData masterData;

    private int quantity;
}
