package com.example.gestion.taller_mecanico.controller;

import com.example.gestion.taller_mecanico.model.dto.DashboardSummaryResponse;
import com.example.gestion.taller_mecanico.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_TALLER')")
    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getDashboardSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String groupBy) {
        return ResponseEntity.ok(dashboardService.getDashboardSummary(startDate, endDate, groupBy));
    }
}
