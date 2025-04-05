package com.wrusic.wrusic.model.spotify;

import lombok.Data;

@Data
public class SpotifySearchResponse {
    private Artists artists;

    @Data
    public static class Artists {
        private PagingObject<SpotifyArtistResponse> items;
    }

    @Data
    public static class PagingObject<T> {
        private java.util.List<T> items;
        private int total;
        private int limit;
        private int offset;
        private String next;
        private String previous;
    }
} 