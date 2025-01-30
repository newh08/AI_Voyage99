package com.example.Voyage.service;

import com.example.Voyage.domain.ColumnMetadata;
import com.example.Voyage.domain.DatabaseMetadata;
import com.example.Voyage.domain.TableMetadata;
import com.example.Voyage.repository.DatabaseMetadataRepository;
import com.example.Voyage.repository.TableMetadataRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DatabaseService {
    private final DatabaseMetadataRepository dbRepository;
    private final TableMetadataRepository tableRepository;


    // 1) DB 생성
    public DatabaseMetadata createDatabase(String dbName) {
        DatabaseMetadata dbMetadata = new DatabaseMetadata();
        dbMetadata.setDbName(dbName);
        return dbRepository.save(dbMetadata);
    }

    // 2) 모든 DB 조회
    public List<DatabaseMetadata> getAllDatabases() {
        return dbRepository.findAll();
    }

    // 3) 특정 DB 조회
    public DatabaseMetadata getDatabaseById(Long dbId) {
        return dbRepository.findById(dbId)
                .orElseThrow(() -> new IllegalArgumentException("Database not found. ID: " + dbId));
    }

    // 4) DB 삭제
    public void deleteDatabase(Long dbId) {
        dbRepository.deleteById(dbId);
    }

    // ✅ 스키마를 텍스트 형태로 변환 (Primary Key 정보 제외)
    public String generateSchemaText(Long dbId) {
        Optional<List<TableMetadata>> tablesOpt = tableRepository.findByDatabaseId(dbId);

        if (tablesOpt.isEmpty()) {
            return "No tables found.";
        }

        List<TableMetadata> tables = tablesOpt.get();
        StringBuilder schemaText = new StringBuilder();

        for (TableMetadata table : tables) {
            schemaText.append("\"").append(table.getTableName()).append("\" ");

            // ✅ 컬럼 추가 (Primary Key 정보 제거)
            String columnPart = table.getColumns().stream()
                    .map(col -> "\"" + col.getColumnName() + "\"")
                    .collect(Collectors.joining(", "));
            schemaText.append(columnPart);

            // ✅ FK 추가
            List<ColumnMetadata> foreignKeys = table.getColumns().stream()
                    .filter(col -> col.getForeignTable() != null && col.getForeignColumn() != null)
                    .collect(Collectors.toList());

            if (!foreignKeys.isEmpty()) {
                schemaText.append(",\n    foreign_key: ");
                schemaText.append(
                        foreignKeys.stream()
                                .map(fk -> "\"" + fk.getColumnName() + "\" from \"" + fk.getForeignTable().getTableName() + "\", \"" + fk.getForeignColumn().getColumnName() + "\"")
                                .collect(Collectors.joining(",\n                 "))
                );
            }

            schemaText.append(" [SEP] \n");
        }

        return schemaText.toString();
    }
}