package com.example.Voyage.controller;

import com.example.Voyage.domain.TableMetadata;
import com.example.Voyage.service.TableService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TableController {

    private final TableService tableService;

    public TableController(TableService tableService) {
        this.tableService = tableService;
    }

    // 1) 특정 데이터베이스에 테이블 생성
    @PostMapping("/databases/{dbId}/tables")
    public ResponseEntity<TableMetadata> createTable(
            @PathVariable Long dbId,
            @RequestBody CreateTableRequest request
    ) {
        TableMetadata table = tableService.createTable(dbId, request.getTableName());
        return ResponseEntity.ok(table);
    }

    // 2) 특정 데이터베이스에 속한 테이블 목록 조회
    @GetMapping("/databases/{dbId}/tables")
    public ResponseEntity<List<TableMetadata>> getTables(@PathVariable Long dbId) {
        return ResponseEntity.ok(tableService.getTablesByDatabase(dbId));
    }

    // 3) 특정 테이블 상세 조회
    @GetMapping("/tables/{tableId}")
    public ResponseEntity<TableMetadata> getTableById(@PathVariable Long tableId) {
        return ResponseEntity.ok(tableService.getTableById(tableId));
    }

    // 4) 테이블 삭제
    @DeleteMapping("/tables/{tableId}")
    public ResponseEntity<Void> deleteTable(@PathVariable Long tableId) {
        tableService.deleteTable(tableId);
        return ResponseEntity.noContent().build();
    }

    // DTO
    public static class CreateTableRequest {
        private String tableName;
        public String getTableName() { return tableName; }
        public void setTableName(String tableName) { this.tableName = tableName; }
    }
}