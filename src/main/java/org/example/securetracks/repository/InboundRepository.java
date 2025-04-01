package org.example.securetracks.repository;

import org.example.securetracks.model.Inbound;
import org.example.securetracks.model.User;
import org.example.securetracks.model.enums.InboundStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InboundRepository extends JpaRepository<Inbound, Long> {
    Page<Inbound> findByImportDateAndUser(LocalDate importDate, User user, Pageable pageable);
    Page<Inbound> findByUser(User user, Pageable pageable);
    List<Inbound> findByQrCodeIn(List<String> qrCodes);
    Page<Inbound> findByStatus(InboundStatus status, Pageable pageable);

    long countByStatus(InboundStatus status);
    @Query("SELECT DISTINCT i.itemName FROM Inbound i")
    List<String> findDistinctItemNames();
    Page<Inbound> findByItemNameContainingIgnoreCaseAndStatus(String itemName, InboundStatus status, Pageable pageable);
    @Query("SELECT i.itemName, COUNT(i) FROM Inbound i GROUP BY i.itemName")
    Page<Object[]> findItemNamesWithTotal(Pageable pageable);
    @Query("SELECT i.item, i.itemName, SUM(i.quantity) " +
            "FROM Inbound i " +
            "WHERE (:startDate IS NULL OR :endDate IS NULL OR i.manufacturingDate BETWEEN :startDate AND :endDate) " +
            "GROUP BY i.item, i.itemName")
    Page<Object[]> findItemNamesWithTotal(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    @Query("SELECT SUM(i.quantity) FROM Inbound i " +
            "WHERE (:startDate IS NULL OR :endDate IS NULL OR i.manufacturingDate BETWEEN :startDate AND :endDate)")
    Long findTotalQuantity(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    @Query("SELECT i.item, i.itemName, SUM(i.quantity) " +
            "FROM Inbound i " +
            "WHERE i.status = 'ACTIVE' " +
            "AND (:startDate IS NULL OR :endDate IS NULL OR i.manufacturingDate BETWEEN :startDate AND :endDate) " +
            "GROUP BY i.item, i.itemName")
    Page<Object[]> findItemNamesWithTotalStatus(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    @Query("SELECT SUM(i.quantity) FROM Inbound i " +
            "WHERE i.status = 'ACTIVE' " +
            "AND (:startDate IS NULL OR :endDate IS NULL OR i.manufacturingDate BETWEEN :startDate AND :endDate)")
    Long findTotalQuantityStatus(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    Page<Inbound> findByItemAndStatus(Long item, InboundStatus status, Pageable pageable);
    Optional<Inbound> findByQrCode(String qrCode);

}
