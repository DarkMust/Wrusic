package com.wrusic.wrusic.service.impl;

import com.wrusic.wrusic.model.ChartEntry;
import com.wrusic.wrusic.repository.ChartEntryRepository;
import com.wrusic.wrusic.service.ChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class ChartServiceImpl implements ChartService {
    
    private final ChartEntryRepository chartEntryRepository;
    
    @Autowired
    public ChartServiceImpl(ChartEntryRepository chartEntryRepository) {
        this.chartEntryRepository = chartEntryRepository;
    }
    
    @Override
    public List<ChartEntry> findGlobalChartByDate(String chartType, LocalDate date) {
        return chartEntryRepository.findGlobalChartByDate(chartType, date);
    }
    
    @Override
    public List<ChartEntry> findCountryChartByDate(String chartType, String country, LocalDate date) {
        return chartEntryRepository.findCountryChartByDate(chartType, country, date);
    }
    
    @Override
    public List<LocalDate> findAvailableChartDates(String chartType) {
        return chartEntryRepository.findAvailableChartDates(chartType);
    }
    
    @Override
    public List<LocalDate> findAvailableChartDatesByCountry(String chartType, String country) {
        return chartEntryRepository.findAvailableChartDatesByCountry(chartType, country);
    }
    
    @Override
    public ChartEntry saveChartEntry(ChartEntry chartEntry) {
        return chartEntryRepository.save(chartEntry);
    }
    
    @Override
    public void deleteChartEntry(Long id) {
        if (!chartEntryRepository.existsById(id)) {
            throw new IllegalArgumentException("Chart entry with ID " + id + " not found");
        }
        
        chartEntryRepository.deleteById(id);
    }
    
    @Override
    public List<ChartEntry> findChartEntriesByTrackId(Long trackId) {
        // This method would need to be implemented in the repository
        // For now, we'll return an empty list
        return List.of();
    }
} 