package org.example.securetracks.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "master_data")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterData {

    @Id
    private Long item;

    private String name;
    private Integer spec;
    private Integer per;

    @Column(name = "calculation_unit")
    private String calculationUnit;
    @OneToMany(mappedBy = "masterData", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MasterDataDelivery> deliveries = new HashSet<>();

}
