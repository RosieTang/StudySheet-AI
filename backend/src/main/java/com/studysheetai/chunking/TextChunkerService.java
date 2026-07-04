package com.studysheetai.chunking;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.studysheetai.parser.ParsedPdfPage;

@Service
public class TextChunkerService {

    private final ChunkingProperties properties;

    public TextChunkerService(ChunkingProperties properties) {
        this.properties = properties;
    }

    public List<TextChunk> chunk(List<ParsedPdfPage> pages) {
        List<TextChunk> chunks = new ArrayList<>();
        int nextChunkIndex = 0;

        for (ParsedPdfPage page : pages) {
            if (!page.hasText()) {
                continue;
            }
            nextChunkIndex = chunkPage(page, nextChunkIndex, chunks);
        }

        return chunks;
    }

    private int chunkPage(ParsedPdfPage page, int nextChunkIndex, List<TextChunk> chunks) {
        List<String> lines = normalizeLines(page.text());
        List<String> currentLines = new ArrayList<>();

        for (String line : lines) {
            if (line.length() > properties.maxCharacters()) {
                nextChunkIndex = flush(page.pageNumber(), nextChunkIndex, currentLines, chunks);
                nextChunkIndex = chunkLongLine(page.pageNumber(), nextChunkIndex, line, chunks);
                continue;
            }

            if (!currentLines.isEmpty() && characterCountWithNewlines(currentLines, line) > properties.maxCharacters()) {
                List<String> previousLines = new ArrayList<>(currentLines);
                nextChunkIndex = flush(page.pageNumber(), nextChunkIndex, currentLines, chunks);
                currentLines.addAll(overlapTail(previousLines));
                if (characterCountWithNewlines(currentLines, line) > properties.maxCharacters()) {
                    currentLines.clear();
                }
            }

            currentLines.add(line);
        }

        return flush(page.pageNumber(), nextChunkIndex, currentLines, chunks);
    }

    private int flush(int pageNumber, int nextChunkIndex, List<String> currentLines, List<TextChunk> chunks) {
        if (currentLines.isEmpty()) {
            return nextChunkIndex;
        }

        chunks.add(new TextChunk(nextChunkIndex, pageNumber, String.join("\n", currentLines)));
        currentLines.clear();
        return nextChunkIndex + 1;
    }

    private int chunkLongLine(int pageNumber, int nextChunkIndex, String line, List<TextChunk> chunks) {
        int start = 0;

        while (start < line.length()) {
            int end = Math.min(start + properties.maxCharacters(), line.length());
            chunks.add(new TextChunk(nextChunkIndex, pageNumber, line.substring(start, end)));
            nextChunkIndex++;

            if (end == line.length()) {
                break;
            }
            start = Math.max(end - properties.overlapCharacters(), start + 1);
        }

        return nextChunkIndex;
    }

    private List<String> normalizeLines(String text) {
        return text.lines()
                .map(line -> line.replaceAll("\\s+", " ").strip())
                .filter(line -> !line.isBlank())
                .toList();
    }

    private int characterCountWithNewlines(List<String> lines, String nextLine) {
        int count = nextLine.length();
        for (String line : lines) {
            count += line.length() + 1;
        }
        return count;
    }

    private List<String> overlapTail(List<String> lines) {
        List<String> tail = new ArrayList<>();
        int count = 0;

        for (int i = lines.size() - 1; i >= 0; i--) {
            String line = lines.get(i);
            int candidateCount = count + line.length() + (tail.isEmpty() ? 0 : 1);
            if (candidateCount > properties.overlapCharacters()) {
                break;
            }
            tail.add(0, line);
            count = candidateCount;
        }

        return tail;
    }
}
