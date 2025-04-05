package com.wrusic.wrusic.controller;

import com.wrusic.wrusic.service.SpotifyTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/spotify")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
public class SpotifyAuthController {

    private final SpotifyTokenService spotifyTokenService;

    @Value("${external.api.spotify.client-id}")
    private String clientId;

    @Value("${external.api.spotify.redirect-uri}")
    private String redirectUri;

    @Value("${external.api.spotify.authorization-uri:https://accounts.spotify.com/authorize}")
    private String authorizationUri;

    /**
     * Get the Spotify authorization URL
     */
    @GetMapping("/authorize")
    public ResponseEntity<Map<String, String>> getAuthorizationUrl() {
        String state = java.util.UUID.randomUUID().toString();
        
        String scope = "user-read-private user-read-email playlist-read-private playlist-read-collaborative " +
                "playlist-modify-public playlist-modify-private user-library-read user-library-modify " +
                "user-top-read user-read-recently-played user-read-playback-state user-modify-playback-state " +
                "user-read-currently-playing";
        
        String authUrl = String.format("%s?client_id=%s&response_type=code&redirect_uri=%s&scope=%s&state=%s",
                authorizationUri, clientId, redirectUri, scope, state);
        
        Map<String, String> response = new HashMap<>();
        response.put("authorizationUrl", authUrl);
        response.put("state", state);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Handle the Spotify OAuth2 callback
     */
    @GetMapping("/callback")
    public RedirectView handleCallback(@RequestParam("code") String code,
                                      @RequestParam("state") String state) {
        log.info("Received callback with code: {} and state: {}", code, state);
        
        // Exchange the authorization code for an access token
        spotifyTokenService.exchangeCodeForToken(code, redirectUri);
        
        // Redirect to the frontend application
        return new RedirectView("http://localhost:4200/auth/callback?code=" + code + "&state=" + state);
    }

    /**
     * Get the current user's Spotify profile
     */
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(
            @AuthenticationPrincipal OAuth2User principal,
            @RegisteredOAuth2AuthorizedClient("spotify") OAuth2AuthorizedClient authorizedClient) {
        
        if (principal == null || authorizedClient == null) {
            return ResponseEntity.badRequest().build();
        }
        
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", principal.getAttribute("id"));
        profile.put("displayName", principal.getAttribute("display_name"));
        profile.put("email", principal.getAttribute("email"));
        profile.put("imageUrl", principal.getAttribute("images"));
        
        return ResponseEntity.ok(profile);
    }

    /**
     * Get the current user's Spotify access token
     */
    @GetMapping("/token")
    public ResponseEntity<Map<String, String>> getToken(@AuthenticationPrincipal OAuth2AuthenticationToken authentication) {
        if (authentication == null) {
            return ResponseEntity.badRequest().build();
        }
        
        String accessToken = spotifyTokenService.getAccessToken(authentication);
        
        if (accessToken == null) {
            return ResponseEntity.badRequest().build();
        }
        
        Map<String, String> response = new HashMap<>();
        response.put("accessToken", accessToken);
        
        return ResponseEntity.ok(response);
    }
} 