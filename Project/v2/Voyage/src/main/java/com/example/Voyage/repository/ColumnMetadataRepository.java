package com.example.Voyage.repository;

import com.example.Voyage.domain.ColumnMetadata;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColumnMetadataRepository extends JpaRepository<ColumnMetadata, Long> {
    List<ColumnMetadata> findByTableMetadataId(Long tableId);
    List<ColumnMetadata> findByForeignColumnId(Long foreignColumnId);
}
