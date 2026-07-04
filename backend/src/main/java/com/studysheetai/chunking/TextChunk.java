package com.studysheetai.chunking;

public record TextChunk(
        int chunkIndex,
        int pageNumber,
        String content
) {
    public TextChunk {
        if (chunkIndex < 0) {
            throw new IllegalArgumentException("chunkIndex must be 0 or greater");
        }
        if (pageNumber < 1) {
            throw new IllegalArgumentException("pageNumber must be 1 or greater");
        }
        content = content == null ? "" : content.strip();
        if (content.isBlank()) {
            throw new IllegalArgumentException("content must not be blank");
        }
    }

    public int characterCount() {
        return content.length();
    }
}
