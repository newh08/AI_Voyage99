package com.example.Voyage.controller;

import com.example.Voyage.domain.ColumnMetadata;
import com.example.Voyage.service.ColumnService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ColumnController {
    private final ColumnService columnService;

    public ColumnController(ColumnService columnService) {
        this.columnService = columnService;
    }

    // 📌 1️⃣ 특정 테이블에 컬럼 추가
    @PostMapping("/tables/{tableId}/columns")
    public ResponseEntity<ColumnMetadata> createColumn(
            @PathVariable Long tableId,
            @RequestBody CreateColumnRequest request) {
        ColumnMetadata column = columnService.createColumn(tableId, request.getColumnName());
        return ResponseEntity.ok(column);
    }

    // 📌 2️⃣ 특정 테이블의 컬럼 목록 조회
    @GetMapping("/tables/{tableId}/columns")
    public ResponseEntity<List<ColumnMetadata>> getColumnsByTable(@PathVariable Long tableId) {
        return ResponseEntity.ok(columnService.getColumnsByTable(tableId));
    }

    // 📌 3️⃣ 특정 컬럼에 FK 설정
    @PostMapping("/columns/{columnId}/foreign-key")
    public ResponseEntity<ColumnMetadata> setForeignKey(
            @PathVariable Long columnId,
            @RequestBody ForeignKeyRequest request) {
        ColumnMetadata updatedColumn = columnService.setForeignKey(columnId, request.getForeignTableId(), request.getForeignColumnId());
        return ResponseEntity.ok(updatedColumn);
    }

    @GetMapping("/columns/{columnId}")
    public ResponseEntity<?> getColumnById(@PathVariable Long columnId) {
        ColumnMetadata column = columnService.getColumnById(columnId);
        if (column == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Column not found with ID: " + columnId);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("id", column.getId());
        response.put("columnName", column.getColumnName());
        response.put("databaseId", column.getTableMetadata().getDatabase().getId());

        return ResponseEntity.ok(response);
    }

    // ✅ 컬럼 삭제
    @DeleteMapping("/columns/{columnId}")
    public ResponseEntity<?> deleteColumn(@PathVariable Long columnId) {
        try {
            columnService.deleteColumn(columnId);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



    // 📌 DTO (Request Body 데이터 받기)
    public static class CreateColumnRequest {
        private String columnName;

        public String getColumnName() { return columnName; }
        public void setColumnName(String columnName) { this.columnName = columnName; }

    }

    public static class ForeignKeyRequest {
        private Long foreignTableId;
        private Long foreignColumnId;

        public Long getForeignTableId() { return foreignTableId; }
        public void setForeignTableId(Long foreignTableId) { this.foreignTableId = foreignTableId; }

        public Long getForeignColumnId() { return foreignColumnId; }
        public void setForeignColumnId(Long foreignColumnId) { this.foreignColumnId = foreignColumnId; }
    }
}