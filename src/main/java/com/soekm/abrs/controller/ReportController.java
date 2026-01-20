package com.soekm.abrs.controller;

import com.soekm.abrs.service.PdfBoxGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final PdfBoxGenerationService pdfService;

    @GetMapping("/boarding/{flightId}")
    public ResponseEntity<byte[]> downloadBoardingReport(@PathVariable Long flightId) throws IOException {
        byte[] pdfContent = pdfService.generateBoardingReport(flightId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        // This header forces the browser to download the file with a specific name
        headers.setContentDispositionFormData("attachment", "boarding_report_flight_" + flightId + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfContent);
    }
}