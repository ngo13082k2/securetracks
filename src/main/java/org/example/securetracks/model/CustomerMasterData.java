package org.example.securetracks.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "customer_master_data")
@Builder
public class CustomerMasterData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "phone_number",  unique = true)
    private String phoneNumber;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "province")
    private String province;

    @Column(name = "district")
    private String district;

    @Column(name = "ward")
    private String ward;

    @Column(name = "street")
    private String street;

    @Column(name = "address_detail")
    private String addressDetail;

    // Thêm liên kết đến User
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
