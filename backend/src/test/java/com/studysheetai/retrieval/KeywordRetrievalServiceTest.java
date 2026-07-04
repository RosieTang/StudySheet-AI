package com.studysheetai.retrieval;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.studysheetai.dto.SearchRequest;
import com.studysheetai.dto.SearchResponse;
import com.studysheetai.entity.Document;
import com.studysheetai.entity.DocumentChunk;
import com.studysheetai.exception.InvalidDocumentException;
import com.studysheetai.exception.ResourceNotFoundException;
import com.studysheetai.repository.DocumentChunkRepository;
import com.studysheetai.repository.DocumentRepository;

@DataJpaTest
@ActiveProfiles("test")
class KeywordRetrievalServiceTest {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentChunkRepository documentChunkRepository;

    private KeywordRetrievalService keywordRetrievalService;

    @BeforeEach
    void setUp() {
        keywordRetrievalService = new KeywordRetrievalService(documentRepository, documentChunkRepository);
    }

    @Test
    void searchesAcrossMultipleDocumentsAndReturnsSelectedChunksInSourceOrder() {
        Document knn = saveDocument(
                "knn.pdf",
                "KNN uses distance functions to measure similarity.",
                "Euclidean distance and Minkowski distance are common.",
                "Unrelated slide about course logistics."
        );
        Document bayes = saveDocument(
                "bayes.pdf",
                "Naive Bayes uses Bayes rule and posterior probability.",
                "Confusion matrix precision and recall evaluate classifiers."
        );

        SearchResponse response = keywordRetrievalService.search(new SearchRequest(
                List.of(knn.getId(), bayes.getId()),
                "Generate a compact exam cheatsheet",
                "KNN Naive Bayes distance functions Bayes rule posterior probability precision recall",
                RetrievalMode.CHEATSHEET,
                1,
                10
        ));

        assertThat(response.resultCount()).isEqualTo(4);
        assertThat(response.results()).extracting(result -> result.documentId())
                .containsExactly(knn.getId(), knn.getId(), bayes.getId(), bayes.getId());
        assertThat(response.results()).extracting(result -> result.pageNumber())
                .containsExactly(1, 2, 1, 2);
        assertThat(response.results().get(0).matchedTerms()).contains("knn", "distance");
        assertThat(response.results().get(2).documentName()).isEqualTo("bayes.pdf");
    }

    @Test
    void appliesMaxChunksAsSafetyLimitBeforeSourceOrderOutput() {
        Document document = saveDocument(
                "evaluation.pdf",
                "precision recall confusion matrix classifier classifier classifier",
                "precision only",
                "recall only"
        );

        SearchResponse response = keywordRetrievalService.search(new SearchRequest(
                List.of(document.getId()),
                "Find strongest chunks",
                "precision recall confusion matrix classifier",
                RetrievalMode.CHEATSHEET,
                1,
                2
        ));

        assertThat(response.resultCount()).isEqualTo(2);
        assertThat(response.results()).extracting(result -> result.pageNumber())
                .containsExactly(1, 2);
    }

    @Test
    void extractsSearchTermsWithStopWordsRemoved() {
        List<String> terms = keywordRetrievalService.extractSearchTerms(
                "What are KNN and Naive Bayes classifiers?"
        );

        assertThat(terms).containsExactly("knn", "naive", "bayes", "classifiers");
    }

    @Test
    void rejectsEmptyScope() {
        assertThatThrownBy(() -> keywordRetrievalService.search(new SearchRequest(
                List.of(1L),
                "Task",
                " ",
                RetrievalMode.CHEATSHEET,
                1,
                10
        ))).isInstanceOf(InvalidDocumentException.class)
                .hasMessage("Scope is required");
    }

    @Test
    void rejectsMissingDocument() {
        assertThatThrownBy(() -> keywordRetrievalService.search(new SearchRequest(
                List.of(999L),
                "Task",
                "knn",
                RetrievalMode.CHEATSHEET,
                1,
                10
        ))).isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Document not found: 999");
    }

    private Document saveDocument(String filename, String... pageContents) {
        Document document = new Document(filename, "application/pdf", 1024L, pageContents.length);

        for (int index = 0; index < pageContents.length; index++) {
            document.addChunk(new DocumentChunk(index, index + 1, pageContents[index]));
        }

        return documentRepository.saveAndFlush(document);
    }
}
