package com.avinashee0012.zorvyn.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.avinashee0012.zorvyn.domain.entity.FinancialRecord;
import com.avinashee0012.zorvyn.domain.enums.RecordType;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {

    Page<FinancialRecord> findByDeletedFalse(Pageable pageable);

    @Query("""
        select r from FinancialRecord r
        where r.deleted = false
          and (
            lower(r.category) like lower(concat('%', :keyword, '%'))
            or lower(coalesce(r.description, '')) like lower(concat('%', :keyword, '%'))
          )
        """)
    Page<FinancialRecord> searchActiveRecords(
        @Param("keyword") String keyword,
        Pageable pageable
    );

    @Query("""
        select r from FinancialRecord r
        where r.deleted = false
          and (:date is null or r.date = :date)
          and (:category is null or lower(r.category) = lower(:category))
          and (:type is null or r.type = :type)
        """)
    Page<FinancialRecord> filterRecords(
        @Param("date") LocalDate date,
        @Param("category") String category,
        @Param("type") RecordType type,
        Pageable pageable
    );

    @Query("""
        select coalesce(sum(r.amount), 0) from FinancialRecord r
        where r.deleted = false and r.type = :type
        """)
    BigDecimal sumAmountByType(@Param("type") RecordType type);

    long countByDeletedFalse();

    List<FinancialRecord> findTop10ByDeletedFalseOrderByDateDescCreatedAtDesc();

    @Query("""
        select r.category, r.type, coalesce(sum(r.amount), 0)
        from FinancialRecord r
        where r.deleted = false
          and (:type is null or r.type = :type)
        group by r.category, r.type
        order by coalesce(sum(r.amount), 0) desc
        """)
    List<Object[]> summarizeByCategory(@Param("type") RecordType type);
}
