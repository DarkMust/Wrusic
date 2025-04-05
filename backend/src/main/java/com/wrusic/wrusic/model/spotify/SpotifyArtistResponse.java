package com.wrusic.wrusic.model.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SpotifyArtistResponse {
    private String id;
    private String name;
    private List<Image> images;
    private List<String> genres;
    private int popularity;
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