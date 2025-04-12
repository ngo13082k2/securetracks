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
    List<Inbound> findByImportDateBetween(LocalDate startDate, LocalDate endDate);

    long countByStatus(InboundStatus status);
    @Query("SELECT i.item, i.itemName, SUM(i.quantity) " +
            "FROM Inbound i " +
            "WHERE (:startDate IS NULL OR :endDate IS NULL OR i.importDate BETWEEN :startDate AND :endDate) " +
            "GROUP BY i.item, i.itemName")
    Page<Object[]> findItemNamesWithTotal(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    @Query("SELECT SUM(i.quantity) FROM Inbound i " +
            "WHERE (:startDate IS NULL OR :endDate IS NULL OR i.importDate BETWEEN :startDate AND :endDate)")
    Long findTotalQuantity(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);



    Optional<Inbound> findByQrCode(String qrCode);
    Page<Inbound> findAll(Pageable pageable);
    Page<Inbound> findByImportDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
    @Query("""
    SELECT i.item, i.itemName, SUM(i.quantity)
    FROM Inbound i
    WHERE i.status IN :statuses AND i.importDate <= :targetDate
    GROUP BY i.item, i.itemName
    ORDER BY i.itemName ASC
    """)
    Page<Object[]> findItemStockAsOfDate(@Param("targetDate") LocalDate targetDate,
                                         @Param("statuses") List<InboundStatus> statuses,
                                         Pageable pageable);

    @Query("""
    SELECT SUM(i.quantity)
    FROM Inbound i
    WHERE i.status IN :statuses AND i.importDate <= :targetDate
    """)
    Long findGrandTotalAsOfDate(@Param("targetDate") LocalDate targetDate,
                                @Param("statuses") List<InboundStatus> statuses);
    @Query("""
    SELECT i FROM Inbound i
    WHERE i.status IN :statuses
    AND (:inventoryDate IS NULL OR i.importDate <= :inventoryDate)
""")
    Page<Inbound> findInventoryByStatusAndDate(
            @Param("statuses") List<InboundStatus> statuses,
            @Param("inventoryDate") LocalDate inventoryDate,
            Pageable pageable);
    @Query("""
    SELECT i FROM Inbound i
    WHERE i.status IN :statuses
    AND (:inventoryDate IS NULL OR i.importDate <= :inventoryDate)
""")
    List<Inbound> findAllByStatusAndImportDateBeforeOrEqual(
            @Param("statuses") List<InboundStatus> statuses,
            @Param("inventoryDate") LocalDate inventoryDate);


    boolean existsByQrCode(String qrCode);
}
