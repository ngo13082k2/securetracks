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

    List<OutBound> findBySaleDate(LocalDate saleDate);

    Page<OutBound> findBySaleDateAndDealer(LocalDate saleDate, String dealer, Pageable pageable);

    Page<OutBound> findByDealer(String dealer, Pageable pageable);

    @Query("SELECT o.item, o.itemName, SUM(o.quantity) " +
            "FROM OutBound o " +
            "WHERE (:startDate IS NULL OR :endDate IS NULL OR o.saleDate BETWEEN :startDate AND :endDate) " +
            "GROUP BY o.item, o.itemName")
    Page<Object[]> findItemNamesWithTotal(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);
    Page<OutBound> findByItem(Long item, Pageable pageable);
    @Query("SELECT SUM(o.quantity) FROM OutBound o " +
            "WHERE (:startDate IS NULL OR :endDate IS NULL OR o.saleDate BETWEEN :startDate AND :endDate)")
    Long findTotalQuantity(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    Page<OutBound> findAll(Pageable pageable);
    Page<OutBound> findBySaleDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
    List<OutBound> findBySaleDateBetween(LocalDate startDate, LocalDate endDate);

}
