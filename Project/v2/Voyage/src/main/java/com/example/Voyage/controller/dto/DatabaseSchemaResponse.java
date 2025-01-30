package com.example.Voyage.controller.dto;

import com.example.Voyage.domain.DatabaseMetadata;
import java.util.List;
import java.util.stream.Collectors;

public class DatabaseSchemaResponse {
    private Long id;
    private String dbName;
    private List<TableSchemaResponse> tables;

    public DatabaseSchemaResponse(DatabaseMetadata db) {
        this.id = db.getId();
        this.dbName = db.getDbName();
        this.tables = db.getTables().stream()
                .map(TableSchemaResponse::new)
                .collect(Collectors.toList());
    }

    public Long getId() { return id; }
    public String getDbName() { return dbName; }
    public List<TableSchemaResponse> getTables() { return tables; }
}