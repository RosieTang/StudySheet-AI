package com.studysheetai.parser;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.studysheetai.testutil.PdfTestFixtures;

class PdfParserServiceTest {

    private final PdfParserService pdfParserService = new PdfParserService();

    @Test
    void extractsTextFromGeneratedLectureSlidePdfByPage() throws Exception {
        byte[] pdf = PdfTestFixtures.lectureSlidesPdf();

        List<ParsedPdfPage> pages = pdfParserService.parse(new ByteArrayInputStream(pdf));

        assertThat(pages).hasSize(2);
        assertThat(pages.get(0).pageNumber()).isEqualTo(1);
        assertThat(pages.get(0).text()).contains("Dynamic Programming", "Define state");
        assertThat(pages.get(1).pageNumber()).isEqualTo(2);
        assertThat(pages.get(1).text()).contains("Greedy Algorithms", "exchange argument");
    }
}
