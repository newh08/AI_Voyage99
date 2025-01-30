package com.example.Voyage.service;

import com.example.Voyage.domain.ColumnMetadata;
import com.example.Voyage.domain.TableMetadata;
import com.example.Voyage.repository.ColumnMetadataRepository;
import com.example.Voyage.repository.TableMetadataRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ColumnService {
    private final ColumnMetadataRepository columnRepo;
    private final TableMetadataRepository tableRepo;

    public ColumnService(ColumnMetadataRepository columnRepo, TableMetadataRepository tableRepo) {
        this.columnRepo = columnRepo;
        this.tableRepo = tableRepo;
    }

    // 📌 1️⃣ 컬럼 생성
    public ColumnMetadata createColumn(Long tableId, String columnName) {
        TableMetadata table = tableRepo.findById(tableId)
                .orElseThrow(() -> new IllegalArgumentException("Table not found: " + tableId));

        ColumnMetadata column = new ColumnMetadata();
        column.setTableMetadata(table);
        column.setColumnName(columnName);

        return columnRepo.save(column);
    }

    // 📌 2️⃣ 특정 테이블의 컬럼 목록 조회
    public List<ColumnMetadata> getColumnsByTable(Long tableId) {
        return columnRepo.findByTableMetadataId(tableId);
    }

    // 📌 3️⃣ 특정 컬럼에 FK 설정
    public ColumnMetadata setForeignKey(Long columnId, Long foreignTableId, Long foreignColumnId) {
        ColumnMetadata column = columnRepo.findById(columnId)
                .orElseThrow(() -> new IllegalArgumentException("Column not found: " + columnId));

        // ✅ 1) FK 해제 (foreignTableId, foreignColumnId가 null이면)
        if (foreignTableId == null && foreignColumnId == null) {
            column.setForeignTable(null);
            column.setForeignColumn(null);
            return columnRepo.save(column);
        }

        // ✅ 2) FK 설정 (foreignTableId, foreignColumnId가 유효)
        TableMetadata foreignTable = tableRepo.findById(foreignTableId)
                .orElseThrow(() -> new IllegalArgumentException("Foreign table not found: " + foreignTableId));

        // foreignColumnId가 null이면? (테이블만 FK?)
        // -> 일반적으로 FK는 컬럼까지 있어야 하므로 null이면 예외처리하거나, 원하는 로직
        ColumnMetadata foreignColumn = columnRepo.findById(foreignColumnId)
                .orElseThrow(() -> new IllegalArgumentException("Foreign column not found: " + foreignColumnId));

        column.setForeignTable(foreignTable);
        column.setForeignColumn(foreignColumn);

        return columnRepo.save(column);
    }

    // ✅ 특정 컬럼 조회 메서드 추가
    public ColumnMetadata getColumnById(Long columnId) {
        return columnRepo.findById(columnId)
                .orElseThrow(() -> new IllegalArgumentException("Column not found: " + columnId));
    }

    // ✅ 컬럼 삭제 메서드
    public void deleteColumn(Long columnId) {
        ColumnMetadata column = columnRepo.findById(columnId)
                .orElseThrow(() -> new IllegalArgumentException("Column not found."));

        // ✅ 1) 참조하는 컬럼 목록 검색
        List<ColumnMetadata> referencingColumns = columnRepo.findByForeignColumnId(columnId);
        if (!referencingColumns.isEmpty()) {
            // 에러 or 409
            String message = referencingColumns.stream()
                    .map(c -> c.getColumnName() + " in table " + c.getTableMetadata().getTableName())
                    .collect(Collectors.joining(", "));
            throw new IllegalStateException("Cannot delete column because it is referenced by: " + message);
        }

        // ✅ 2) 참조 없으면 삭제
        columnRepo.delete(column);
    }
}