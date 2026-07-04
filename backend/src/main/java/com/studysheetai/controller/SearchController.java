package com.studysheetai.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studysheetai.dto.SearchRequest;
import com.studysheetai.dto.SearchResponse;
import com.studysheetai.retrieval.KeywordRetrievalService;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final KeywordRetrievalService keywordRetrievalService;

    public SearchController(KeywordRetrievalService keywordRetrievalService) {
        this.keywordRetrievalService = keywordRetrievalService;
    }

    @PostMapping
    public SearchResponse search(@RequestBody SearchRequest request) {
        return keywordRetrievalService.search(request);
    }
}
