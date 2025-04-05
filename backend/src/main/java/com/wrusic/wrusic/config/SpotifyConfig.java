package com.wrusic.wrusic.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class SpotifyConfig {

    @Value("${external.api.spotify.client-id}")
    private String clientId;

    @Value("${external.api.spotify.client-secret}")
    private String clientSecret;

    @Value("${external.api.spotify.api-uri:https://api.spotify.com/v1}")
    private String apiUri;

    @Bean
    public WebClient spotifyWebClient() {
        return WebClient.builder()
                .baseUrl(apiUri)
                .build();
    }
} 