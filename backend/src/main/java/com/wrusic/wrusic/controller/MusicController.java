package com.wrusic.wrusic.controller;

import com.wrusic.wrusic.model.Album;
import com.wrusic.wrusic.model.Track;
import com.wrusic.wrusic.service.SpotifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/music")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class MusicController {

    private final SpotifyService spotifyService;

    @GetMapping("/new-releases")
    public Mono<ResponseEntity<List<Album>>> getNewReleases(
            @RequestParam(defaultValue = "US") String country,
            @RequestParam(defaultValue = "20") int limit) {
        return spotifyService.getNewReleases(country, limit)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/charts/{country}")
    public Mono<ResponseEntity<List<Track>>> getTopTracks(
            @PathVariable String country,
            @RequestParam(defaultValue = "50") int limit) {
        return spotifyService.getTopTracks(country, limit)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
} 