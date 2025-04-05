package com.wrusic.wrusic.service.impl;

import com.wrusic.wrusic.model.Artist;
import com.wrusic.wrusic.repository.ArtistRepository;
import com.wrusic.wrusic.service.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ArtistServiceImpl implements ArtistService {
    
    private final ArtistRepository artistRepository;
    
    @Autowired
    public ArtistServiceImpl(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }
    
    @Override
    public List<Artist> findAllArtists() {
        return artistRepository.findAll();
    }
    
    @Override
    public Optional<Artist> findArtistById(Long id) {
        return artistRepository.findById(id);
    }
    
    @Override
    public Artist saveArtist(Artist artist) {
        return artistRepository.save(artist);
    }
    
    @Override
    public Artist updateArtist(Long id, Artist artist) {
        if (!artistRepository.existsById(id)) {
            throw new IllegalArgumentException("Artist with ID " + id + " not found");
        }
        
        artist.setId(id);
        return artistRepository.save(artist);
    }
    
    @Override
    public void deleteArtist(Long id) {
        if (!artistRepository.existsById(id)) {
            throw new IllegalArgumentException("Artist with ID " + id + " not found");
        }
        
        artistRepository.deleteById(id);
    }
    
    @Override
    public List<Artist> searchArtistsByName(String name) {
        return artistRepository.findByNameContainingIgnoreCase(name);
    }
    
    @Override
    public boolean existsByName(String name) {
        return artistRepository.existsByNameIgnoreCase(name);
    }
} 