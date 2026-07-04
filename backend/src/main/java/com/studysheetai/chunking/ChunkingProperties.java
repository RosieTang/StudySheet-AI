package com.studysheetai.chunking;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;

@Validated
@ConfigurationProperties(prefix = "app.chunking")
public record ChunkingProperties(
        @Min(100) int maxCharacters,
        @Min(0) int overlapCharacters
) {
    public ChunkingProperties {
        if (overlapCharacters >= maxCharacters) {
            throw new IllegalArgumentException("overlapCharacters must be smaller than maxCharacters");
        }
    }
}
