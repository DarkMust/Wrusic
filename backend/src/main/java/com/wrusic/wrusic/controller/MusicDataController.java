package com.wrusic.wrusic.controller;

import com.wrusic.wrusic.model.Album;
import com.wrusic.wrusic.model.Artist;
import com.wrusic.wrusic.model.Track;
import com.wrusic.wrusic.service.MusicDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/music")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class MusicDataController {

    private final MusicDataService musicDataService;

    @GetMapping("/search/artists")
    public ResponseEntity<List<Artist>> searchArtists(@RequestParam String query) {
        return ResponseEntity.ok(musicDataService.searchArtists(query));
    }

    @GetMapping("/artists/{spotifyId}/{lastFmName}/{musicBrainzId}")
    public ResponseEntity<Artist> getArtistDetails(
            @PathVariable(required = false) String spotifyId,
            @PathVariable(required = false) String lastFmName,
            @PathVariable(required = false) String musicBrainzId) {
        return ResponseEntity.ok(musicDataService.getArtistDetails(spotifyId, lastFmName, musicBrainzId));
    }

    @GetMapping("/artists/{spotifyId}/{lastFmName}/{musicBrainzId}/albums")
    public ResponseEntity<List<Album>> getArtistAlbums(
            @PathVariable(required = false) String spotifyId,
            @PathVariable(required = false) String lastFmName,
            @PathVariable(required = false) String musicBrainzId) {
        return ResponseEntity.ok(musicDataService.getArtistAlbums(spotifyId, lastFmName, musicBrainzId));
    }

    @GetMapping("/artists/{spotifyId}/{lastFmName}/{musicBrainzId}/tracks")
    public ResponseEntity<List<Track>> getArtistTracks(
            @PathVariable(required = false) String spotifyId,
            @PathVariable(required = false) String lastFmName,
            @PathVariable(required = false) String musicBrainzId) {
        return ResponseEntity.ok(musicDataService.getArtistTracks(spotifyId, lastFmName, musicBrainzId));
    }

    @GetMapping("/charts/country/{country}")
    public ResponseEntity<List<Track>> getTopTracksByCountry(@PathVariable String country) {
        return ResponseEntity.ok(musicDataService.getTopTracksByCountry(country));
    }
} 