package com.wrusic.wrusic.controller;

import com.wrusic.wrusic.model.ChartEntry;
import com.wrusic.wrusic.service.ChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/charts")
@CrossOrigin(origins = "*")
public class ChartController {
    
    private final ChartService chartService;
    
    @Autowired
    public ChartController(ChartService chartService) {
        this.chartService = chartService;
    }
    
    @GetMapping("/global")
    public ResponseEntity<List<ChartEntry>> getGlobalChart(
            @RequestParam String chartType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        List<ChartEntry> chartEntries = chartService.findGlobalChartByDate(chartType, date);
        return ResponseEntity.ok(chartEntries);
    }
    
    @GetMapping("/country/{country}")
    public ResponseEntity<List<ChartEntry>> getCountryChart(
            @PathVariable String country,
            @RequestParam String chartType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        List<ChartEntry> chartEntries = chartService.findCountryChartByDate(chartType, country, date);
        return ResponseEntity.ok(chartEntries);
    }
    
    @GetMapping("/dates")
    public ResponseEntity<List<LocalDate>> getAvailableChartDates(@RequestParam String chartType) {
        List<LocalDate> dates = chartService.findAvailableChartDates(chartType);
        return ResponseEntity.ok(dates);
    }
    
    @GetMapping("/dates/country/{country}")
    public ResponseEntity<List<LocalDate>> getAvailableChartDatesByCountry(
            @PathVariable String country,
            @RequestParam String chartType) {
        
        List<LocalDate> dates = chartService.findAvailableChartDatesByCountry(chartType, country);
        return ResponseEntity.ok(dates);
    }
    
    @PostMapping
    public ResponseEntity<ChartEntry> createChartEntry(@RequestBody ChartEntry chartEntry) {
        ChartEntry savedChartEntry = chartService.saveChartEntry(chartEntry);
        return new ResponseEntity<>(savedChartEntry, HttpStatus.CREATED);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChartEntry(@PathVariable Long id) {
        try {
            chartService.deleteChartEntry(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
} 