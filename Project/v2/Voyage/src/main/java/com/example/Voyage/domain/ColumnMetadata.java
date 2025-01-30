package com.example.Voyage.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity @Setter @Getter
@Table(name = "column_metadata")
public class ColumnMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String columnName;

    @ManyToOne
    @JoinColumn(name = "table_id", nullable = false)
    @JsonIgnore
    private TableMetadata tableMetadata; // 컬럼이 속한 테이블

    // 📌 FK 설정 (외래키로 연결될 테이블과 컬럼)
    @ManyToOne
    @JoinColumn(name = "foreign_table_id")
    @JsonIgnore
    private TableMetadata foreignTable;

    @ManyToOne
    @JoinColumn(name = "foreign_column_id")
    @JsonIgnore
    private ColumnMetadata foreignColumn;

    public ColumnMetadata() {
    }
    // 필요하다면 nullable, unique 등 추가 필드
}
