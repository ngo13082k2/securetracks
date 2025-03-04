package org.example.securetracks.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "master_data")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterData {

    @Id
    private Integer item;

    private String name;
    private Integer spec;
    private Integer per;

    @Column(name = "calculation_unit")
    private String calculationUnit;
}
