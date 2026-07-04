package com.studysheetai.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.studysheetai.entity.Document;
import com.studysheetai.entity.DocumentChunk;
import com.studysheetai.repository.DocumentChunkRepository;
import com.studysheetai.repository.DocumentRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentChunkRepository documentChunkRepository;

    @BeforeEach
    void cleanDatabase() {
        documentChunkRepository.deleteAll();
        documentRepository.deleteAll();
    }

    @Test
    void searchesChunksAcrossRequestedDocuments() throws Exception {
        Document knn = saveDocument("knn.pdf", "KNN distance functions", "Unrelated content");
        Document bayes = saveDocument("bayes.pdf", "Naive Bayes posterior probability");

        mockMvc.perform(post("/api/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "documentIds": [%d, %d],
                                  "task": "Generate a compact exam cheatsheet",
                                  "scope": "KNN Naive Bayes distance posterior probability",
                                  "mode": "CHEATSHEET",
                                  "minScore": 1,
                                  "maxChunks": 10
                                }
                                """.formatted(knn.getId(), bayes.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mode").value("CHEATSHEET"))
                .andExpect(jsonPath("$.resultCount").value(2))
                .andExpect(jsonPath("$.results[0].documentId").value(knn.getId()))
                .andExpect(jsonPath("$.results[0].matchedTerms[0]").value("knn"))
                .andExpect(jsonPath("$.results[1].documentId").value(bayes.getId()))
                .andExpect(jsonPath("$.results[1].documentName").value("bayes.pdf"));
    }

    @Test
    void returnsNotFoundForMissingDocument() throws Exception {
        mockMvc.perform(post("/api/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "documentIds": [999],
                                  "scope": "KNN"
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Document not found: 999"));
    }

    private Document saveDocument(String filename, String... pageContents) {
        Document document = new Document(filename, "application/pdf", 1024L, pageContents.length);

        for (int index = 0; index < pageContents.length; index++) {
            document.addChunk(new DocumentChunk(index, index + 1, pageContents[index]));
        }

        return documentRepository.saveAndFlush(document);
    }
}
