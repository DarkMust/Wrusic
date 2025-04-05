package com.wrusic.wrusic.service;

import com.wrusic.wrusic.model.Artist;
import com.wrusic.wrusic.model.Album;
import com.wrusic.wrusic.model.Track;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class SpotifyApiService {

    private final SpotifyTokenService spotifyTokenService;
    private final WebClient webClient;

    @Value("${external.api.spotify.api-uri:https://api.spotify.com/v1}")
    private String apiUri;

    /**
     * Search for artists
     */
    public Mono<List<Artist>> searchArtists(OAuth2AuthenticationToken authentication, String query) {
        String accessToken = spotifyTokenService.getAccessToken(authentication);
        if (accessToken == null) {
            return Mono.error(new RuntimeException("No access token available"));
        }

        return webClient.get()
                .uri(apiUri + "/search?q=" + query + "&type=artist")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(SpotifySearchResponse.class)
                .map(response -> response.getArtists().getItems().stream()
                        .map(this::mapToArtist)
                        .toList());
    }

    /**
     * Get artist details
     */
    public Mono<Artist> getArtist(OAuth2AuthenticationToken authentication, String artistId) {
        String accessToken = spotifyTokenService.getAccessToken(authentication);
        if (accessToken == null) {
            return Mono.error(new RuntimeException("No access token available"));
        }

        return webClient.get()
                .uri(apiUri + "/artists/" + artistId)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(SpotifyArtistResponse.class)
                .map(this::mapToArtist);
    }

    /**
     * Get artist's albums
     */
    public Mono<List<Album>> getArtistAlbums(OAuth2AuthenticationToken authentication, String artistId) {
        String accessToken = spotifyTokenService.getAccessToken(authentication);
        if (accessToken == null) {
            return Mono.error(new RuntimeException("No access token available"));
        }

        return webClient.get()
                .uri(apiUri + "/artists/" + artistId + "/albums")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(SpotifyAlbumsResponse.class)
                .map(response -> response.getItems().stream()
                        .map(this::mapToAlbum)
                        .toList());
    }

    /**
     * Get album tracks
     */
    public Mono<List<Track>> getAlbumTracks(OAuth2AuthenticationToken authentication, String albumId) {
        String accessToken = spotifyTokenService.getAccessToken(authentication);
        if (accessToken == null) {
            return Mono.error(new RuntimeException("No access token available"));
        }

        return webClient.get()
                .uri(apiUri + "/albums/" + albumId + "/tracks")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(SpotifyTracksResponse.class)
                .map(response -> response.getItems().stream()
                        .map(this::mapToTrack)
                        .toList());
    }

    /**
     * Get artist's top tracks
     */
    public Mono<List<Track>> getArtistTopTracks(OAuth2AuthenticationToken authentication, String artistId) {
        String accessToken = spotifyTokenService.getAccessToken(authentication);
        if (accessToken == null) {
            return Mono.error(new RuntimeException("No access token available"));
        }

        return webClient.get()
                .uri(apiUri + "/artists/" + artistId + "/top-tracks?market=US")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(SpotifyTracksResponse.class)
                .map(response -> response.getItems().stream()
                        .map(this::mapToTrack)
                        .toList());
    }

    private Artist mapToArtist(SpotifyArtistResponse response) {
        Artist artist = new Artist();
        artist.setName(response.getName());
        artist.setSpotifyId(response.getId());
        if (response.getImages() != null && !response.getImages().isEmpty()) {
            artist.setImageUrl(response.getImages().get(0).getUrl());
        }
        return artist;
    }

    private Album mapToAlbum(SpotifyAlbumResponse response) {
        Album album = new Album();
        album.setTitle(response.getName());
        album.setSpotifyId(response.getId());
        album.setReleaseDate(response.getReleaseDate());
        if (response.getImages() != null && !response.getImages().isEmpty()) {
            album.setCoverUrl(response.getImages().get(0).getUrl());
        }
        return album;
    }

    private Track mapToTrack(SpotifyTrackResponse response) {
        Track track = new Track();
        track.setTitle(response.getName());
        track.setSpotifyId(response.getId());
        track.setDuration(response.getDurationMs());
        track.setPreviewUrl(response.getPreviewUrl());
        return track;
    }
} 