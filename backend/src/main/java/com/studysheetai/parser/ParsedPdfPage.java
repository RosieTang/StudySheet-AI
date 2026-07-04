package com.studysheetai.parser;

public record ParsedPdfPage(
        int pageNumber,
        String text
) {
    public ParsedPdfPage {
        if (pageNumber < 1) {
            throw new IllegalArgumentException("pageNumber must be 1 or greater");
        }
        text = text == null ? "" : text.strip();
    }

    public int characterCount() {
        return text.length();
    }

    public boolean hasText() {
        return !text.isBlank();
    }
}
