package com.soekm.abrs.service;

import com.soekm.abrs.dto.BoardingSequenceDTO;
import com.soekm.abrs.dto.FlightDTO;
import com.soekm.abrs.entity.enums.FlightStatus;
import com.soekm.abrs.service.iService.IBoardingSequenceService;
import com.soekm.abrs.service.iService.IFlightService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfBoxGenerationService {

    private final IFlightService flightService;
    private final IBoardingSequenceService boardingSequenceService;

    private static final DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final float MARGIN = 50;

    public byte[] generateBoardingReport(Long flightId) throws IOException {
        FlightDTO flight = flightService.getFlight(flightId);

        if (!FlightStatus.BOARDING_CLOSED.name().equals(flight.getStatus())) {
            throw new IllegalStateException("Cannot generate report. Flight must be in BOARDING_CLOSED status.");
        }

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            // PDFBox 3.0+ Constructor: removed 'document' argument
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.OVERWRITE, true)) {
                float yPosition = page.getMediaBox().getHeight() - MARGIN;

                yPosition = addHeader(contentStream, page, yPosition, flight);
                yPosition = addFlightInfo(contentStream, page, yPosition, flight);
                yPosition = addBoardedPassengers(contentStream, page, yPosition, flightId);
                yPosition = addMissingPassengers(contentStream, page, yPosition, flightId);
                addComplianceSection(contentStream, page, yPosition);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);
            return outputStream.toByteArray();
        }
    }

    private float addHeader(PDPageContentStream contentStream, PDPage page, float yPosition, FlightDTO flight) throws IOException {
        PDFont titleFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        PDFont normalFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

        contentStream.setFont(titleFont, 14);
        contentStream.setNonStrokingColor(Color.BLUE);
        contentStream.beginText();
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText("AIRLINE BOARDING REPORT");
        contentStream.endText();

        contentStream.setFont(normalFont, 9);
        contentStream.setNonStrokingColor(Color.BLACK);
        String sub = "Sensitivity: Internal";
        float subW = normalFont.getStringWidth(sub) / 1000 * 9;
        contentStream.beginText();
        contentStream.newLineAtOffset(page.getMediaBox().getWidth() - MARGIN - subW, yPosition);
        contentStream.showText(sub);
        contentStream.endText();

        return yPosition - 20;
    }

    private float addFlightInfo(PDPageContentStream contentStream, PDPage page, float yPosition, FlightDTO flight) throws IOException {
        PDFont boldFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        PDFont normalFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        float fontSize = 9;

        float[] cols = {MARGIN, MARGIN + 110, MARGIN + 210, MARGIN + 350, MARGIN + 450};

        contentStream.setFont(boldFont, fontSize);
        contentStream.beginText();
        contentStream.newLineAtOffset(cols[0], yPosition);
        contentStream.showText("Flight: ");
        contentStream.setFont(normalFont, fontSize);
        contentStream.showText(flight.getFlightNumber());
        contentStream.endText();

        contentStream.beginText();
        contentStream.newLineAtOffset(cols[1], yPosition);
        contentStream.setFont(boldFont, fontSize);
        contentStream.showText("Date: ");
        contentStream.setFont(normalFont, fontSize);
        contentStream.showText(flight.getFlightDate().format(DATE_ONLY_FORMATTER));
        contentStream.endText();

        contentStream.beginText();
        contentStream.newLineAtOffset(cols[3], yPosition);
        contentStream.setFont(boldFont, fontSize);
        contentStream.showText("High Seq: ");
        contentStream.setFont(normalFont, fontSize);
        contentStream.showText(String.valueOf(flight.getCheckedInSeats()));
        contentStream.endText();

        contentStream.beginText();
        contentStream.newLineAtOffset(cols[4], yPosition);
        contentStream.setFont(boldFont, fontSize);
        contentStream.showText("TOB: ");
        contentStream.setFont(normalFont, fontSize);
        contentStream.showText(String.valueOf(flight.getBoardedSeats()));
        contentStream.endText();

        contentStream.setLineWidth(0.5f);
        contentStream.moveTo(MARGIN, yPosition - 5);
        contentStream.lineTo(page.getMediaBox().getWidth() - MARGIN, yPosition - 5);
        contentStream.stroke();

        return yPosition - 25;
    }

    private float addBoardedPassengers(PDPageContentStream contentStream, PDPage page, float yPosition, Long flightId) throws IOException {
        List<BoardingSequenceDTO> boarded = boardingSequenceService.getBoardedPassengers(flightId);
        List<BoardingSequenceDTO> sorted = boarded.stream().sorted(Comparator.comparing(BoardingSequenceDTO::getSequenceNumber)).toList();

        float rowHeight = 11.2f;
        int columns = 15;
        float colWidth = (page.getMediaBox().getWidth() - (2 * MARGIN)) / columns;
        float fontSize = 6.5f;

        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 9);
        contentStream.beginText();
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText("BOARDED SEQUENCES (Total: " + sorted.size() + ")");
        contentStream.endText();

        yPosition -= 12;
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), fontSize);
        contentStream.setLineWidth(0.2f);
        contentStream.setStrokingColor(Color.LIGHT_GRAY);

        for (int i = 0; i < sorted.size(); i++) {
            int col = i % columns;
            int row = i / columns;
            float x = MARGIN + (col * colWidth);
            float y = yPosition - (row * rowHeight);

            contentStream.addRect(x, y - rowHeight, colWidth, rowHeight);
            contentStream.stroke();

            String txt = String.valueOf(sorted.get(i).getSequenceNumber());
            float tw = new PDType1Font(Standard14Fonts.FontName.HELVETICA).getStringWidth(txt) / 1000 * fontSize;
            contentStream.beginText();
            contentStream.newLineAtOffset(x + (colWidth - tw) / 2, y - rowHeight + 3);
            contentStream.showText(txt);
            contentStream.endText();
        }

        int totalRows = (int) Math.ceil((double) sorted.size() / columns);
        return yPosition - (totalRows * rowHeight) - 15;
    }

    private float addMissingPassengers(PDPageContentStream contentStream, PDPage page, float yPosition, Long flightId) throws IOException {
        List<BoardingSequenceDTO> missing = boardingSequenceService.getNoShowPassengers(flightId).stream()
                .limit(15).sorted(Comparator.comparingInt(BoardingSequenceDTO::getSequenceNumber)).toList();

        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 9);
        contentStream.beginText();
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText("MISSING PASSENGERS (Max 15)");
        contentStream.endText();

        yPosition -= 12;
        float rowH = 12.5f;
        float[] cols = {35, 460};

        drawTableHeader(contentStream, MARGIN, yPosition, cols, new String[]{"Seq", "Notes"}, rowH);
        yPosition -= rowH;

        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 8);
        for (BoardingSequenceDTO p : missing) {
            drawTableCell(contentStream, MARGIN, yPosition, cols[0], String.valueOf(p.getSequenceNumber()), rowH);
            drawTableCell(contentStream, MARGIN + cols[0], yPosition, cols[1], p.getNote() != null ? p.getNote() : "-", rowH);
            yPosition -= rowH;
        }
        return yPosition - 10;
    }

    private void addComplianceSection(PDPageContentStream contentStream, PDPage page, float yPosition) throws IOException {
        PDFont normalFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        PDFont boldFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

        // Safety check to ensure the section fits on the page
        if (yPosition < MARGIN + 120) {
            return;
        }

        yPosition -= 20;

        // --- SECTION 1: ZONE 1 COMPLIANCE ---
        contentStream.setFont(boldFont, 10);
        contentStream.beginText();
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText("Zone 1 Boarding Compliance:");
        contentStream.endText();

        contentStream.setFont(normalFont, 10);
        // YES Box & Text
        contentStream.addRect(MARGIN + 175, yPosition - 3, 10, 10);
        contentStream.beginText();
        contentStream.newLineAtOffset(MARGIN + 190, yPosition);
        contentStream.showText("YES");
        contentStream.endText();

        // NO Box & Text
        contentStream.addRect(MARGIN + 210, yPosition - 3, 10, 10);
        contentStream.beginText();
        contentStream.newLineAtOffset(MARGIN + 225, yPosition);
        contentStream.showText("NO");
        contentStream.endText();

        // Justification
        contentStream.beginText();
        contentStream.newLineAtOffset(MARGIN + 255, yPosition);
        contentStream.showText("JUSTIFICATION");
        contentStream.endText();
        contentStream.moveTo(MARGIN + 335, yPosition - 2);
        contentStream.lineTo(MARGIN + 515, yPosition - 2);
        contentStream.stroke();

        yPosition -= 20;

        // --- SECTION 2: MNDU COMPLIANCE ---
        contentStream.setFont(boldFont, 10);
        contentStream.beginText();
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText("MNDU Compliance:");
        contentStream.endText();

        contentStream.setFont(normalFont, 10);
        // YES Box & Text
        contentStream.addRect(MARGIN + 175, yPosition - 3, 10, 10);
        contentStream.beginText();
        contentStream.newLineAtOffset(MARGIN + 190, yPosition);
        contentStream.showText("YES");
        contentStream.endText();

        // NO Box & Text
        contentStream.addRect(MARGIN + 210, yPosition - 3, 10, 10);
        contentStream.beginText();
        contentStream.newLineAtOffset(MARGIN + 225, yPosition);
        contentStream.showText("NO");
        contentStream.endText();

        // Justification
        contentStream.beginText();
        contentStream.newLineAtOffset(MARGIN + 255, yPosition);
        contentStream.showText("JUSTIFICATION");
        contentStream.endText();
        contentStream.moveTo(MARGIN + 335, yPosition - 2);
        contentStream.lineTo(MARGIN + 515, yPosition - 2);
        contentStream.stroke();

        yPosition -= 30;

        // --- SECTION 3: CONSOLIDATED STAFF ROW (SINGLE ROW) ---
        float currentX = MARGIN;
        contentStream.setFont(boldFont, 9);

        // 1. QR Gate Staff Name Line
        contentStream.beginText();
        contentStream.newLineAtOffset(currentX, yPosition);
        contentStream.showText("QR Gate Staff:");
        contentStream.endText();
        contentStream.moveTo(currentX + 65, yPosition - 2);
        contentStream.lineTo(currentX + 165, yPosition - 2); // 100pt for Name
        contentStream.stroke();

        // 2. Staff Number (Short 4-digit)
        contentStream.setFont(normalFont, 9);
        contentStream.beginText();
        contentStream.newLineAtOffset(currentX + 170, yPosition);
        contentStream.showText("No:");
        contentStream.endText();
        contentStream.moveTo(currentX + 185, yPosition - 2);
        contentStream.lineTo(currentX + 220, yPosition - 2); // 35pt for 4 digits
        contentStream.stroke();

        // 3. Verified By Name Line
        contentStream.setFont(boldFont, 9);
        contentStream.beginText();
        contentStream.newLineAtOffset(currentX + 235, yPosition);
        contentStream.showText("Verified By:");
        contentStream.endText();
        contentStream.moveTo(currentX + 295, yPosition - 2);
        contentStream.lineTo(currentX + 395, yPosition - 2); // 100pt for Name
        contentStream.stroke();

        // 4. Verified Staff Number (Short 4-digit)
        contentStream.setFont(normalFont, 9);
        contentStream.beginText();
        contentStream.newLineAtOffset(currentX + 400, yPosition);
        contentStream.showText("No:");
        contentStream.endText();
        contentStream.moveTo(currentX + 415, yPosition - 2);
        contentStream.lineTo(currentX + 450, yPosition - 2); // 35pt for 4 digits
        contentStream.stroke();

        yPosition -= 25;

        // --- SECTION 4: REMARKS ---
        contentStream.setFont(boldFont, 10);
        contentStream.beginText();
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText("Remarks Details:");
        contentStream.endText();

        contentStream.moveTo(MARGIN + 90, yPosition - 2);
        contentStream.lineTo(page.getMediaBox().getWidth() - MARGIN, yPosition - 2);
        contentStream.stroke();
    }

    private void drawTableHeader(PDPageContentStream contentStream, float x, float y, float[] columnWidths, String[] headers, float rowHeight) throws IOException {
        float currentX = x;
        contentStream.setLineWidth(0.5f);
        contentStream.setStrokingColor(Color.BLACK);
        for (int i = 0; i < headers.length; i++) {
            contentStream.addRect(currentX, y - rowHeight, columnWidths[i], rowHeight);
            contentStream.stroke();
            contentStream.beginText();
            contentStream.newLineAtOffset(currentX + 5, y - rowHeight + 4);
            contentStream.showText(headers[i]);
            contentStream.endText();
            currentX += columnWidths[i];
        }
    }

    private void drawTableCell(PDPageContentStream contentStream, float x, float y, float width, String text, float rowHeight) throws IOException {
        contentStream.addRect(x, y - rowHeight, width, rowHeight);
        contentStream.stroke();
        contentStream.beginText();
        contentStream.newLineAtOffset(x + 5, y - rowHeight + 4);
        contentStream.showText(text != null ? text : "");
        contentStream.endText();
    }
}