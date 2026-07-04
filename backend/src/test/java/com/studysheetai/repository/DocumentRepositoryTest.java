package com.studysheetai.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.studysheetai.entity.Document;
import com.studysheetai.entity.DocumentChunk;

@DataJpaTest
@ActiveProfiles("test")
class DocumentRepositoryTest {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentChunkRepository documentChunkRepository;

    @Test
    void savesDocumentWithChunksAndFindsChunksInOrder() {
        Document document = new Document("lecture-01.pdf", "application/pdf", 2048L, 2);
        document.addChunk(new DocumentChunk(0, 1, "Introduction to dynamic programming"));
        document.addChunk(new DocumentChunk(1, 2, "State transition and recurrence"));

        Document savedDocument = documentRepository.saveAndFlush(document);

        assertThat(savedDocument.getId()).isNotNull();
        assertThat(savedDocument.getUploadedAt()).isNotNull();

        List<DocumentChunk> chunks = documentChunkRepository.findByDocumentIdOrderByChunkIndexAsc(savedDocument.getId());

        assertThat(chunks).hasSize(2);
        assertThat(chunks).extracting(DocumentChunk::getChunkIndex).containsExactly(0, 1);
        assertThat(chunks).extracting(DocumentChunk::getPageNumber).containsExactly(1, 2);
        assertThat(chunks.get(0).getCharacterCount()).isEqualTo("Introduction to dynamic programming".length());
    }
}
