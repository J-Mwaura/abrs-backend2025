package com.soekm.abrs.controller;

import com.soekm.abrs.service.PdfBoxGenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final PdfBoxGenerationService pdfService;

    @GetMapping("/boarding/{flightId}")
    public ResponseEntity<byte[]> downloadBoardingReport(@PathVariable Long flightId
    ) throws IOException {
        byte[] pdfContent = pdfService.generateBoardingReport(flightId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        // This header forces the browser to download the file with a specific name
        headers.setContentDispositionFormData("attachment", "boarding_report_flight_" + flightId + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfContent);
    }


//    @PreAuthorize("hasAnyRole('USER', 'SUPERVISOR')")
//    @GetMapping("/boarding/{flightId}")
//    public ResponseEntity<byte[]> downloadBoardingReport(
//            @PathVariable Long flightId,
//            Authentication authentication) throws IOException {
//
//        // 1. Log the user and their authorities
//        if (authentication != null) {
//            log.info("User: {}", authentication.getName());
//            log.info("Authorities from JWT: {}", authentication.getAuthorities().stream()
//                    .map(GrantedAuthority::getAuthority)
//                    .toList());
//        }
//
//        byte[] pdfContent = pdfService.generateBoardingReport(flightId);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_PDF);
//        headers.setContentDispositionFormData("attachment", "boarding_report_flight_" + flightId + ".pdf");
//
//        return ResponseEntity.ok()
//                .headers(headers)
//                .body(pdfContent);
//    }
}