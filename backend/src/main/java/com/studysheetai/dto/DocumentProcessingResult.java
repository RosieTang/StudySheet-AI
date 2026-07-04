package com.studysheetai.dto;

import java.time.Instant;
import java.util.List;

public record DocumentProcessingResult(
        Long documentId,
        String originalFilename,
        int pageCount,
        int chunkCount,
        Instant uploadedAt,
        List<ChunkPreview> chunkPreviews
) {
}
