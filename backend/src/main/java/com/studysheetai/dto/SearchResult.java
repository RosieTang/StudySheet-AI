package com.studysheetai.dto;

import java.util.List;

public record SearchResult(
        Long chunkId,
        Long documentId,
        String documentName,
        int chunkIndex,
        int pageNumber,
        int score,
        List<String> matchedTerms,
        String content
) {
}
