package com.wrusic.wrusic.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class SpotifyOAuth2Config {

    @Value("${external.api.spotify.client-id}")
    private String clientId;

    @Value("${external.api.spotify.client-secret}")
    private String clientSecret;

    @Value("${external.api.spotify.redirect-uri}")
    private String redirectUri;

    @Value("${external.api.spotify.token-uri:https://accounts.spotify.com/api/token}")
    private String tokenUri;

    @Value("${external.api.spotify.authorization-uri:https://accounts.spotify.com/authorize}")
    private String authorizationUri;

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(spotifyClientRegistration());
    }

    private ClientRegistration spotifyClientRegistration() {
        return ClientRegistration.withRegistrationId("spotify")
                .clientId(clientId)
                .clientSecret(clientSecret)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri(redirectUri)
                .scope("user-read-private", "user-read-email", "playlist-read-private", "playlist-read-collaborative", "playlist-modify-public", "playlist-modify-private", "user-library-read", "user-library-modify", "user-top-read", "user-read-recently-played", "user-read-playback-state", "user-modify-playback-state", "user-read-currently-playing")
                .authorizationUri(authorizationUri)
                .tokenUri(tokenUri)
                .userInfoUri("https://api.spotify.com/v1/me")
                .userNameAttributeName("id")
                .clientName("Spotify")
                .build();
    }

    @Bean
    public OAuth2AuthorizedClientProvider authorizedClientProvider() {
        return OAuth2AuthorizedClientProviderBuilder.builder()
                .authorizationCode()
                .refreshToken()
                .build();
    }
} 