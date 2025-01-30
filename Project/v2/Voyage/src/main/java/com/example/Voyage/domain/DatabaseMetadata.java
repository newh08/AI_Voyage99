package com.example.Voyage.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity @Setter @Getter
@Table(name = "database_metadata")
public class DatabaseMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String dbName;

    @OneToMany(mappedBy = "database", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TableMetadata> tables;

    // Getter, Setter, etc.
}
