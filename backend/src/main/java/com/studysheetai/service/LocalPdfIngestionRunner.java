package com.studysheetai.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.studysheetai.dto.ChunkPreview;
import com.studysheetai.dto.DocumentProcessingResult;

@Component
public class LocalPdfIngestionRunner implements ApplicationRunner {

    private final String ingestPath;
    private final DocumentProcessingService documentProcessingService;

    public LocalPdfIngestionRunner(
            @Value("${app.ingest:}") String ingestPath,
            DocumentProcessingService documentProcessingService
    ) {
        this.ingestPath = ingestPath;
        this.documentProcessingService = documentProcessingService;
    }

    @Override
    public void run(ApplicationArguments args) throws IOException {
        if (ingestPath == null || ingestPath.isBlank()) {
            return;
        }

        Path path = Path.of(ingestPath).toAbsolutePath().normalize();
        try (InputStream inputStream = Files.newInputStream(path)) {
            DocumentProcessingResult result = documentProcessingService.process(new DocumentProcessingRequest(
                    path.getFileName().toString(),
                    "application/pdf",
                    Files.size(path),
                    inputStream
            ));

            printResult(path, result);
        }
    }

    private void printResult(Path path, DocumentProcessingResult result) {
        System.out.printf("Ingested PDF: %s%n", path);
        System.out.printf("Document id: %d%n", result.documentId());
        System.out.printf("Pages: %d%n", result.pageCount());
        System.out.printf("Chunks: %d%n", result.chunkCount());

        for (ChunkPreview preview : result.chunkPreviews()) {
            System.out.printf(
                    "Chunk %d, page %d, %d chars: %s%n",
                    preview.chunkIndex(),
                    preview.pageNumber(),
                    preview.characterCount(),
                    preview.contentPreview().replace(System.lineSeparator(), " ")
            );
        }
    }
}
