package com.example.Voyage.service;

import com.example.Voyage.domain.ColumnMetadata;
import com.example.Voyage.domain.ForeignKeyMetadata;
import com.example.Voyage.domain.TableMetadata;
import com.example.Voyage.repository.ColumnMetadataRepository;
import com.example.Voyage.repository.ForeignKeyMetadataRepository;
import com.example.Voyage.repository.TableMetadataRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ForeignKeyService {

    private final ForeignKeyMetadataRepository fkRepo;
    private final TableMetadataRepository tableRepo;
    private final ColumnMetadataRepository columnRepo;

    public ForeignKeyService(
            ForeignKeyMetadataRepository fkRepo,
            TableMetadataRepository tableRepo,
            ColumnMetadataRepository columnRepo
    ) {
        this.fkRepo = fkRepo;
        this.tableRepo = tableRepo;
        this.columnRepo = columnRepo;
    }

    // 1) FK 생성
    public ForeignKeyMetadata createForeignKey(Long fromTableId, Long fromColumnId,
                                               Long toTableId, Long toColumnId) {
        TableMetadata fromTable = tableRepo.findById(fromTableId)
                .orElseThrow(() -> new IllegalArgumentException("From table not found. ID: " + fromTableId));
        TableMetadata toTable = tableRepo.findById(toTableId)
                .orElseThrow(() -> new IllegalArgumentException("To table not found. ID: " + toTableId));

        ColumnMetadata fromColumn = columnRepo.findById(fromColumnId)
                .orElseThrow(() -> new IllegalArgumentException("From column not found. ID: " + fromColumnId));
        ColumnMetadata toColumn = columnRepo.findById(toColumnId)
                .orElseThrow(() -> new IllegalArgumentException("To column not found. ID: " + toColumnId));

        ForeignKeyMetadata fk = new ForeignKeyMetadata();
        fk.setFromTable(fromTable);
        fk.setFromColumn(fromColumn);
        fk.setToTable(toTable);
        fk.setToColumn(toColumn);

        return fkRepo.save(fk);
    }

    // 2) FK 조회
    public ForeignKeyMetadata getForeignKey(Long fkId) {
        return fkRepo.findById(fkId)
                .orElseThrow(() -> new IllegalArgumentException("ForeignKey not found. ID: " + fkId));
    }

    // 3) FK 삭제
    public void deleteForeignKey(Long fkId) {
        fkRepo.deleteById(fkId);
    }
}