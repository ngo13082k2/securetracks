package org.example.securetracks.repository;

import org.example.securetracks.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);

    boolean existsByUsername(String username);
}
