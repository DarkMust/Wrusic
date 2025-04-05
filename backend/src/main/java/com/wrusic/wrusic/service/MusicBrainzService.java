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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MusicBrainzService {

    private final WebClient musicBrainzWebClient;

    @Value("${external.api.musicbrainz.user-agent}")
    private String userAgent;

    public List<Artist> searchArtists(String query) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl("https://musicbrainz.org/ws/2/artist")
                    .queryParam("query", query)
                    .queryParam("fmt", "json")
                    .build()
                    .toUriString();

            Map<String, Object> response = musicBrainzWebClient.get()
                    .uri(url)
                    .header("User-Agent", userAgent)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.containsKey("artists")) {
                List<Map<String, Object>> artists = (List<Map<String, Object>>) response.get("artists");
                return artists.stream()
                        .map(this::mapToArtist)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("Error searching artists on MusicBrainz: {}", e.getMessage());
        }
        
        return new ArrayList<>();
    }

    public Artist getArtistById(String mbid) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl("https://musicbrainz.org/ws/2/artist/" + mbid)
                    .queryParam("fmt", "json")
                    .queryParam("inc", "url-rels+releases")
                    .build()
                    .toUriString();

            Map<String, Object> response = musicBrainzWebClient.get()
                    .uri(url)
                    .header("User-Agent", userAgent)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null) {
                return mapToArtist(response);
            }
        } catch (Exception e) {
            log.error("Error getting artist from MusicBrainz: {}", e.getMessage());
        }
        
        return null;
    }

    public List<Album> getReleasesByArtist(String mbid) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl("https://musicbrainz.org/ws/2/release")
                    .queryParam("artist", mbid)
                    .queryParam("fmt", "json")
                    .build()
                    .toUriString();

            Map<String, Object> response = musicBrainzWebClient.get()
                    .uri(url)
                    .header("User-Agent", userAgent)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.containsKey("releases")) {
                List<Map<String, Object>> releases = (List<Map<String, Object>>) response.get("releases");
                return releases.stream()
                        .map(this::mapToAlbum)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("Error getting releases from MusicBrainz: {}", e.getMessage());
        }
        
        return new ArrayList<>();
    }

    public List<Track> getRecordingsByRelease(String mbid) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl("https://musicbrainz.org/ws/2/recording")
                    .queryParam("release", mbid)
                    .queryParam("fmt", "json")
                    .build()
                    .toUriString();

            Map<String, Object> response = musicBrainzWebClient.get()
                    .uri(url)
                    .header("User-Agent", userAgent)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.containsKey("recordings")) {
                List<Map<String, Object>> recordings = (List<Map<String, Object>>) response.get("recordings");
                return recordings.stream()
                        .map(this::mapToTrack)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("Error getting recordings from MusicBrainz: {}", e.getMessage());
        }
        
        return new ArrayList<>();
    }

    private Artist mapToArtist(Map<String, Object> data) {
        Artist artist = new Artist();
        artist.setName((String) data.get("name"));
        
        if (data.containsKey("disambiguation")) {
            artist.setBio((String) data.get("disambiguation"));
        }
        
        if (data.containsKey("url-relation-list")) {
            List<Map<String, Object>> relations = (List<Map<String, Object>>) data.get("url-relation-list");
            for (Map<String, Object> relation : relations) {
                if ("image".equals(relation.get("type"))) {
                    Map<String, Object> url = (Map<String, Object>) relation.get("url");
                    artist.setImageUrl((String) url.get("resource"));
                    break;
                }
            }
        }
        
        return artist;
    }

    private Album mapToAlbum(Map<String, Object> data) {
        Album album = new Album();
        album.setTitle((String) data.get("title"));
        
        if (data.containsKey("date")) {
            album.setReleaseDate(java.time.LocalDate.parse((String) data.get("date")));
        }
        
        if (data.containsKey("cover-art-archive")) {
            Map<String, Object> coverArt = (Map<String, Object>) data.get("cover-art-archive");
            if ((Boolean) coverArt.get("front")) {
                album.setCoverImageUrl("https://coverartarchive.org/release/" + data.get("id") + "/front");
            }
        }
        
        return album;
    }

    private Track mapToTrack(Map<String, Object> data) {
        Track track = new Track();
        track.setTitle((String) data.get("title"));
        
        if (data.containsKey("length")) {
            track.setDuration((Integer) data.get("length") / 1000); // Convert milliseconds to seconds
        }
        
        return track;
    }
} 