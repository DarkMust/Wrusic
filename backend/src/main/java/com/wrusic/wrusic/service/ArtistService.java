package com.wrusic.wrusic.service;

import com.wrusic.wrusic.model.Artist;

import java.util.List;
import java.util.Optional;

public interface ArtistService {
    
    List<Artist> findAllArtists();
    
    Optional<Artist> findArtistById(Long id);
    
    Artist saveArtist(Artist artist);
    
    Artist updateArtist(Long id, Artist artist);
    
    void deleteArtist(Long id);
    
    List<Artist> searchArtistsByName(String name);
    
    boolean existsByName(String name);
} 