package org.example.securetracks.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.securetracks.model.enums.CalculationUnit;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Entity
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "delivery")
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deliveryId;
    @Enumerated(EnumType.STRING)
    @Column(name = "calculation_unit")
    private CalculationUnit calculationUnit;
    private LocalDate deliveryDate;
    private int quantity;
    @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MasterDataDelivery> masterDataDeliveries = new ArrayList<>();
}

