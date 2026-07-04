package com.studysheetai.testutil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

public final class PdfTestFixtures {

    private PdfTestFixtures() {
    }

    public static byte[] lectureSlidesPdf() throws IOException {
        return pdf(List.of(
                List.of(
                        "Dynamic Programming",
                        "- Define state",
                        "- Write recurrence",
                        "- Choose base case"
                ),
                List.of(
                        "Greedy Algorithms",
                        "- Make locally optimal choice",
                        "- Prove exchange argument"
                )
        ));
    }

    private static byte[] pdf(List<List<String>> pages) throws IOException {
        try (PDDocument document = new PDDocument()) {
            for (List<String> lines : pages) {
                PDPage page = new PDPage();
                document.addPage(page);

                try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                    content.beginText();
                    content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                    content.setLeading(16);
                    content.newLineAtOffset(72, 720);

                    for (String line : lines) {
                        content.showText(line);
                        content.newLine();
                    }

                    content.endText();
                }
            }

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            document.save(output);
            return output.toByteArray();
        }
    }
}
