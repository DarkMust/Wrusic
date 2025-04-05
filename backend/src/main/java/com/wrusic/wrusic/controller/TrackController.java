package com.wrusic.wrusic.controller;

import com.wrusic.wrusic.model.Track;
import com.wrusic.wrusic.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tracks")
@CrossOrigin(origins = "*")
public class TrackController {
    
    private final TrackService trackService;
    
    @Autowired
    public TrackController(TrackService trackService) {
        this.trackService = trackService;
    }
    
    @GetMapping
    public ResponseEntity<List<Track>> getAllTracks() {
        List<Track> tracks = trackService.findAllTracks();
        return ResponseEntity.ok(tracks);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Track> getTrackById(@PathVariable Long id) {
        return trackService.findTrackById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Track> createTrack(@RequestBody Track track) {
        Track savedTrack = trackService.saveTrack(track);
        return new ResponseEntity<>(savedTrack, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Track> updateTrack(@PathVariable Long id, @RequestBody Track track) {
        try {
            Track updatedTrack = trackService.updateTrack(id, track);
            return ResponseEntity.ok(updatedTrack);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrack(@PathVariable Long id) {
        try {
            trackService.deleteTrack(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/artist/{artistId}")
    public ResponseEntity<List<Track>> getTracksByArtist(@PathVariable Long artistId) {
        List<Track> tracks = trackService.findTracksByArtistId(artistId);
        return ResponseEntity.ok(tracks);
    }
    
    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<Track>> getTracksByGenre(@PathVariable String genre) {
        List<Track> tracks = trackService.findTracksByGenre(genre);
        return ResponseEntity.ok(tracks);
    }
    
    @GetMapping("/country/{country}")
    public ResponseEntity<List<Track>> getTracksByCountry(@PathVariable String country) {
        List<Track> tracks = trackService.findTracksByCountry(country);
        return ResponseEntity.ok(tracks);
    }
    
    @GetMapping("/latest")
    public ResponseEntity<Page<Track>> getLatestReleases(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("releaseDate").descending());
        Page<Track> tracks = trackService.findLatestReleases(startDate, endDate, pageRequest);
        return ResponseEntity.ok(tracks);
    }
    
    @GetMapping("/charts/global")
    public ResponseEntity<List<Track>> getGlobalCharts() {
        List<Track> tracks = trackService.findTopChartingTracks();
        return ResponseEntity.ok(tracks);
    }
    
    @GetMapping("/charts/country/{country}")
    public ResponseEntity<List<Track>> getCountryCharts(@PathVariable String country) {
        List<Track> tracks = trackService.findTopChartingTracksByCountry(country);
        return ResponseEntity.ok(tracks);
    }
} 