package com.wrusic.wrusic.service;

import com.wrusic.wrusic.model.Track;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrackService {
    
    List<Track> findAllTracks();
    
    Optional<Track> findTrackById(Long id);
    
    Track saveTrack(Track track);
    
    Track updateTrack(Long id, Track track);
    
    void deleteTrack(Long id);
    
    List<Track> findTracksByArtistId(Long artistId);
    
    List<Track> findTracksByGenre(String genre);
    
    List<Track> findTracksByCountry(String country);
    
    Page<Track> findLatestReleases(LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    List<Track> findTopChartingTracks();
    
    List<Track> findTopChartingTracksByCountry(String country);
} 