package com.studysheetai.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.studysheetai.entity.DocumentChunk;

public interface DocumentChunkRepository extends JpaRepository<DocumentChunk, Long> {

    List<DocumentChunk> findByDocumentIdOrderByChunkIndexAsc(Long documentId);

    @Query("""
            select chunk
            from DocumentChunk chunk
            join fetch chunk.document document
            where document.id in :documentIds
            order by document.id asc, chunk.pageNumber asc, chunk.chunkIndex asc
            """)
    List<DocumentChunk> findByDocumentIdsWithDocument(@Param("documentIds") List<Long> documentIds);
}
