package com.wrusic.wrusic.service.impl;

import com.wrusic.wrusic.model.Track;
import com.wrusic.wrusic.repository.TrackRepository;
import com.wrusic.wrusic.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TrackServiceImpl implements TrackService {
    
    private final TrackRepository trackRepository;
    
    @Autowired
    public TrackServiceImpl(TrackRepository trackRepository) {
        this.trackRepository = trackRepository;
    }
    
    @Override
    public List<Track> findAllTracks() {
        return trackRepository.findAll();
    }
    
    @Override
    public Optional<Track> findTrackById(Long id) {
        return trackRepository.findById(id);
    }
    
    @Override
    public Track saveTrack(Track track) {
        return trackRepository.save(track);
    }
    
    @Override
    public Track updateTrack(Long id, Track track) {
        if (!trackRepository.existsById(id)) {
            throw new IllegalArgumentException("Track with ID " + id + " not found");
        }
        
        track.setId(id);
        return trackRepository.save(track);
    }
    
    @Override
    public void deleteTrack(Long id) {
        if (!trackRepository.existsById(id)) {
            throw new IllegalArgumentException("Track with ID " + id + " not found");
        }
        
        trackRepository.deleteById(id);
    }
    
    @Override
    public List<Track> findTracksByArtistId(Long artistId) {
        return trackRepository.findByArtistId(artistId);
    }
    
    @Override
    public List<Track> findTracksByGenre(String genre) {
        return trackRepository.findByGenreIgnoreCase(genre);
    }
    
    @Override
    public List<Track> findTracksByCountry(String country) {
        return trackRepository.findByCountryIgnoreCase(country);
    }
    
    @Override
    public Page<Track> findLatestReleases(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return trackRepository.findByReleaseDateBetweenOrderByReleaseDateDesc(startDate, endDate, pageable);
    }
    
    @Override
    public List<Track> findTopChartingTracks() {
        return trackRepository.findTopChartingTracks();
    }
    
    @Override
    public List<Track> findTopChartingTracksByCountry(String country) {
        return trackRepository.findTopChartingTracksByCountry(country);
    }
} 