package com.wrusic.wrusic.service;

import com.wrusic.wrusic.model.Album;
import com.wrusic.wrusic.model.Artist;
import com.wrusic.wrusic.model.Track;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MusicDataService {

    private final SpotifyService spotifyService;
    private final LastFmService lastFmService;
    private final MusicBrainzService musicBrainzService;

    public List<Artist> searchArtists(String query) {
        List<Artist> artists = new ArrayList<>();
        
        // Search on Spotify
        List<Artist> spotifyArtists = spotifyService.searchArtists(query);
        artists.addAll(spotifyArtists);
        
        // Search on Last.fm
        List<Artist> lastFmArtists = lastFmService.searchArtists(query);
        artists.addAll(lastFmArtists);
        
        // Search on MusicBrainz
        List<Artist> musicBrainzArtists = musicBrainzService.searchArtists(query);
        artists.addAll(musicBrainzArtists);
        
        // Remove duplicates based on artist name
        return artists.stream()
                .collect(Collectors.collectingAndThen(
                    Collectors.toMap(
                        Artist::getName,
                        artist -> artist,
                        (existing, replacement) -> {
                            // Merge artist data, preferring non-null values
                            if (existing.getBio() == null && replacement.getBio() != null) {
                                existing.setBio(replacement.getBio());
                            }
                            if (existing.getImageUrl() == null && replacement.getImageUrl() != null) {
                                existing.setImageUrl(replacement.getImageUrl());
                            }
                            return existing;
                        }
                    ),
                    map -> new ArrayList<>(map.values())
                ));
    }

    public Artist getArtistDetails(String spotifyId, String lastFmName, String musicBrainzId) {
        Artist artist = new Artist();
        
        // Get data from Spotify
        if (spotifyId != null) {
            Artist spotifyArtist = spotifyService.getArtistById(spotifyId);
            if (spotifyArtist != null) {
                artist.setName(spotifyArtist.getName());
                artist.setBio(spotifyArtist.getBio());
                artist.setImageUrl(spotifyArtist.getImageUrl());
            }
        }
        
        // Get data from Last.fm
        if (lastFmName != null) {
            Artist lastFmArtist = lastFmService.getArtistInfo(lastFmName);
            if (lastFmArtist != null) {
                if (artist.getName() == null) artist.setName(lastFmArtist.getName());
                if (artist.getBio() == null) artist.setBio(lastFmArtist.getBio());
                if (artist.getImageUrl() == null) artist.setImageUrl(lastFmArtist.getImageUrl());
            }
        }
        
        // Get data from MusicBrainz
        if (musicBrainzId != null) {
            Artist musicBrainzArtist = musicBrainzService.getArtistById(musicBrainzId);
            if (musicBrainzArtist != null) {
                if (artist.getName() == null) artist.setName(musicBrainzArtist.getName());
                if (artist.getBio() == null) artist.setBio(musicBrainzArtist.getBio());
                if (artist.getImageUrl() == null) artist.setImageUrl(musicBrainzArtist.getImageUrl());
            }
        }
        
        return artist;
    }

    public List<Album> getArtistAlbums(String spotifyId, String lastFmName, String musicBrainzId) {
        List<Album> albums = new ArrayList<>();
        
        // Get albums from Spotify
        if (spotifyId != null) {
            List<Album> spotifyAlbums = spotifyService.getAlbumsByArtist(spotifyId);
            albums.addAll(spotifyAlbums);
        }
        
        // Get albums from Last.fm
        if (lastFmName != null) {
            List<Album> lastFmAlbums = lastFmService.getTopAlbumsByArtist(lastFmName);
            albums.addAll(lastFmAlbums);
        }
        
        // Get albums from MusicBrainz
        if (musicBrainzId != null) {
            List<Album> musicBrainzAlbums = musicBrainzService.getReleasesByArtist(musicBrainzId);
            albums.addAll(musicBrainzAlbums);
        }
        
        // Remove duplicates and sort by release date
        return albums.stream()
                .collect(Collectors.collectingAndThen(
                    Collectors.toMap(
                        Album::getTitle,
                        album -> album,
                        (existing, replacement) -> {
                            // Merge album data, preferring non-null values
                            if (existing.getReleaseDate() == null && replacement.getReleaseDate() != null) {
                                existing.setReleaseDate(replacement.getReleaseDate());
                            }
                            if (existing.getCoverImageUrl() == null && replacement.getCoverImageUrl() != null) {
                                existing.setCoverImageUrl(replacement.getCoverImageUrl());
                            }
                            return existing;
                        }
                    ),
                    map -> new ArrayList<>(map.values())
                ))
                .stream()
                .sorted(Comparator.comparing(Album::getReleaseDate, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    public List<Track> getArtistTracks(String spotifyId, String lastFmName, String musicBrainzId) {
        List<Track> tracks = new ArrayList<>();
        
        // Get tracks from Spotify
        if (spotifyId != null) {
            List<Track> spotifyTracks = spotifyService.getTopTracksByArtist(spotifyId);
            tracks.addAll(spotifyTracks);
        }
        
        // Get tracks from Last.fm
        if (lastFmName != null) {
            List<Track> lastFmTracks = lastFmService.getTopTracksByArtist(lastFmName);
            tracks.addAll(lastFmTracks);
        }
        
        // Get tracks from MusicBrainz (if we have album IDs)
        if (musicBrainzId != null) {
            List<Album> albums = musicBrainzService.getReleasesByArtist(musicBrainzId);
            for (Album album : albums) {
                if (album.getMusicBrainzId() != null) {
                    List<Track> albumTracks = musicBrainzService.getRecordingsByRelease(album.getMusicBrainzId());
                    tracks.addAll(albumTracks);
                }
            }
        }
        
        // Remove duplicates based on track title
        return tracks.stream()
                .collect(Collectors.collectingAndThen(
                    Collectors.toMap(
                        Track::getTitle,
                        track -> track,
                        (existing, replacement) -> {
                            // Merge track data, preferring non-null values
                            if (existing.getDuration() == null && replacement.getDuration() != null) {
                                existing.setDuration(replacement.getDuration());
                            }
                            if (existing.getCoverImageUrl() == null && replacement.getCoverImageUrl() != null) {
                                existing.setCoverImageUrl(replacement.getCoverImageUrl());
                            }
                            return existing;
                        }
                    ),
                    map -> new ArrayList<>(map.values())
                ));
    }

    public List<Track> getTopTracksByCountry(String country) {
        List<Track> tracks = new ArrayList<>();
        
        // Get tracks from Last.fm (MusicBrainz doesn't have country-specific charts)
        List<Track> lastFmTracks = lastFmService.getTopTracksByCountry(country);
        tracks.addAll(lastFmTracks);
        
        // Remove duplicates based on track title
        return tracks.stream()
                .collect(Collectors.collectingAndThen(
                    Collectors.toMap(
                        Track::getTitle,
                        track -> track,
                        (existing, replacement) -> {
                            // Merge track data, preferring non-null values
                            if (existing.getDuration() == null && replacement.getDuration() != null) {
                                existing.setDuration(replacement.getDuration());
                            }
                            if (existing.getCoverImageUrl() == null && replacement.getCoverImageUrl() != null) {
                                existing.setCoverImageUrl(replacement.getCoverImageUrl());
                            }
                            return existing;
                        }
                    ),
                    map -> new ArrayList<>(map.values())
                ));
    }
} 