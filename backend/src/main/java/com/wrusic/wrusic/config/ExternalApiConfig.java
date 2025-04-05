package com.wrusic.wrusic.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ExternalApiConfig {

    @Value("${external.api.spotify.base-url:https://api.spotify.com/v1}")
    private String spotifyBaseUrl;

    @Value("${external.api.spotify.client-id}")
    private String spotifyClientId;

    @Value("${external.api.spotify.client-secret}")
    private String spotifyClientSecret;

    @Value("${external.api.lastfm.base-url:http://ws.audioscrobbler.com/2.0/}")
    private String lastfmBaseUrl;

    @Value("${external.api.lastfm.api-key}")
    private String lastfmApiKey;

    @Value("${external.api.musicbrainz.base-url:https://musicbrainz.org/ws/2/}")
    private String musicbrainzBaseUrl;

    @Bean
    public WebClient spotifyWebClient() {
        return WebClient.builder()
                .baseUrl(spotifyBaseUrl)
                .defaultHeader("Authorization", "Bearer " + getSpotifyAccessToken())
                .build();
    }

    @Bean
    public WebClient lastfmWebClient() {
        return WebClient.builder()
                .baseUrl(lastfmBaseUrl)
                .defaultHeader("User-Agent", "Wrusic/1.0")
                .build();
    }

    @Bean
    public WebClient musicbrainzWebClient() {
        return WebClient.builder()
                .baseUrl(musicbrainzBaseUrl)
                .defaultHeader("User-Agent", "Wrusic/1.0")
                .build();
    }

    private String getSpotifyAccessToken() {
        // This is a placeholder. In a real application, you would implement OAuth2 flow
        // or use a token management service to get and refresh tokens
        return "YOUR_SPOTIFY_ACCESS_TOKEN";
    }
} 