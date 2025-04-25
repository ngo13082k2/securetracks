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
    Page<Inbound> findByUserIdAndImportDateBetween(Long userId, LocalDate startDate, LocalDate endDate, Pageable pageable);
    Page<Inbound> findByUserId(Long userId, Pageable pageable);

    @Query("""
    SELECT i FROM Inbound i
    WHERE i.importDate BETWEEN :startDate AND :endDate
    AND i.user.id = :userId
""")
    List<Inbound> findByImportDateBetweenAndUserId(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("userId") Long userId);

    long countByStatus(InboundStatus status);
    @Query("""
    SELECT i.item, i.itemName, SUM(i.quantity)
    FROM Inbound i
    WHERE (:startDate IS NULL OR :endDate IS NULL OR i.importDate BETWEEN :startDate AND :endDate)
      AND (:username IS NULL OR i.user.username = :username)
    GROUP BY i.item, i.itemName
    ORDER BY i.itemName ASC
""")
    Page<Object[]> findItemNamesWithTotal(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("username") String username,
            Pageable pageable);

    @Query("""
    SELECT SUM(i.quantity)
    FROM Inbound i
    WHERE (:startDate IS NULL OR :endDate IS NULL OR i.importDate BETWEEN :startDate AND :endDate)
      AND (:username IS NULL OR i.user.username = :username)
""")
    Long findTotalQuantity(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("username") String username);





    Optional<Inbound> findByQrCode(String qrCode);
    Page<Inbound> findAll(Pageable pageable);
    Page<Inbound> findByImportDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    Page<Inbound> findByUserUsername(String username, Pageable pageable);

    @Query("""
    SELECT i FROM Inbound i
    WHERE i.importDate BETWEEN :startDate AND :endDate
      AND i.user.username = :username
""")
    Page<Inbound> findByImportDateBetweenAndUsername(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("username") String username,
            Pageable pageable);

    @Query("""
    SELECT i.item, i.itemName, SUM(i.quantity)
    FROM Inbound i
    WHERE i.status IN :statuses
      AND (:targetDate IS NULL OR i.importDate <= :targetDate)
      AND (:username IS NULL OR i.user.username = :username)
    GROUP BY i.item, i.itemName
    ORDER BY i.itemName ASC
""")
    Page<Object[]> findItemStockByOptionalDateAndUsername(
            @Param("targetDate") LocalDate targetDate,
            @Param("statuses") List<InboundStatus> statuses,
            @Param("username") String username,
            Pageable pageable);

    @Query("""
    SELECT SUM(i.quantity)
    FROM Inbound i
    WHERE i.status IN :statuses
      AND (:targetDate IS NULL OR i.importDate <= :targetDate)
      AND (:username IS NULL OR i.user.username = :username)
""")
    Long findGrandTotalByOptionalDateAndUsername(
            @Param("targetDate") LocalDate targetDate,
            @Param("statuses") List<InboundStatus> statuses,
            @Param("username") String username);

    @Query("""
    SELECT i FROM Inbound i
    WHERE i.status IN :statuses
    AND (:inventoryDate IS NULL OR i.importDate <= :inventoryDate)
    AND (:username IS NULL OR i.user.username = :username)
""")
    Page<Inbound> findInventoryByStatusDateAndUsername(
            @Param("statuses") List<InboundStatus> statuses,
            @Param("inventoryDate") LocalDate inventoryDate,
            @Param("username") String username,
            Pageable pageable);

    @Query("SELECT i FROM Inbound i " +
            "WHERE i.user.id = :userId " +
            "AND i.status IN :statuses " +
            "AND (:inventoryDate IS NULL OR i.importDate = :inventoryDate)")
    Page<Inbound> findInventoryByUserAndStatusAndDate(
            @Param("userId") Long userId,
            @Param("statuses") List<InboundStatus> statuses,
            @Param("inventoryDate") LocalDate inventoryDate,
            Pageable pageable
    );
    @Query("""
    SELECT i FROM Inbound i
    WHERE i.status IN :statuses
    AND (:inventoryDate IS NULL OR i.importDate <= :inventoryDate)
    AND (:username IS NULL OR i.user.username = :username)
""")
    List<Inbound> findAllByStatusAndImportDateBeforeOrEqualAndUsername(
            @Param("statuses") List<InboundStatus> statuses,
            @Param("inventoryDate") LocalDate inventoryDate,
            @Param("username") String username);


    boolean existsByQrCode(String qrCode);
    @Query("SELECT i.item, i.itemName, SUM(i.quantity) " +
            "FROM Inbound i " +
            "WHERE (:startDate IS NULL OR :endDate IS NULL OR i.importDate BETWEEN :startDate AND :endDate) " +
            "AND i.user.id = :userId " +
            "GROUP BY i.item, i.itemName")
    Page<Object[]> findItemNamesWithTotalByUser(LocalDate startDate, LocalDate endDate, Long userId, Pageable pageable);

    @Query("SELECT SUM(i.quantity) " +
            "FROM Inbound i " +
            "WHERE (:startDate IS NULL OR :endDate IS NULL OR i.importDate BETWEEN :startDate AND :endDate) " +
            "AND i.user.id = :userId")
    Long findTotalQuantityByUser(LocalDate startDate, LocalDate endDate, Long userId);
    @Query("""
    SELECT i.item, i.itemName, SUM(i.quantity)
    FROM Inbound i
    WHERE i.status IN :statuses
      AND i.importDate <= :targetDate
      AND i.user.id = :userId
    GROUP BY i.item, i.itemName
    ORDER BY i.itemName ASC
    """)
    Page<Object[]> findItemStockAsOfDate(@Param("targetDate") LocalDate targetDate,
                                         @Param("statuses") List<InboundStatus> statuses,
                                         @Param("userId") Long userId,
                                         Pageable pageable);

    @Query("""
    SELECT SUM(i.quantity)
    FROM Inbound i
    WHERE i.status IN :statuses
      AND i.importDate <= :targetDate
      AND i.user.id = :userId
    """)
    Long findGrandTotalAsOfDate(@Param("targetDate") LocalDate targetDate,
                                @Param("statuses") List<InboundStatus> statuses,
                                @Param("userId") Long userId);
    @Query("""
    SELECT i FROM Inbound i
    WHERE i.status IN :statuses
    AND (:inventoryDate IS NULL OR i.importDate <= :inventoryDate)
    AND i.user.id = :userId
""")
    List<Inbound> findAllByStatusAndImportDateBeforeOrEqualByUser(
            @Param("statuses") List<InboundStatus> statuses,
            @Param("inventoryDate") LocalDate inventoryDate,
            @Param("userId") Long userId);


    List<Inbound> findByImportDateBetweenAndUserUsername(LocalDate startDate, LocalDate endDate, String username);

    List<Inbound> findByUserUsername(String username);
}
