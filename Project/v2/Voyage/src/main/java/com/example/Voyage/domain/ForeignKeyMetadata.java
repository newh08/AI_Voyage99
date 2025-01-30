package com.example.Voyage.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Setter;

@Entity @Setter
@Table(name = "foreign_key_metadata")
public class ForeignKeyMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 외래키를 설정하는 테이블
    @ManyToOne
    @JoinColumn(name = "from_table_id", nullable = false)
    private TableMetadata fromTable;

    // 외래키를 설정하는 컬럼
    @ManyToOne
    @JoinColumn(name = "from_column_id", nullable = false)
    private ColumnMetadata fromColumn;

    // 참조 대상 테이블
    @ManyToOne
    @JoinColumn(name = "to_table_id", nullable = false)
    private TableMetadata toTable;

    // 참조 대상 컬럼
    @ManyToOne
    @JoinColumn(name = "to_column_id", nullable = false)
    private ColumnMetadata toColumn;
}

