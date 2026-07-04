package com.studysheetai.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.studysheetai.chunking.TextChunk;
import com.studysheetai.chunking.TextChunkerService;
import com.studysheetai.dto.ChunkPreview;
import com.studysheetai.dto.DocumentProcessingResult;
import com.studysheetai.entity.Document;
import com.studysheetai.entity.DocumentChunk;
import com.studysheetai.exception.DocumentProcessingException;
import com.studysheetai.exception.InvalidDocumentException;
import com.studysheetai.parser.ParsedPdfPage;
import com.studysheetai.parser.PdfParserService;
import com.studysheetai.repository.DocumentRepository;

@Service
public class DocumentProcessingService {

    private static final int PREVIEW_LIMIT = 5;
    private static final int PREVIEW_CHARACTERS = 160;

    private final PdfParserService pdfParserService;
    private final TextChunkerService textChunkerService;
    private final DocumentRepository documentRepository;

    public DocumentProcessingService(
            PdfParserService pdfParserService,
            TextChunkerService textChunkerService,
            DocumentRepository documentRepository
    ) {
        this.pdfParserService = pdfParserService;
        this.textChunkerService = textChunkerService;
        this.documentRepository = documentRepository;
    }

    @Transactional
    public DocumentProcessingResult process(DocumentProcessingRequest request) {
        validate(request);

        try (InputStream inputStream = request.inputStream()) {
            List<ParsedPdfPage> pages = pdfParserService.parse(inputStream);
            List<TextChunk> chunks = textChunkerService.chunk(pages);

            if (pages.isEmpty()) {
                throw new InvalidDocumentException("PDF does not contain any pages");
            }
            if (chunks.isEmpty()) {
                throw new InvalidDocumentException("PDF does not contain extractable text");
            }

            Document document = new Document(
                    request.originalFilename().strip(),
                    normalizedContentType(request.contentType()),
                    request.fileSize(),
                    pages.size()
            );

            for (TextChunk chunk : chunks) {
                document.addChunk(new DocumentChunk(chunk.chunkIndex(), chunk.pageNumber(), chunk.content()));
            }

            Document savedDocument = documentRepository.save(document);
            return toResult(savedDocument);
        } catch (IOException exception) {
            throw new DocumentProcessingException("Failed to parse PDF document", exception);
        }
    }

    private void validate(DocumentProcessingRequest request) {
        if (request == null) {
            throw new InvalidDocumentException("Document request is required");
        }
        if (request.inputStream() == null) {
            throw new InvalidDocumentException("Document content is required");
        }
        if (request.fileSize() <= 0) {
            throw new InvalidDocumentException("Document must not be empty");
        }
        if (request.originalFilename() == null || request.originalFilename().isBlank()) {
            throw new InvalidDocumentException("Original filename is required");
        }
        if (!looksLikePdf(request.originalFilename(), request.contentType())) {
            throw new InvalidDocumentException("Only PDF documents are supported");
        }
    }

    private boolean looksLikePdf(String originalFilename, String contentType) {
        String filename = originalFilename.toLowerCase(Locale.ROOT);
        return filename.endsWith(".pdf") || "application/pdf".equalsIgnoreCase(normalizedContentType(contentType));
    }

    private String normalizedContentType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return "application/pdf";
        }
        return contentType.strip();
    }

    private DocumentProcessingResult toResult(Document document) {
        List<ChunkPreview> previews = document.getChunks().stream()
                .limit(PREVIEW_LIMIT)
                .map(this::toPreview)
                .toList();

        return new DocumentProcessingResult(
                document.getId(),
                document.getOriginalFilename(),
                document.getPageCount(),
                document.getChunks().size(),
                document.getUploadedAt(),
                previews
        );
    }

    private ChunkPreview toPreview(DocumentChunk chunk) {
        String content = chunk.getContent();
        String preview = content.length() <= PREVIEW_CHARACTERS
                ? content
                : content.substring(0, PREVIEW_CHARACTERS).strip() + "...";

        return new ChunkPreview(
                chunk.getChunkIndex(),
                chunk.getPageNumber(),
                chunk.getCharacterCount(),
                preview
        );
    }
}
