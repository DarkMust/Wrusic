package com.wrusic.wrusic.controller;

import com.wrusic.wrusic.model.Artist;
import com.wrusic.wrusic.model.Album;
import com.wrusic.wrusic.model.Track;
import com.wrusic.wrusic.service.SpotifyApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/spotify")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
public class SpotifyApiController {

    private final SpotifyApiService spotifyApiService;

    /**
     * Search for artists
     */
    @GetMapping("/search/artists")
    public Mono<ResponseEntity<List<Artist>>> searchArtists(
            @AuthenticationPrincipal OAuth2AuthenticationToken authentication,
            @RequestParam String query) {
        return spotifyApiService.searchArtists(authentication, query)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Error searching artists: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().build());
                });
    }

    /**
     * Get artist details
     */
    @GetMapping("/artists/{artistId}")
    public Mono<ResponseEntity<Artist>> getArtist(
            @AuthenticationPrincipal OAuth2AuthenticationToken authentication,
            @PathVariable String artistId) {
        return spotifyApiService.getArtist(authentication, artistId)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Error getting artist: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().build());
                });
    }

    /**
     * Get artist's albums
     */
    @GetMapping("/artists/{artistId}/albums")
    public Mono<ResponseEntity<List<Album>>> getArtistAlbums(
            @AuthenticationPrincipal OAuth2AuthenticationToken authentication,
            @PathVariable String artistId) {
        return spotifyApiService.getArtistAlbums(authentication, artistId)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Error getting artist albums: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().build());
                });
    }

    /**
     * Get album tracks
     */
    @GetMapping("/albums/{albumId}/tracks")
    public Mono<ResponseEntity<List<Track>>> getAlbumTracks(
            @AuthenticationPrincipal OAuth2AuthenticationToken authentication,
            @PathVariable String albumId) {
        return spotifyApiService.getAlbumTracks(authentication, albumId)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Error getting album tracks: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().build());
                });
    }

    /**
     * Get artist's top tracks
     */
    @GetMapping("/artists/{artistId}/top-tracks")
    public Mono<ResponseEntity<List<Track>>> getArtistTopTracks(
            @AuthenticationPrincipal OAuth2AuthenticationToken authentication,
            @PathVariable String artistId) {
        return spotifyApiService.getArtistTopTracks(authentication, artistId)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Error getting artist top tracks: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().build());
                });
    }
} 