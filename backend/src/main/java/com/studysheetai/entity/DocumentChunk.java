package com.studysheetai.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "document_chunks",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_document_chunks_document_chunk_index",
                columnNames = {"document_id", "chunk_index"}
        ),
        indexes = {
                @Index(name = "idx_document_chunks_document_id", columnList = "document_id"),
                @Index(name = "idx_document_chunks_page_number", columnList = "page_number")
        }
)
public class DocumentChunk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @Column(name = "chunk_index", nullable = false)
    private int chunkIndex;

    @Column(name = "page_number", nullable = false)
    private int pageNumber;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int characterCount;

    protected DocumentChunk() {
    }

    public DocumentChunk(int chunkIndex, int pageNumber, String content) {
        this.chunkIndex = chunkIndex;
        this.pageNumber = pageNumber;
        this.content = content == null ? "" : content.strip();
        this.characterCount = this.content.length();
    }

    void setDocument(Document document) {
        this.document = document;
    }

    public Long getId() {
        return id;
    }

    public Document getDocument() {
        return document;
    }

    public int getChunkIndex() {
        return chunkIndex;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public String getContent() {
        return content;
    }

    public int getCharacterCount() {
        return characterCount;
    }
}
