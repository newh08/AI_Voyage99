package com.example.Voyage.repository;


import com.example.Voyage.domain.DatabaseMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DatabaseMetadataRepository extends JpaRepository<DatabaseMetadata, Long> {
}
