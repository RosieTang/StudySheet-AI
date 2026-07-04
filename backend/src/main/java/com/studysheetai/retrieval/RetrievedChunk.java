package com.studysheetai.retrieval;

import java.util.List;

public record RetrievedChunk(
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
