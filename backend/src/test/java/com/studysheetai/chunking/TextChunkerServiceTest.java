package com.studysheetai.chunking;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.studysheetai.parser.ParsedPdfPage;

class TextChunkerServiceTest {

    @Test
    void chunksShortSlideLinesWithoutCrossingPageBoundaries() {
        TextChunkerService chunker = new TextChunkerService(new ChunkingProperties(120, 30));
        List<ParsedPdfPage> pages = List.of(
                new ParsedPdfPage(1, """
                        Dynamic Programming
                        - Define subproblems
                        - Write recurrence
                        - Choose evaluation order
                        - Reconstruct answer
                        """),
                new ParsedPdfPage(2, """
                        Greedy Algorithms
                        - Make locally optimal choice
                        - Prove exchange argument
                        """));

        List<TextChunk> chunks = chunker.chunk(pages);

        assertThat(chunks).isNotEmpty();
        assertThat(chunks).extracting(TextChunk::pageNumber).contains(1, 2);
        assertThat(chunks).allSatisfy(chunk -> assertThat(chunk.characterCount()).isLessThanOrEqualTo(120));
        assertThat(chunks.get(0).content()).contains("Dynamic Programming");
        assertThat(chunks.get(chunks.size() - 1).content()).contains("Greedy Algorithms");
    }

    @Test
    void skipsBlankPages() {
        TextChunkerService chunker = new TextChunkerService(new ChunkingProperties(120, 30));

        List<TextChunk> chunks = chunker.chunk(List.of(
                new ParsedPdfPage(1, "   "),
                new ParsedPdfPage(2, "Only useful page")));

        assertThat(chunks).hasSize(1);
        assertThat(chunks.get(0).pageNumber()).isEqualTo(2);
    }

    @Test
    void splitsLongSingleLineWithOverlap() {
        TextChunkerService chunker = new TextChunkerService(new ChunkingProperties(100, 20));
        String longLine = "A".repeat(160);

        List<TextChunk> chunks = chunker.chunk(List.of(new ParsedPdfPage(1, longLine)));

        assertThat(chunks).hasSize(2);
        assertThat(chunks).allSatisfy(chunk -> assertThat(chunk.characterCount()).isLessThanOrEqualTo(100));
        assertThat(chunks.get(0).content()).hasSize(100);
        assertThat(chunks.get(1).content()).hasSize(80);
    }
}
