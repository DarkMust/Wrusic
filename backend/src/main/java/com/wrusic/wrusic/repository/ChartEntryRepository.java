package com.wrusic.wrusic.repository;

import com.wrusic.wrusic.model.ChartEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ChartEntryRepository extends JpaRepository<ChartEntry, Long> {
    
    List<ChartEntry> findByChartTypeAndEntryDateOrderByPositionAsc(String chartType, LocalDate entryDate);
    
    List<ChartEntry> findByChartTypeAndCountryAndEntryDateOrderByPositionAsc(String chartType, String country, LocalDate entryDate);
    
    @Query("SELECT ce FROM ChartEntry ce WHERE ce.chartType = ?1 AND ce.entryDate = ?2 ORDER BY ce.position ASC")
    List<ChartEntry> findGlobalChartByDate(String chartType, LocalDate entryDate);
    
    @Query("SELECT ce FROM ChartEntry ce WHERE ce.chartType = ?1 AND ce.country = ?2 AND ce.entryDate = ?3 ORDER BY ce.position ASC")
    List<ChartEntry> findCountryChartByDate(String chartType, String country, LocalDate entryDate);
    
    @Query("SELECT DISTINCT ce.entryDate FROM ChartEntry ce WHERE ce.chartType = ?1 ORDER BY ce.entryDate DESC")
    List<LocalDate> findAvailableChartDates(String chartType);
    
    @Query("SELECT DISTINCT ce.entryDate FROM ChartEntry ce WHERE ce.chartType = ?1 AND ce.country = ?2 ORDER BY ce.entryDate DESC")
    List<LocalDate> findAvailableChartDatesByCountry(String chartType, String country);
} 