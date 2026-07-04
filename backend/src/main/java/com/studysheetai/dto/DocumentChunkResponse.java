package com.studysheetai.dto;

public record DocumentChunkResponse(
        Long id,
        Long documentId,
        int chunkIndex,
        int pageNumber,
        int characterCount,
        String content
) {
}
