package org.example.securetracks.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerMasterData customer;

    @Column(name = "total_products", nullable = false)
    private int totalProducts;

    @OneToMany(mappedBy = "orderDetail", cascade = CascadeType.ALL)
    private List<OrderQrDetail> qrDetails;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(name = "date_create")
    private LocalDate dateCreate;
    @Column(name = "time_create")
    private LocalTime timeCreate;
}
