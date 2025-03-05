package org.example.securetracks.repository;

import org.example.securetracks.model.MasterData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MasterDataRepository extends JpaRepository<MasterData, Long> {
    Optional<MasterData> findByItem(Long item);
}
