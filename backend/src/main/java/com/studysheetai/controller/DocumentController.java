package com.studysheetai.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.studysheetai.dto.DocumentChunkResponse;
import com.studysheetai.dto.DocumentProcessingResult;
import com.studysheetai.entity.DocumentChunk;
import com.studysheetai.exception.DocumentProcessingException;
import com.studysheetai.exception.ResourceNotFoundException;
import com.studysheetai.repository.DocumentChunkRepository;
import com.studysheetai.repository.DocumentRepository;
import com.studysheetai.service.DocumentProcessingRequest;
import com.studysheetai.service.DocumentProcessingService;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentProcessingService documentProcessingService;
    private final DocumentRepository documentRepository;
    private final DocumentChunkRepository documentChunkRepository;

    public DocumentController(
            DocumentProcessingService documentProcessingService,
            DocumentRepository documentRepository,
            DocumentChunkRepository documentChunkRepository
    ) {
        this.documentProcessingService = documentProcessingService;
        this.documentRepository = documentRepository;
        this.documentChunkRepository = documentChunkRepository;
    }

    @PostMapping(value = "/parse", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DocumentProcessingResult parse(@RequestPart("file") MultipartFile file) {
        try {
            return documentProcessingService.process(new DocumentProcessingRequest(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getSize(),
                    file.getInputStream()
            ));
        } catch (IOException exception) {
            throw new DocumentProcessingException("Failed to read uploaded PDF document", exception);
        }
    }

    @GetMapping("/{documentId}/chunks")
    public List<DocumentChunkResponse> chunks(@PathVariable Long documentId) {
        if (!documentRepository.existsById(documentId)) {
            throw new ResourceNotFoundException("Document not found: " + documentId);
        }

        return documentChunkRepository.findByDocumentIdOrderByChunkIndexAsc(documentId)
                .stream()
                .map(chunk -> toResponse(documentId, chunk))
                .toList();
    }

    private DocumentChunkResponse toResponse(Long documentId, DocumentChunk chunk) {
        return new DocumentChunkResponse(
                chunk.getId(),
                documentId,
                chunk.getChunkIndex(),
                chunk.getPageNumber(),
                chunk.getCharacterCount(),
                chunk.getContent()
        );
    }
}
