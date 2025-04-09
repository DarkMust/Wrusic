package com.wrusic.wrusic.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.core.ParameterizedTypeReference;
import reactor.core.publisher.Mono;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

@Service
public class LastFmService {
    private final WebClient webClient;
    private final String apiKey;
    private final String baseUrl;
    private final Map<String, String> countryCodeMap;

    public LastFmService(
            @Value("${external.api.lastfm.api-key}") String apiKey,
            @Value("${external.api.lastfm.base-url}") String baseUrl,
            WebClient.Builder webClientBuilder) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.countryCodeMap = initializeCountryCodeMap();
    }

    private Map<String, String> initializeCountryCodeMap() {
        Map<String, String> map = new HashMap<>();
        map.put("united states", "united states");
        map.put("us", "united states");
        map.put("united kingdom", "united kingdom");
        map.put("uk", "united kingdom");
        map.put("gb", "united kingdom");
        map.put("france", "france");
        map.put("fr", "france");
        map.put("germany", "germany");
        map.put("de", "germany");
        map.put("japan", "japan");
        map.put("jp", "japan");
        map.put("canada", "canada");
        map.put("ca", "canada");
        map.put("australia", "australia");
        map.put("au", "australia");
        map.put("brazil", "brazil");
        map.put("br", "brazil");
        map.put("india", "india");
        map.put("in", "india");
        map.put("russia", "russia");
        map.put("ru", "russia");
        return map;
    }

    private String getCountryCode(String country) {
        if (country == null || country.trim().isEmpty()) {
            return "united states"; // Default country
        }
        String normalizedCountry = country.toLowerCase().trim();
        return countryCodeMap.getOrDefault(normalizedCountry, normalizedCountry);
    }

    /**
     * Get new releases
     */
    public Mono<Map<String, Object>> getNewReleases(String country, int limit) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("method", "album.search")
                        .queryParam("album", "new")
                        .queryParam("limit", limit)
                        .queryParam("api_key", apiKey)
                        .queryParam("format", "json")
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(response -> {
                    Map<String, Object> transformedResponse = new HashMap<>();
                    Map<String, Object> albums = new HashMap<>();
                    List<Map<String, Object>> albumList = new ArrayList<>();
                    
                    if (response.containsKey("results")) {
                        Map<String, Object> results = (Map<String, Object>) response.get("results");
                        if (results.containsKey("albummatches")) {
                            Map<String, Object> albumMatches = (Map<String, Object>) results.get("albummatches");
                            if (albumMatches.containsKey("album")) {
                                List<Map<String, Object>> rawAlbums = (List<Map<String, Object>>) albumMatches.get("album");
                                for (Map<String, Object> rawAlbum : rawAlbums) {
                                    Map<String, Object> transformedAlbum = new HashMap<>();
                                    transformedAlbum.put("id", rawAlbum.get("mbid"));
                                    transformedAlbum.put("name", rawAlbum.get("name"));
                                    
                                    // Handle images
                                    List<Map<String, Object>> images = new ArrayList<>();
                                    Map<String, Object> image = new HashMap<>();
                                    image.put("url", ((List<Map<String, Object>>) rawAlbum.get("image")).get(2).get("#text"));
                                    images.add(image);
                                    transformedAlbum.put("images", images);
                                    
                                    // Handle artists
                                    List<Map<String, Object>> artists = new ArrayList<>();
                                    Map<String, Object> artist = new HashMap<>();
                                    artist.put("name", rawAlbum.get("artist"));
                                    artists.add(artist);
                                    transformedAlbum.put("artists", artists);
                                    
                                    transformedAlbum.put("release_date", "Recent");
                                    albumList.add(transformedAlbum);
                                }
                            }
                        }
                    }
                    
                    albums.put("album", albumList);
                    transformedResponse.put("albums", albums);
                    return transformedResponse;
                })
                .onErrorResume(e -> {
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Failed to fetch new releases");
                    errorResponse.put("message", e.getMessage());
                    return Mono.just(errorResponse);
                });
    }

    /**
     * Get top tracks (charts) for a country
     */
    public Mono<Map<String, Object>> getTopTracks(String country, int limit) {
        String countryCode = getCountryCode(country);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("method", "geo.getTopTracks")
                        .queryParam("country", countryCode)
                        .queryParam("limit", limit)
                        .queryParam("api_key", apiKey)
                        .queryParam("format", "json")
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(response -> {
                    Map<String, Object> transformedResponse = new HashMap<>();
                    Map<String, Object> toptracks = new HashMap<>();
                    List<Map<String, Object>> trackList = new ArrayList<>();
                    
                    if (response.containsKey("tracks")) {
                        Map<String, Object> tracksData = (Map<String, Object>) response.get("tracks");
                        if (tracksData.containsKey("track")) {
                            List<Map<String, Object>> rawTracks = (List<Map<String, Object>>) tracksData.get("track");
                            for (Map<String, Object> rawTrack : rawTracks) {
                                Map<String, Object> transformedTrack = new HashMap<>();
                                transformedTrack.put("id", rawTrack.get("mbid"));
                                transformedTrack.put("name", rawTrack.get("name"));
                                
                                // Handle artists
                                List<Map<String, Object>> artists = new ArrayList<>();
                                Map<String, Object> artist = new HashMap<>();
                                artist.put("name", ((Map<String, Object>) rawTrack.get("artist")).get("name"));
                                artists.add(artist);
                                transformedTrack.put("artists", artists);
                                
                                // Handle album
                                Map<String, Object> album = new HashMap<>();
                                album.put("name", "Unknown Album");
                                List<Map<String, Object>> images = new ArrayList<>();
                                Map<String, Object> image = new HashMap<>();
                                image.put("url", ((List<Map<String, Object>>) rawTrack.get("image")).get(2).get("#text"));
                                images.add(image);
                                album.put("images", images);
                                transformedTrack.put("album", album);
                                
                                trackList.add(transformedTrack);
                            }
                        }
                    }
                    
                    toptracks.put("track", trackList);
                    transformedResponse.put("toptracks", toptracks);
                    return transformedResponse;
                })
                .onErrorResume(e -> {
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Failed to fetch top tracks");
                    errorResponse.put("message", e.getMessage());
                    return Mono.just(errorResponse);
                });
    }
    
    /**
     * Get top albums for a country
     */
    public Mono<Map<String, Object>> getTopAlbums(String country, int limit) {
        String countryCode = getCountryCode(country);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("method", "geo.getTopTracks")
                        .queryParam("country", countryCode)
                        .queryParam("limit", limit)
                        .queryParam("api_key", apiKey)
                        .queryParam("format", "json")
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(response -> {
                    Map<String, Object> transformedResponse = new HashMap<>();
                    Map<String, Object> topalbums = new HashMap<>();
                    List<Map<String, Object>> albumList = new ArrayList<>();
                    
                    if (response.containsKey("tracks")) {
                        Map<String, Object> tracksData = (Map<String, Object>) response.get("tracks");
                        if (tracksData.containsKey("track")) {
                            List<Map<String, Object>> rawTracks = (List<Map<String, Object>>) tracksData.get("track");
                            for (Map<String, Object> rawTrack : rawTracks) {
                                Map<String, Object> transformedAlbum = new HashMap<>();
                                transformedAlbum.put("id", rawTrack.getOrDefault("mbid", ""));
                                
                                // Handle album name
                                Map<String, Object> album = (Map<String, Object>) rawTrack.getOrDefault("album", new HashMap<>());
                                transformedAlbum.put("name", album.getOrDefault("title", "Unknown Album"));
                                
                                // Handle images
                                List<Map<String, Object>> images = new ArrayList<>();
                                Map<String, Object> image = new HashMap<>();
                                List<Map<String, Object>> rawImages = (List<Map<String, Object>>) rawTrack.getOrDefault("image", new ArrayList<>());
                                String imageUrl = rawImages.size() > 2 ? 
                                    (String) rawImages.get(2).getOrDefault("#text", "assets/images/default-album.jpg") :
                                    "assets/images/default-album.jpg";
                                image.put("url", imageUrl);
                                images.add(image);
                                transformedAlbum.put("images", images);
                                
                                // Handle artists
                                List<Map<String, Object>> artists = new ArrayList<>();
                                Map<String, Object> artist = new HashMap<>();
                                Map<String, Object> rawArtist = (Map<String, Object>) rawTrack.getOrDefault("artist", new HashMap<>());
                                artist.put("name", rawArtist.getOrDefault("name", "Unknown Artist"));
                                artists.add(artist);
                                transformedAlbum.put("artists", artists);
                                
                                albumList.add(transformedAlbum);
                            }
                        }
                    }
                    
                    topalbums.put("album", albumList);
                    transformedResponse.put("topalbums", topalbums);
                    return transformedResponse;
                })
                .onErrorResume(e -> {
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Failed to fetch top albums");
                    errorResponse.put("message", e.getMessage());
                    return Mono.just(errorResponse);
                });
    }
    
    /**
     * Get top artists for a country
     */
    public Mono<Map<String, Object>> getTopArtists(String country, int limit) {
        String countryCode = getCountryCode(country);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("method", "geo.getTopArtists")
                        .queryParam("country", countryCode)
                        .queryParam("limit", limit)
                        .queryParam("api_key", apiKey)
                        .queryParam("format", "json")
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(response -> {
                    Map<String, Object> transformedResponse = new HashMap<>();
                    Map<String, Object> topartists = new HashMap<>();
                    List<Map<String, Object>> artistList = new ArrayList<>();
                    
                    if (response.containsKey("topartists")) {
                        Map<String, Object> artistsData = (Map<String, Object>) response.get("topartists");
                        if (artistsData.containsKey("artist")) {
                            List<Map<String, Object>> rawArtists = (List<Map<String, Object>>) artistsData.get("artist");
                            for (Map<String, Object> rawArtist : rawArtists) {
                                Map<String, Object> transformedArtist = new HashMap<>();
                                transformedArtist.put("id", rawArtist.get("mbid"));
                                transformedArtist.put("name", rawArtist.get("name"));
                                
                                // Handle images
                                List<Map<String, Object>> images = new ArrayList<>();
                                Map<String, Object> image = new HashMap<>();
                                image.put("url", ((List<Map<String, Object>>) rawArtist.get("image")).get(2).get("#text"));
                                images.add(image);
                                transformedArtist.put("images", images);
                                
                                // Handle listeners
                                transformedArtist.put("listeners", rawArtist.get("listeners"));
                                
                                artistList.add(transformedArtist);
                            }
                        }
                    }
                    
                    topartists.put("artist", artistList);
                    transformedResponse.put("topartists", topartists);
                    return transformedResponse;
                })
                .onErrorResume(e -> {
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Failed to fetch top artists");
                    errorResponse.put("message", e.getMessage());
                    return Mono.just(errorResponse);
                });
    }
} 