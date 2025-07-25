package com.example.gestion.taller_mecanico.controller;

import com.example.gestion.taller_mecanico.model.dto.DashboardSummaryResponse;
import com.example.gestion.taller_mecanico.service.DashboardService;
import com.example.gestion.taller_mecanico.service.PdfGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final PdfGeneratorService pdfGeneratorService;

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getDashboardSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String groupBy) {
        return ResponseEntity.ok(dashboardService.getDashboardSummary(startDate, endDate, groupBy));
    }
    
    /**
     * Endpoint para generar un PDF con las métricas del dashboard
     * @param startDate Fecha de inicio del periodo (opcional)
     * @param endDate Fecha de fin del periodo (opcional)
     * @param groupBy Agrupación de datos (MONTH o YEAR)
     * @return PDF con las métricas del dashboard
     */
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @GetMapping("/summary/pdf")
    public ResponseEntity<byte[]> getDashboardSummaryPdf(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String groupBy) {
        
        // Obtener los datos del dashboard usando el servicio existente
        DashboardSummaryResponse dashboardData = dashboardService.getDashboardSummary(startDate, endDate, groupBy);
        
        // Generar el PDF con los datos obtenidos
        byte[] pdfBytes = pdfGeneratorService.generateDashboardPdf(dashboardData, startDate, endDate, groupBy);
        
        // Configurar los headers para la descarga del PDF
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "dashboard-report.pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
