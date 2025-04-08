package org.example.securetracks.repository;

import org.example.securetracks.model.CustomerMasterData;
import org.example.securetracks.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CustomerMasterDataRepository extends JpaRepository<CustomerMasterData, Long> {
    Optional<CustomerMasterData> findByPhoneNumber(String phoneNumber);
    @Query("SELECT c.phoneNumber FROM CustomerMasterData c")
    Set<String> findAllPhoneNumbers();
}