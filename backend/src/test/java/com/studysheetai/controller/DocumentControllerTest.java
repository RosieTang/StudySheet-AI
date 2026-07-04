package com.studysheetai.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studysheetai.repository.DocumentChunkRepository;
import com.studysheetai.repository.DocumentRepository;
import com.studysheetai.testutil.PdfTestFixtures;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    void parsesUploadedPdfAndReturnsStoredChunks() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "lecture.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                PdfTestFixtures.lectureSlidesPdf()
        );

        MvcResult parseResult = mockMvc.perform(multipart("/api/documents/parse").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documentId").isNumber())
                .andExpect(jsonPath("$.originalFilename").value("lecture.pdf"))
                .andExpect(jsonPath("$.pageCount").value(2))
                .andExpect(jsonPath("$.chunkCount").isNumber())
                .andExpect(jsonPath("$.chunkPreviews[0].contentPreview").isNotEmpty())
                .andReturn();

        JsonNode json = objectMapper.readTree(parseResult.getResponse().getContentAsString());
        long documentId = json.get("documentId").asLong();
        int chunkCount = json.get("chunkCount").asInt();

        mockMvc.perform(get("/api/documents/{documentId}/chunks", documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(chunkCount))
                .andExpect(jsonPath("$[0].documentId").value(documentId))
                .andExpect(jsonPath("$[0].pageNumber").value(1))
                .andExpect(jsonPath("$[0].content").value(org.hamcrest.Matchers.containsString("Dynamic Programming")));

        assertThat(documentRepository.count()).isEqualTo(1);
    }

    @Test
    void rejectsNonPdfUpload() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "notes.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "not a pdf".getBytes()
        );

        mockMvc.perform(multipart("/api/documents/parse").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Only PDF documents are supported"));
    }

    @Test
    void returnsNotFoundForMissingDocumentChunks() throws Exception {
        mockMvc.perform(get("/api/documents/{documentId}/chunks", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Document not found: 999"));
    }
}
