package com.example.Voyage.service;

import com.example.Voyage.domain.DatabaseMetadata;
import com.example.Voyage.domain.TableMetadata;
import com.example.Voyage.repository.DatabaseMetadataRepository;
import com.example.Voyage.repository.TableMetadataRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class TableService {

    private final TableMetadataRepository tableRepo;
    private final DatabaseMetadataRepository dbRepo;

    public TableService(TableMetadataRepository tableRepo, DatabaseMetadataRepository dbRepo) {
        this.tableRepo = tableRepo;
        this.dbRepo = dbRepo;
    }

    // 1) 테이블 생성
    public TableMetadata createTable(Long dbId, String tableName) {
        DatabaseMetadata database = dbRepo.findById(dbId)
                .orElseThrow(() -> new IllegalArgumentException("Database not found. ID: " + dbId));

        TableMetadata table = new TableMetadata();
        table.setTableName(tableName);
        table.setDatabase(database);

        log.info(tableName);

        return tableRepo.save(table);
    }

    // 2) 특정 DB의 테이블 목록 조회
    public List<TableMetadata> getTablesByDatabase(Long dbId) {
        DatabaseMetadata database = dbRepo.findById(dbId)
                .orElseThrow(() -> new IllegalArgumentException("Database not found. ID: " + dbId));
        return database.getTables();
    }

    // 3) 테이블 상세 조회
    public TableMetadata getTableById(Long tableId) {
        return tableRepo.findById(tableId)
                .orElseThrow(() -> new IllegalArgumentException("Table not found. ID: " + tableId));
    }

    // 4) 테이블 삭제
    public void deleteTable(Long tableId) {
        tableRepo.deleteById(tableId);
    }
}