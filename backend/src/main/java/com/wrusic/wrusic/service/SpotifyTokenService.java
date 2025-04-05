package com.wrusic.wrusic.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpotifyTokenService {

    private final OAuth2AuthorizedClientService clientService;
    private final WebClient webClient;

    @Value("${external.api.spotify.client-id}")
    private String clientId;

    @Value("${external.api.spotify.client-secret}")
    private String clientSecret;

    @Value("${external.api.spotify.token-uri:https://accounts.spotify.com/api/token}")
    private String tokenUri;

    /**
     * Get the access token for the authenticated user
     */
    public String getAccessToken(OAuth2AuthenticationToken authentication) {
        OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName());

        if (client == null) {
            log.error("No authorized client found for user: {}", authentication.getName());
            return null;
        }

        OAuth2AccessToken accessToken = client.getAccessToken();
        
        // Check if token is expired or about to expire (within 5 minutes)
        if (accessToken.getExpiresAt().isBefore(Instant.now().plusSeconds(300))) {
            log.info("Access token is expired or about to expire, refreshing...");
            client = refreshToken(client);
            accessToken = client.getAccessToken();
        }

        return accessToken.getTokenValue();
    }

    /**
     * Refresh the access token using the refresh token
     */
    private OAuth2AuthorizedClient refreshToken(OAuth2AuthorizedClient client) {
        OAuth2RefreshToken refreshToken = client.getRefreshToken();
        
        if (refreshToken == null) {
            log.error("No refresh token available for user: {}", client.getPrincipalName());
            return null;
        }

        String authHeader = "Basic " + Base64.getEncoder().encodeToString(
                (clientId + ":" + clientSecret).getBytes());

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "refresh_token");
        formData.add("refresh_token", refreshToken.getTokenValue());

        try {
            var response = webClient.post()
                    .uri(tokenUri)
                    .header("Authorization", authHeader)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(SpotifyTokenResponse.class)
                    .block();

            if (response != null) {
                // Create a new OAuth2AuthorizedClient with the refreshed tokens
                OAuth2AccessToken newAccessToken = new OAuth2AccessToken(
                        OAuth2AccessToken.TokenType.BEARER,
                        response.getAccessToken(),
                        Instant.now(),
                        Instant.now().plusSeconds(response.getExpiresIn()));

                OAuth2RefreshToken newRefreshToken = response.getRefreshToken() != null ?
                        new OAuth2RefreshToken(
                                "refresh_token",
                                response.getRefreshToken(),
                                Instant.now(),
                                null) :
                        refreshToken;

                return new OAuth2AuthorizedClient(
                        client.getClientRegistration(),
                        client.getPrincipalName(),
                        newAccessToken,
                        newRefreshToken);
            }
        } catch (Exception e) {
            log.error("Error refreshing token: {}", e.getMessage());
        }

        return client;
    }

    /**
     * Exchange authorization code for access token
     */
    public OAuth2AuthorizedClient exchangeCodeForToken(String code, String redirectUri) {
        String authHeader = "Basic " + Base64.getEncoder().encodeToString(
                (clientId + ":" + clientSecret).getBytes());

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("code", code);
        formData.add("redirect_uri", redirectUri);

        try {
            var response = webClient.post()
                    .uri(tokenUri)
                    .header("Authorization", authHeader)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(SpotifyTokenResponse.class)
                    .block();

            if (response != null) {
                // This is a simplified version - in a real application, you would need to
                // create a proper OAuth2AuthorizedClient with the user's principal
                log.info("Successfully exchanged code for token");
                return null; // This would need to be properly implemented
            }
        } catch (Exception e) {
            log.error("Error exchanging code for token: {}", e.getMessage());
        }

        return null;
    }
} 