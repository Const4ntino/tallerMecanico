package com.example.gestion.taller_mecanico.service;

import com.example.gestion.taller_mecanico.model.dto.DashboardSummaryResponse;

import java.time.LocalDateTime;

public interface DashboardService {
    DashboardSummaryResponse getDashboardSummary(LocalDateTime startDate, LocalDateTime endDate, String groupBy);
}
