package com.example.Voyage.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity @Setter @Getter
@Table(name = "table_metadata")
public class TableMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 Database에 소속된 테이블인가
    @ManyToOne
    @JoinColumn(name = "database_id", nullable = false)
    @JsonIgnore
    private DatabaseMetadata database;

    @Column(nullable = false)
    private String tableName;

    @OneToMany(mappedBy = "tableMetadata", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ColumnMetadata> columns;

    @OneToMany(mappedBy = "fromTable", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ForeignKeyMetadata> foreignKeysFrom;

    // Getter, Setter, etc.
}