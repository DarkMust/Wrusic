package com.wrusic.wrusic.service;

import com.wrusic.wrusic.model.ChartEntry;

import java.time.LocalDate;
import java.util.List;

public interface ChartService {
    
    List<ChartEntry> findGlobalChartByDate(String chartType, LocalDate date);
    
    List<ChartEntry> findCountryChartByDate(String chartType, String country, LocalDate date);
    
    List<LocalDate> findAvailableChartDates(String chartType);
    
    List<LocalDate> findAvailableChartDatesByCountry(String chartType, String country);
    
    ChartEntry saveChartEntry(ChartEntry chartEntry);
    
    void deleteChartEntry(Long id);
    
    List<ChartEntry> findChartEntriesByTrackId(Long trackId);
} 