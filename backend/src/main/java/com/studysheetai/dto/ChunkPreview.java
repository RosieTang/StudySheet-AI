package com.studysheetai.dto;

public record ChunkPreview(
        int chunkIndex,
        int pageNumber,
        int characterCount,
        String contentPreview
) {
}
