package com.example.Voyage.controller.dto;

import com.example.Voyage.domain.TableMetadata;
import java.util.List;
import java.util.stream.Collectors;

public class TableSchemaResponse {
    private Long id;
    private String tableName;
    private List<ColumnSchemaResponse> columns;

    public TableSchemaResponse(TableMetadata table) {
        this.id = table.getId();
        this.tableName = table.getTableName();
        this.columns = table.getColumns().stream()
                .map(ColumnSchemaResponse::new)
                .collect(Collectors.toList());
    }

    public Long getId() { return id; }
    public String getTableName() { return tableName; }
    public List<ColumnSchemaResponse> getColumns() { return columns; }
}