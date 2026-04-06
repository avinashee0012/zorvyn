package com.avinashee0012.zorvyn.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.avinashee0012.zorvyn.domain.enums.RecordType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "financial_records")
@Getter
@NoArgsConstructor
public class FinancialRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RecordType type;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private LocalDate date;

    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(nullable = false)
    private boolean deleted = false;

    public FinancialRecord(
        BigDecimal amount,
        RecordType type,
        String category,
        LocalDate date,
        String description,
        User createdBy
    ) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Category cannot be null or blank");
        }
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        if (createdBy == null) {
            throw new IllegalArgumentException("CreatedBy cannot be null");
        }
        if (description != null && description.length() > 500) {
            throw new IllegalArgumentException("Description cannot exceed 500 characters");
        }
        this.amount = amount;
        this.type = type;
        this.category = category.trim();
        this.date = date;
        this.description = description != null ? description.trim() : null;
        this.createdBy = createdBy;
    }

    public void update(
        BigDecimal amount,
        RecordType type,
        String category,
        LocalDate date,
        String description
    ) {
        if (amount != null) {
            this.amount = amount;
        }
        if (type != null) {
            this.type = type;
        }
        if (category != null && !category.isBlank()) {
            this.category = category.trim();
        }
        if (date != null) {
            this.date = date;
        }
        if (description != null) {
            if (description.length() > 500) {
                throw new IllegalArgumentException("Description cannot exceed 500 characters");
            }
            this.description = description.trim();
        }
    }

    public void softDelete() {
        this.deleted = true;
    }
}
