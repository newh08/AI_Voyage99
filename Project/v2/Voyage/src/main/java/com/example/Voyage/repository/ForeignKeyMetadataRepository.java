package com.example.Voyage.repository;

import com.example.Voyage.domain.ForeignKeyMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForeignKeyMetadataRepository extends JpaRepository<ForeignKeyMetadata, Long> {
}