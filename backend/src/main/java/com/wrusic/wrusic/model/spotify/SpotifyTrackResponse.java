package com.wrusic.wrusic.model.spotify;

import lombok.Data;

import java.util.List;

@Data
public class SpotifyTrackResponse {
    private String id;
    private String name;
    private int durationMs;
    private String previewUrl;
    private List<SpotifyArtistResponse> artists;
    private SpotifyAlbumResponse album;
    private ExternalUrls externalUrls;

    @Data
    public static class ExternalUrls {
        private String spotify;
    }
} 