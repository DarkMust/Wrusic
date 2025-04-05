package com.wrusic.wrusic.repository;

import com.wrusic.wrusic.model.Track;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {
    
    Page<Track> findByReleaseDateBetweenOrderByReleaseDateDesc(LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    List<Track> findByArtistId(Long artistId);
    
    List<Track> findByGenreIgnoreCase(String genre);
    
    List<Track> findByCountryIgnoreCase(String country);
    
    @Query("SELECT t FROM Track t WHERE t.chartPosition IS NOT NULL ORDER BY t.chartPosition ASC")
    List<Track> findTopChartingTracks();
    
    @Query("SELECT t FROM Track t WHERE t.country = ?1 AND t.chartPosition IS NOT NULL ORDER BY t.chartPosition ASC")
    List<Track> findTopChartingTracksByCountry(String country);
} 