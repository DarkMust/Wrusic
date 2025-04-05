package com.wrusic.wrusic.service;

import com.wrusic.wrusic.model.Album;
import com.wrusic.wrusic.model.Artist;
import com.wrusic.wrusic.model.Track;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LastFmService {

    private final WebClient lastfmWebClient;

    @Value("${external.api.lastfm.api-key}")
    private String apiKey;

    public List<Artist> searchArtists(String query) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl("http://ws.audioscrobbler.com/2.0/")
                    .queryParam("method", "artist.search")
                    .queryParam("artist", query)
                    .queryParam("api_key", apiKey)
                    .queryParam("format", "json")
                    .build()
                    .toUriString();

            Map<String, Object> response = lastfmWebClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.containsKey("results") && 
                response.get("results") instanceof Map && 
                ((Map) response.get("results")).containsKey("artistmatches") &&
                ((Map) ((Map) response.get("results")).get("artistmatches")).containsKey("artist")) {
                
                List<Map<String, Object>> artists = (List<Map<String, Object>>) 
                    ((Map) ((Map) response.get("results")).get("artistmatches")).get("artist");
                
                return artists.stream()
                        .map(this::mapToArtist)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("Error searching artists on Last.fm: {}", e.getMessage());
        }
        
        return new ArrayList<>();
    }

    public Artist getArtistInfo(String artistName) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl("http://ws.audioscrobbler.com/2.0/")
                    .queryParam("method", "artist.getInfo")
                    .queryParam("artist", artistName)
                    .queryParam("api_key", apiKey)
                    .queryParam("format", "json")
                    .build()
                    .toUriString();

            Map<String, Object> response = lastfmWebClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.containsKey("artist")) {
                return mapToArtist((Map<String, Object>) response.get("artist"));
            }
        } catch (Exception e) {
            log.error("Error getting artist info from Last.fm: {}", e.getMessage());
        }
        
        return null;
    }

    public List<Album> getTopAlbumsByArtist(String artistName) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl("http://ws.audioscrobbler.com/2.0/")
                    .queryParam("method", "artist.getTopAlbums")
                    .queryParam("artist", artistName)
                    .queryParam("api_key", apiKey)
                    .queryParam("format", "json")
                    .build()
                    .toUriString();

            Map<String, Object> response = lastfmWebClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.containsKey("topalbums") && 
                response.get("topalbums") instanceof Map && 
                ((Map) response.get("topalbums")).containsKey("album")) {
                
                List<Map<String, Object>> albums = (List<Map<String, Object>>) 
                    ((Map) response.get("topalbums")).get("album");
                
                return albums.stream()
                        .map(this::mapToAlbum)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("Error getting top albums from Last.fm: {}", e.getMessage());
        }
        
        return new ArrayList<>();
    }

    public List<Track> getTopTracksByArtist(String artistName) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl("http://ws.audioscrobbler.com/2.0/")
                    .queryParam("method", "artist.getTopTracks")
                    .queryParam("artist", artistName)
                    .queryParam("api_key", apiKey)
                    .queryParam("format", "json")
                    .build()
                    .toUriString();

            Map<String, Object> response = lastfmWebClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.containsKey("toptracks") && 
                response.get("toptracks") instanceof Map && 
                ((Map) response.get("toptracks")).containsKey("track")) {
                
                List<Map<String, Object>> tracks = (List<Map<String, Object>>) 
                    ((Map) response.get("toptracks")).get("track");
                
                return tracks.stream()
                        .map(this::mapToTrack)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("Error getting top tracks from Last.fm: {}", e.getMessage());
        }
        
        return new ArrayList<>();
    }

    public List<Track> getTopTracksByCountry(String country) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl("http://ws.audioscrobbler.com/2.0/")
                    .queryParam("method", "geo.getTopTracks")
                    .queryParam("country", country)
                    .queryParam("api_key", apiKey)
                    .queryParam("format", "json")
                    .build()
                    .toUriString();

            Map<String, Object> response = lastfmWebClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.containsKey("tracks") && 
                response.get("tracks") instanceof Map && 
                ((Map) response.get("tracks")).containsKey("track")) {
                
                List<Map<String, Object>> tracks = (List<Map<String, Object>>) 
                    ((Map) response.get("tracks")).get("track");
                
                return tracks.stream()
                        .map(this::mapToTrack)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("Error getting top tracks by country from Last.fm: {}", e.getMessage());
        }
        
        return new ArrayList<>();
    }

    private Artist mapToArtist(Map<String, Object> data) {
        Artist artist = new Artist();
        artist.setName((String) data.get("name"));
        
        if (data.containsKey("bio") && data.get("bio") instanceof Map) {
            Map<String, Object> bio = (Map<String, Object>) data.get("bio");
            if (bio.containsKey("summary")) {
                artist.setBio((String) bio.get("summary"));
            }
        }
        
        if (data.containsKey("image") && data.get("image") instanceof List) {
            List<Map<String, Object>> images = (List<Map<String, Object>>) data.get("image");
            for (Map<String, Object> image : images) {
                if ("large".equals(image.get("size"))) {
                    artist.setImageUrl((String) image.get("#text"));
                    break;
                }
            }
        }
        
        return artist;
    }

    private Album mapToAlbum(Map<String, Object> data) {
        Album album = new Album();
        album.setTitle((String) data.get("name"));
        
        if (data.containsKey("image") && data.get("image") instanceof List) {
            List<Map<String, Object>> images = (List<Map<String, Object>>) data.get("image");
            for (Map<String, Object> image : images) {
                if ("large".equals(image.get("size"))) {
                    album.setCoverImageUrl((String) image.get("#text"));
                    break;
                }
            }
        }
        
        return album;
    }

    private Track mapToTrack(Map<String, Object> data) {
        Track track = new Track();
        track.setTitle((String) data.get("name"));
        
        if (data.containsKey("image") && data.get("image") instanceof List) {
            List<Map<String, Object>> images = (List<Map<String, Object>>) data.get("image");
            for (Map<String, Object> image : images) {
                if ("large".equals(image.get("size"))) {
                    track.setCoverImageUrl((String) image.get("#text"));
                    break;
                }
            }
        }
        
        return track;
    }
} 