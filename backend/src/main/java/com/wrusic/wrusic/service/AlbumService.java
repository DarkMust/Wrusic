package com.wrusic.wrusic.service;

import com.wrusic.wrusic.model.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AlbumService {
    
    List<Album> findAllAlbums();
    
    Optional<Album> findAlbumById(Long id);
    
    Album saveAlbum(Album album);
    
    Album updateAlbum(Long id, Album album);
    
    void deleteAlbum(Long id);
    
    List<Album> findAlbumsByArtistId(Long artistId);
    
    List<Album> findAlbumsByGenre(String genre);
    
    List<Album> searchAlbumsByTitle(String title);
    
    Page<Album> findLatestReleases(LocalDate startDate, LocalDate endDate, Pageable pageable);
} 