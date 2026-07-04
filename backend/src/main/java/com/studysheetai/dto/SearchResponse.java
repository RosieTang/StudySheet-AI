package com.studysheetai.dto;

import java.util.List;

import com.studysheetai.retrieval.RetrievalMode;

public record SearchResponse(
        List<Long> documentIds,
        String task,
        String scope,
        RetrievalMode mode,
        int resultCount,
        List<SearchResult> results
) {
}
