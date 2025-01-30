package com.example.Voyage.repository;

import com.example.Voyage.domain.TableMetadata;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TableMetadataRepository extends JpaRepository<TableMetadata, Long> {
    Optional<List<TableMetadata>> findByDatabaseId(Long dbId);
}