package com.wrusic.wrusic.model.spotify;

import lombok.Data;

import java.util.List;

@Data
public class SpotifyAlbumResponse {
    private String id;
    private String name;
    private String releaseDate;
    private List<Image> images;
    private List<SpotifyArtistResponse> artists;
    private ExternalUrls externalUrls;

    @Data
    public static class Image {
        private String url;
        private int height;
        private int width;
    }

    @Data
    public static class ExternalUrls {
        private String spotify;
    }
} 