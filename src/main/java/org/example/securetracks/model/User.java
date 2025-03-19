package org.example.securetracks.model;

import jakarta.persistence.*;

import lombok.*;
import org.example.securetracks.model.enums.Role;
import org.example.securetracks.model.enums.Status;

import java.time.LocalDateTime;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "users")

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String email;
    private String fullName;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Enumerated(EnumType.STRING)
    private Status status;
    private LocalDateTime createAt;
    private String phone;

}
