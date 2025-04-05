package com.wrusic.wrusic.service;

import com.wrusic.wrusic.model.Album;
import com.wrusic.wrusic.model.Artist;
import com.wrusic.wrusic.model.Track;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpotifyService {

    private final WebClient spotifyWebClient;

    @Value("${external.api.spotify.client-id}")
    private String clientId;

    @Value("${external.api.spotify.client-secret}")
    private String clientSecret;

    @Value("${external.api.spotify.token-uri:https://accounts.spotify.com/api/token}")
    private String tokenUri;

    private String accessToken;
    private long tokenExpirationTime;

    public List<Artist> searchArtists(String query) {
        try {
            Map<String, Object> response = spotifyWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search")
                            .queryParam("q", query)
                            .queryParam("type", "artist")
                            .queryParam("limit", 20)
                            .build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.containsKey("artists") && 
                response.get("artists") instanceof Map && 
                ((Map) response.get("artists")).containsKey("items")) {
                
                List<Map<String, Object>> items = (List<Map<String, Object>>) ((Map) response.get("artists")).get("items");
                
                return items.stream()
                        .map(this::mapToArtist)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("Error searching artists: {}", e.getMessage());
        }
        
        return new ArrayList<>();
    }

    public Artist getArtistById(String spotifyId) {
        try {
            Map<String, Object> response = spotifyWebClient.get()
                    .uri("/artists/{id}", spotifyId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null) {
                return mapToArtist(response);
            }
        } catch (Exception e) {
            log.error("Error getting artist by ID: {}", e.getMessage());
        }
        
        return null;
    }

    public List<Album> getAlbumsByArtist(String artistId) {
        try {
            Map<String, Object> response = spotifyWebClient.get()
                    .uri("/artists/{id}/albums", artistId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.containsKey("items")) {
                List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");
                
                return items.stream()
                        .map(this::mapToAlbum)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("Error getting albums by artist: {}", e.getMessage());
        }
        
        return new ArrayList<>();
    }

    public List<Track> getTracksByAlbum(String albumId) {
        try {
            Map<String, Object> response = spotifyWebClient.get()
                    .uri("/albums/{id}/tracks", albumId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.containsKey("items")) {
                List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");
                
                return items.stream()
                        .map(this::mapToTrack)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("Error getting tracks by album: {}", e.getMessage());
        }
        
        return new ArrayList<>();
    }

    public List<Track> getTopTracksByArtist(String artistId) {
        try {
            Map<String, Object> response = spotifyWebClient.get()
                    .uri("/artists/{id}/top-tracks", artistId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.containsKey("tracks")) {
                List<Map<String, Object>> tracks = (List<Map<String, Object>>) response.get("tracks");
                
                return tracks.stream()
                        .map(this::mapToTrack)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("Error getting top tracks by artist: {}", e.getMessage());
        }
        
        return new ArrayList<>();
    }

    /**
     * Get new releases
     */
    public Mono<List<Album>> getNewReleases(String country, int limit) {
        return getAccessToken()
                .flatMap(token -> spotifyWebClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/browse/new-releases")
                                .queryParam("country", country)
                                .queryParam("limit", limit)
                                .build())
                        .header("Authorization", "Bearer " + token)
                        .retrieve()
                        .bodyToMono(Map.class)
                        .map(response -> {
                            Map<String, Object> albums = (Map<String, Object>) response.get("albums");
                            List<Map<String, Object>> items = (List<Map<String, Object>>) albums.get("items");
                            return items.stream()
                                    .map(this::mapToAlbum)
                                    .toList();
                        }));
    }

    /**
     * Get featured playlists (charts)
     */
    public Mono<List<Track>> getTopTracks(String country, int limit) {
        return getAccessToken()
                .flatMap(token -> spotifyWebClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/playlists/37i9dQZEVXbMDoHDwVN2tF/tracks") // Global Top 50 playlist
                                .queryParam("limit", limit)
                                .build())
                        .header("Authorization", "Bearer " + token)
                        .retrieve()
                        .bodyToMono(Map.class)
                        .map(response -> {
                            List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");
                            return items.stream()
                                    .map(item -> (Map<String, Object>) item.get("track"))
                                    .map(this::mapToTrack)
                                    .toList();
                        }));
    }

    private Mono<String> getAccessToken() {
        if (accessToken != null && System.currentTimeMillis() < tokenExpirationTime) {
            return Mono.just(accessToken);
        }

        String credentials = clientId + ":" + clientSecret;
        String encodedCredentials = Base64Utils.encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        return WebClient.create()
                .post()
                .uri(tokenUri)
                .header("Authorization", "Basic " + encodedCredentials)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "client_credentials"))
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    accessToken = (String) response.get("access_token");
                    int expiresIn = (Integer) response.get("expires_in");
                    tokenExpirationTime = System.currentTimeMillis() + (expiresIn * 1000L);
                    return accessToken;
                });
    }

    private Artist mapToArtist(Map<String, Object> data) {
        Artist artist = new Artist();
        artist.setName((String) data.get("name"));
        
        if (data.containsKey("images") && data.get("images") instanceof List) {
            List<Map<String, Object>> images = (List<Map<String, Object>>) data.get("images");
            if (!images.isEmpty() && images.get(0).containsKey("url")) {
                artist.setImageUrl((String) images.get(0).get("url"));
            }
        }
        
        return artist;
    }

    private Album mapToAlbum(Map<String, Object> data) {
        Album album = new Album();
        album.setTitle((String) data.get("name"));
        album.setSpotifyId((String) data.get("id"));
        album.setReleaseDate((String) data.get("release_date"));
        
        List<Map<String, Object>> images = (List<Map<String, Object>>) data.get("images");
        if (!images.isEmpty()) {
            album.setCoverUrl((String) images.get(0).get("url"));
        }
        
        return album;
    }

    private Track mapToTrack(Map<String, Object> data) {
        Track track = new Track();
        track.setTitle((String) data.get("name"));
        track.setSpotifyId((String) data.get("id"));
        track.setDuration((Integer) data.get("duration_ms"));
        track.setPreviewUrl((String) data.get("preview_url"));
        
        Map<String, Object> album = (Map<String, Object>) data.get("album");
        if (album != null) {
            List<Map<String, Object>> images = (List<Map<String, Object>>) album.get("images");
            if (!images.isEmpty()) {
                track.setImageUrl((String) images.get(0).get("url"));
            }
        }
        
        return track;
    }
} 