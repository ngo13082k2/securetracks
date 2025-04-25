package org.example.securetracks.repository;

import org.example.securetracks.model.OutBound;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface OutBoundRepository extends JpaRepository<OutBound, Long> {


    Page<OutBound> findBySaleDateAndDealer(LocalDate saleDate, String dealer, Pageable pageable);

    Page<OutBound> findByDealer(String dealer, Pageable pageable);
    Page<OutBound> findBySaleDateBetweenAndUserId(LocalDate startDate, LocalDate endDate, Long userId, Pageable pageable);
    List<OutBound> findByUserId(Long userId);

    @Query("""
    SELECT o.item, o.itemName, SUM(o.quantity)
    FROM OutBound o
    WHERE (:startDate IS NULL OR o.saleDate >= :startDate)
      AND (:endDate IS NULL OR o.saleDate <= :endDate)
      AND (:username IS NULL OR o.user.username = :username)
    GROUP BY o.item, o.itemName
    ORDER BY o.itemName ASC
""")
    Page<Object[]> findItemNamesWithTotal(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("username") String username,
            Pageable pageable);
    Page<OutBound> findByItem(Long item, Pageable pageable);
    @Query("""
    SELECT SUM(o.quantity)
    FROM OutBound o
    WHERE (:startDate IS NULL OR o.saleDate >= :startDate)
      AND (:endDate IS NULL OR o.saleDate <= :endDate)
      AND (:username IS NULL OR o.user.username = :username)
""")
    Long findTotalQuantity(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("username") String username);
    Page<OutBound> findAll(Pageable pageable);
    Page<OutBound> findBySaleDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
    List<OutBound> findBySaleDateBetween(LocalDate startDate, LocalDate endDate);
    @Query("""
    SELECT o FROM OutBound o
    WHERE o.saleDate BETWEEN :startDate AND :endDate
    AND o.user.id = :userId
""")
    List<OutBound> findBySaleDateBetweenAndUserId(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("userId") Long userId);

    @Query("SELECT o.item, o.itemName, SUM(o.quantity) " +
            "FROM OutBound o " +
            "WHERE (:startDate IS NULL OR :endDate IS NULL OR o.saleDate BETWEEN :startDate AND :endDate) " +
            "AND o.user.id = :userId " +
            "GROUP BY o.item, o.itemName")
    Page<Object[]> findItemNamesWithTotalByUser(@Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate,
                                                @Param("userId") Long userId,
                                                Pageable pageable);

    @Query("SELECT SUM(o.quantity) FROM OutBound o " +
            "WHERE (:startDate IS NULL OR :endDate IS NULL OR o.saleDate BETWEEN :startDate AND :endDate) " +
            "AND o.user.id = :userId")
    Long findTotalQuantityByUser(@Param("startDate") LocalDate startDate,
                                 @Param("endDate") LocalDate endDate,
                                 @Param("userId") Long userId);

    Page<OutBound> findByUserId(Long userId, Pageable pageable);

    Page<OutBound> findByUserUsernameAndSaleDateBetween(String username, LocalDate startDate, LocalDate endDate, Pageable pageable);

    Page<OutBound> findByUserUsername(String username, Pageable pageable);
    List<OutBound> findByUserUsername(String username);

    List<OutBound> findByUserUsernameAndSaleDateBetween(String username, LocalDate startDate, LocalDate endDate);

}
