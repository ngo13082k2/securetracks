package org.example.securetracks.repository;

import org.example.securetracks.model.CustomerMasterData;
import org.example.securetracks.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerMasterDataRepository extends JpaRepository<CustomerMasterData, Long> {
    Optional<CustomerMasterData> findByPhoneNumber(String phoneNumber);
}