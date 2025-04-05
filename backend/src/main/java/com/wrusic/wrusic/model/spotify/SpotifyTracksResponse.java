package com.wrusic.wrusic.model.spotify;

import lombok.Data;

import java.util.List;

@Data
public class SpotifyTracksResponse {
    private List<SpotifyTrackResponse> items;
    private int total;
    private int limit;
    private int offset;
    private String next;
    private String previous;
} 