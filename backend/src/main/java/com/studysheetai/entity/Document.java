package com.studysheetai.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originalFilename;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private long fileSize;

    @Column(nullable = false)
    private int pageCount;

    @Column(nullable = false, updatable = false)
    private Instant uploadedAt;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentChunk> chunks = new ArrayList<>();

    protected Document() {
    }

    public Document(String originalFilename, String contentType, long fileSize, int pageCount) {
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.pageCount = pageCount;
    }

    @PrePersist
    void setUploadedAtIfMissing() {
        if (uploadedAt == null) {
            uploadedAt = Instant.now();
        }
    }

    public void addChunk(DocumentChunk chunk) {
        chunks.add(chunk);
        chunk.setDocument(this);
    }

    public Long getId() {
        return id;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public String getContentType() {
        return contentType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public int getPageCount() {
        return pageCount;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }

    public List<DocumentChunk> getChunks() {
        return chunks;
    }
}
