package com.wrusic.wrusic.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "chart_entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChartEntry {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "track_id", nullable = false)
    private Track track;
    
    @Column(nullable = false)
    private Integer position;
    
    @Column(nullable = false)
    private String chartType; // GLOBAL, COUNTRY
    
    private String country;
    
    @Column(nullable = false)
    private LocalDate entryDate;
    
    private Integer previousPosition;
    
    private Integer weeksOnChart;
} 