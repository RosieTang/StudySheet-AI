package com.studysheetai.service;

import java.io.InputStream;

public record DocumentProcessingRequest(
        String originalFilename,
        String contentType,
        long fileSize,
        InputStream inputStream
) {
}
