package com.studysheetai.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;

import com.studysheetai.dto.DocumentProcessingResult;
import com.studysheetai.exception.DocumentProcessingException;
import com.studysheetai.exception.InvalidDocumentException;
import com.studysheetai.parser.ParsedPdfPage;
import com.studysheetai.parser.PdfParserService;
import com.studysheetai.repository.DocumentChunkRepository;
import com.studysheetai.repository.DocumentRepository;

@SpringBootTest
@ActiveProfiles("test")
class DocumentProcessingServiceTest {

    @Autowired
    private DocumentProcessingService documentProcessingService;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentChunkRepository documentChunkRepository;

    @Autowired
    private FakePdfParserService pdfParserService;

    @BeforeEach
    void cleanDatabase() {
        documentChunkRepository.deleteAll();
        documentRepository.deleteAll();
        pdfParserService.reset();
    }

    @Test
    void parsesChunksAndPersistsPdfDocument() throws IOException {
        pdfParserService.returnPages(List.of(
                new ParsedPdfPage(1, """
                        Dynamic Programming
                        - Define state
                        - Write recurrence
                        """),
                new ParsedPdfPage(2, """
                        Greedy Algorithms
                        - Exchange argument
                        - Local optimality
                        """)));

        DocumentProcessingResult result = documentProcessingService.process(request(
                "lecture-01.pdf",
                "application/pdf",
                "fake pdf bytes"
        ));

        assertThat(result.documentId()).isNotNull();
        assertThat(result.originalFilename()).isEqualTo("lecture-01.pdf");
        assertThat(result.pageCount()).isEqualTo(2);
        assertThat(result.chunkCount()).isPositive();
        assertThat(result.uploadedAt()).isNotNull();
        assertThat(result.chunkPreviews()).isNotEmpty();

        assertThat(documentRepository.count()).isEqualTo(1);
        assertThat(documentChunkRepository.findByDocumentIdOrderByChunkIndexAsc(result.documentId()))
                .hasSize(result.chunkCount());
    }

    @Test
    void rejectsNonPdfDocumentsBeforeParsing() {
        assertThatThrownBy(() -> documentProcessingService.process(request(
                "notes.txt",
                "text/plain",
                "not a pdf"
        ))).isInstanceOf(InvalidDocumentException.class)
                .hasMessage("Only PDF documents are supported");

        assertThat(documentRepository.count()).isZero();
    }

    @Test
    void rejectsPdfWithoutExtractableText() throws IOException {
        pdfParserService.returnPages(List.of(new ParsedPdfPage(1, "   ")));

        assertThatThrownBy(() -> documentProcessingService.process(request(
                "scanned.pdf",
                "application/pdf",
                "fake pdf bytes"
        ))).isInstanceOf(InvalidDocumentException.class)
                .hasMessage("PDF does not contain extractable text");

        assertThat(documentRepository.count()).isZero();
    }

    @Test
    void wrapsParserIoFailure() throws IOException {
        pdfParserService.throwException(new IOException("broken pdf"));

        assertThatThrownBy(() -> documentProcessingService.process(request(
                "broken.pdf",
                "application/pdf",
                "fake pdf bytes"
        ))).isInstanceOf(DocumentProcessingException.class)
                .hasMessage("Failed to parse PDF document")
                .hasCauseInstanceOf(IOException.class);
    }

    private DocumentProcessingRequest request(String filename, String contentType, String content) {
        byte[] bytes = content.getBytes();
        return new DocumentProcessingRequest(
                filename,
                contentType,
                bytes.length,
                new ByteArrayInputStream(bytes)
        );
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        @Primary
        FakePdfParserService fakePdfParserService() {
            return new FakePdfParserService();
        }
    }

    static class FakePdfParserService extends PdfParserService {

        private List<ParsedPdfPage> pages = List.of();
        private IOException exception;

        @Override
        public List<ParsedPdfPage> parse(InputStream inputStream) throws IOException {
            if (exception != null) {
                throw exception;
            }
            return pages;
        }

        void returnPages(List<ParsedPdfPage> pages) {
            this.pages = pages;
        }

        void throwException(IOException exception) {
            this.exception = exception;
        }

        void reset() {
            pages = List.of();
            exception = null;
        }
    }
}
