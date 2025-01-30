package com.example.Voyage.controller.dto;

import com.example.Voyage.domain.ColumnMetadata;

public class ColumnSchemaResponse {
    private Long id;
    private String columnName;
    // FK 정보 (foreignTableId, foreignColumnId)
    private Long foreignTableId;
    private Long foreignColumnId;

    public ColumnSchemaResponse(ColumnMetadata column) {
        this.id = column.getId();
        this.columnName = column.getColumnName();

        // FK가 있으면 ID 세팅, 없으면 null
        if (column.getForeignTable() != null) {
            this.foreignTableId = column.getForeignTable().getId();
        }
        if (column.getForeignColumn() != null) {
            this.foreignColumnId = column.getForeignColumn().getId();
        }
    }

    public Long getId() { return id; }
    public String getColumnName() { return columnName; }
    public Long getForeignTableId() { return foreignTableId; }
    public Long getForeignColumnId() { return foreignColumnId; }
}