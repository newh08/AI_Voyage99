package com.example.Voyage.controller;

import com.example.Voyage.controller.dto.DatabaseSchemaResponse;
import com.example.Voyage.domain.DatabaseMetadata;
import com.example.Voyage.service.DatabaseService;
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
@RequestMapping("/api/databases")
public class DatabaseController {

    private final DatabaseService databaseService;

    public DatabaseController(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    // 1) 데이터베이스 생성
    @PostMapping
    public ResponseEntity<DatabaseMetadata> createDatabase(@RequestBody CreateDatabaseRequest request) {
        DatabaseMetadata db = databaseService.createDatabase(request.getDbName());
        return ResponseEntity.ok(db);
    }

    // 2) 모든 데이터베이스 조회
    @GetMapping
    public ResponseEntity<List<DatabaseMetadata>> getAllDatabases() {
        return ResponseEntity.ok(databaseService.getAllDatabases());
    }

    // 3) 특정 데이터베이스 조회
    @GetMapping("/{dbId}")
    public ResponseEntity<DatabaseMetadata> getDatabaseById(@PathVariable Long dbId) {
        return ResponseEntity.ok(databaseService.getDatabaseById(dbId));
    }

    // 4) 특정 데이터베이스 삭제
    @DeleteMapping("/{dbId}")
    public ResponseEntity<Void> deleteDatabase(@PathVariable Long dbId) {
        databaseService.deleteDatabase(dbId);
        return ResponseEntity.noContent().build();
    }

    // DTO (Request Body 용)
    public static class CreateDatabaseRequest {
        private String dbName;
        public String getDbName() { return dbName; }
        public void setDbName(String dbName) { this.dbName = dbName; }
    }

    @GetMapping("/{dbId}/schema")
    public ResponseEntity<?> getDatabaseSchema(@PathVariable Long dbId) {
        DatabaseMetadata db = databaseService.getDatabaseById(dbId);
        if (db == null) {
            return ResponseEntity.notFound().build();
        }
        // DTO로 변환
        DatabaseSchemaResponse response = new DatabaseSchemaResponse(db);
        return ResponseEntity.ok(response);
    }

    // ✅ 스키마 정보를 텍스트로 반환하는 API
    @GetMapping("/{dbId}/schema-text")
    public ResponseEntity<String> getDatabaseSchemaText(@PathVariable Long dbId) {
        String schemaText = databaseService.generateSchemaText(dbId);
        return ResponseEntity.ok(schemaText);
    }
}
