package com.wrusic.wrusic.controller;

import org.springframework.web.bind.annotation.*;
import com.wrusic.wrusic.service.LastFmService;
import reactor.core.publisher.Mono;
import java.util.Map;

@RestController
@RequestMapping("/music")
@CrossOrigin(origins = "*") // In production, replace with your frontend URL
public class LastFmController {
    private final LastFmService lastFmService;

    public LastFmController(LastFmService lastFmService) {
        this.lastFmService = lastFmService;
    }

    @GetMapping("/new-releases")
    public Mono<Map<String, Object>> getNewReleases(
            @RequestParam(defaultValue = "US") String country,
            @RequestParam(defaultValue = "20") int limit) {
        return lastFmService.getNewReleases(country, limit);
    }

    @GetMapping("/charts")
    public Mono<Map<String, Object>> getTopTracks(
            @RequestParam(defaultValue = "US") String country,
            @RequestParam(defaultValue = "50") int limit) {
        return lastFmService.getTopTracks(country, limit);
    }
    
    @GetMapping("/top-albums")
    public Mono<Map<String, Object>> getTopAlbums(
            @RequestParam(defaultValue = "US") String country,
            @RequestParam(defaultValue = "50") int limit) {
        return lastFmService.getTopAlbums(country, limit);
    }
    
    @GetMapping("/top-artists")
    public Mono<Map<String, Object>> getTopArtists(
            @RequestParam(defaultValue = "US") String country,
            @RequestParam(defaultValue = "50") int limit) {
        return lastFmService.getTopArtists(country, limit);
    }
} 