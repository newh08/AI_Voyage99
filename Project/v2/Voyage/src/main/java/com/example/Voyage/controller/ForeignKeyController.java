package com.example.Voyage.controller;

import com.example.Voyage.domain.ForeignKeyMetadata;
import com.example.Voyage.service.ForeignKeyService;
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
public class ForeignKeyController {

    private final ForeignKeyService fkService;

    public ForeignKeyController(ForeignKeyService fkService) {
        this.fkService = fkService;
    }

    // 1) FK 생성
    @PostMapping("/relationships")
    public ResponseEntity<ForeignKeyMetadata> createForeignKey(@RequestBody CreateFKRequest request) {
        ForeignKeyMetadata fk = fkService.createForeignKey(
                request.getFromTableId(),
                request.getFromColumnId(),
                request.getToTableId(),
                request.getToColumnId()
        );
        return ResponseEntity.ok(fk);
    }

    // 2) FK 조회
    @GetMapping("/relationships/{fkId}")
    public ResponseEntity<ForeignKeyMetadata> getForeignKey(@PathVariable Long fkId) {
        return ResponseEntity.ok(fkService.getForeignKey(fkId));
    }

    // 3) FK 삭제
    @DeleteMapping("/relationships/{fkId}")
    public ResponseEntity<Void> deleteForeignKey(@PathVariable Long fkId) {
        fkService.deleteForeignKey(fkId);
        return ResponseEntity.noContent().build();
    }

    // DTO
    public static class CreateFKRequest {
        private Long fromTableId;
        private Long fromColumnId;
        private Long toTableId;
        private Long toColumnId;

        // getter, setter
        public Long getFromTableId() { return fromTableId; }
        public void setFromTableId(Long fromTableId) { this.fromTableId = fromTableId; }
        public Long getFromColumnId() { return fromColumnId; }
        public void setFromColumnId(Long fromColumnId) { this.fromColumnId = fromColumnId; }
        public Long getToTableId() { return toTableId; }
        public void setToTableId(Long toTableId) { this.toTableId = toTableId; }
        public Long getToColumnId() { return toColumnId; }
        public void setToColumnId(Long toColumnId) { this.toColumnId = toColumnId; }
    }
}