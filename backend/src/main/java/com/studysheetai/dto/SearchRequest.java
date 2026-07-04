package com.studysheetai.dto;

import java.util.List;

import com.studysheetai.retrieval.RetrievalMode;

public record SearchRequest(
        List<Long> documentIds,
        String task,
        String scope,
        RetrievalMode mode,
        Integer minScore,
        Integer maxChunks
) {
}
