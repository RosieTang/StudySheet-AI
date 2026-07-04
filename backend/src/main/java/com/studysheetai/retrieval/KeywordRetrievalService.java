package com.studysheetai.retrieval;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.studysheetai.dto.SearchRequest;
import com.studysheetai.dto.SearchResponse;
import com.studysheetai.dto.SearchResult;
import com.studysheetai.entity.Document;
import com.studysheetai.entity.DocumentChunk;
import com.studysheetai.exception.InvalidDocumentException;
import com.studysheetai.exception.ResourceNotFoundException;
import com.studysheetai.repository.DocumentChunkRepository;
import com.studysheetai.repository.DocumentRepository;

@Service
public class KeywordRetrievalService {

    private static final int DEFAULT_MIN_SCORE = 1;
    private static final int DEFAULT_MAX_CHUNKS = 40;
    private static final int HARD_MAX_CHUNKS = 100;

    private static final Set<String> STOP_WORDS = Set.of(
            "a", "an", "and", "are", "as", "at", "be", "by", "for", "from",
            "how", "in", "into", "is", "it", "of", "on", "or", "that", "the",
            "this", "to", "what", "when", "where", "which", "with"
    );

    private final DocumentRepository documentRepository;
    private final DocumentChunkRepository documentChunkRepository;

    public KeywordRetrievalService(
            DocumentRepository documentRepository,
            DocumentChunkRepository documentChunkRepository
    ) {
        this.documentRepository = documentRepository;
        this.documentChunkRepository = documentChunkRepository;
    }

    @Transactional(readOnly = true)
    public SearchResponse search(SearchRequest request) {
        ValidatedSearch validated = validate(request);
        List<String> terms = extractSearchTerms(validated.scope());

        if (terms.isEmpty()) {
            throw new InvalidDocumentException("Scope must contain searchable terms");
        }

        Map<Long, Integer> documentOrder = documentOrder(validated.documentIds());
        List<DocumentChunk> chunks = documentChunkRepository
                .findByDocumentIdsWithDocument(validated.documentIds());

        List<RetrievedChunk> ranked = chunks.stream()
                .map(chunk -> score(chunk, terms))
                .filter(chunk -> chunk.score() >= validated.minScore())
                .sorted(Comparator
                        .comparingInt(RetrievedChunk::score).reversed()
                        .thenComparingInt(chunk -> documentOrder.get(chunk.documentId()))
                        .thenComparingInt(RetrievedChunk::pageNumber)
                        .thenComparingInt(RetrievedChunk::chunkIndex))
                .limit(validated.maxChunks())
                .sorted(Comparator
                        .comparingInt((RetrievedChunk chunk) -> documentOrder.get(chunk.documentId()))
                        .thenComparingInt(RetrievedChunk::pageNumber)
                        .thenComparingInt(RetrievedChunk::chunkIndex))
                .toList();

        List<SearchResult> results = ranked.stream()
                .map(this::toResult)
                .toList();

        return new SearchResponse(
                validated.documentIds(),
                validated.task(),
                validated.scope(),
                validated.mode(),
                results.size(),
                results
        );
    }

    List<String> extractSearchTerms(String text) {
        LinkedHashSet<String> terms = new LinkedHashSet<>();

        for (String token : text.toLowerCase(Locale.ROOT).split("[^\\p{L}\\p{N}]+")) {
            if (token.length() < 2 || STOP_WORDS.contains(token)) {
                continue;
            }
            terms.add(token);
        }

        return new ArrayList<>(terms);
    }

    private RetrievedChunk score(DocumentChunk chunk, List<String> terms) {
        String content = chunk.getContent().toLowerCase(Locale.ROOT);
        List<String> matchedTerms = terms.stream()
                .filter(content::contains)
                .toList();
        Document document = chunk.getDocument();

        return new RetrievedChunk(
                chunk.getId(),
                document.getId(),
                document.getOriginalFilename(),
                chunk.getChunkIndex(),
                chunk.getPageNumber(),
                matchedTerms.size(),
                matchedTerms,
                chunk.getContent()
        );
    }

    private SearchResult toResult(RetrievedChunk chunk) {
        return new SearchResult(
                chunk.chunkId(),
                chunk.documentId(),
                chunk.documentName(),
                chunk.chunkIndex(),
                chunk.pageNumber(),
                chunk.score(),
                chunk.matchedTerms(),
                chunk.content()
        );
    }

    private ValidatedSearch validate(SearchRequest request) {
        if (request == null) {
            throw new InvalidDocumentException("Search request is required");
        }
        if (request.documentIds() == null || request.documentIds().isEmpty()) {
            throw new InvalidDocumentException("At least one documentId is required");
        }
        if (request.scope() == null || request.scope().isBlank()) {
            throw new InvalidDocumentException("Scope is required");
        }

        List<Long> documentIds = request.documentIds().stream()
                .filter(id -> id != null && id > 0)
                .distinct()
                .toList();

        if (documentIds.isEmpty()) {
            throw new InvalidDocumentException("At least one valid documentId is required");
        }

        verifyDocumentsExist(documentIds);

        int minScore = request.minScore() == null ? DEFAULT_MIN_SCORE : request.minScore();
        if (minScore < 1) {
            throw new InvalidDocumentException("minScore must be at least 1");
        }

        int maxChunks = request.maxChunks() == null ? DEFAULT_MAX_CHUNKS : request.maxChunks();
        if (maxChunks < 1) {
            throw new InvalidDocumentException("maxChunks must be at least 1");
        }
        if (maxChunks > HARD_MAX_CHUNKS) {
            throw new InvalidDocumentException("maxChunks must be less than or equal to " + HARD_MAX_CHUNKS);
        }

        RetrievalMode mode = request.mode() == null ? RetrievalMode.CHEATSHEET : request.mode();
        String task = request.task() == null || request.task().isBlank()
                ? "Find relevant study material"
                : request.task().strip();

        return new ValidatedSearch(
                documentIds,
                task,
                request.scope().strip(),
                mode,
                minScore,
                maxChunks
        );
    }

    private void verifyDocumentsExist(List<Long> documentIds) {
        Set<Long> foundIds = new HashSet<>();
        for (Document document : documentRepository.findAllById(documentIds)) {
            foundIds.add(document.getId());
        }

        List<Long> missingIds = documentIds.stream()
                .filter(id -> !foundIds.contains(id))
                .toList();

        if (!missingIds.isEmpty()) {
            throw new ResourceNotFoundException("Document not found: " + missingIds.get(0));
        }
    }

    private Map<Long, Integer> documentOrder(List<Long> documentIds) {
        Map<Long, Integer> order = new HashMap<>();
        for (int index = 0; index < documentIds.size(); index++) {
            order.put(documentIds.get(index), index);
        }
        return order;
    }

    private record ValidatedSearch(
            List<Long> documentIds,
            String task,
            String scope,
            RetrievalMode mode,
            int minScore,
            int maxChunks
    ) {
    }
}
