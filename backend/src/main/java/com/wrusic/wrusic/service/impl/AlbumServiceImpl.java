package com.wrusic.wrusic.service.impl;

import com.wrusic.wrusic.model.Album;
import com.wrusic.wrusic.repository.AlbumRepository;
import com.wrusic.wrusic.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AlbumServiceImpl implements AlbumService {
    
    private final AlbumRepository albumRepository;
    
    @Autowired
    public AlbumServiceImpl(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }
    
    @Override
    public List<Album> findAllAlbums() {
        return albumRepository.findAll();
    }
    
    @Override
    public Optional<Album> findAlbumById(Long id) {
        return albumRepository.findById(id);
    }
    
    @Override
    public Album saveAlbum(Album album) {
        return albumRepository.save(album);
    }
    
    @Override
    public Album updateAlbum(Long id, Album album) {
        if (!albumRepository.existsById(id)) {
            throw new IllegalArgumentException("Album with ID " + id + " not found");
        }
        
        album.setId(id);
        return albumRepository.save(album);
    }
    
    @Override
    public void deleteAlbum(Long id) {
        if (!albumRepository.existsById(id)) {
            throw new IllegalArgumentException("Album with ID " + id + " not found");
        }
        
        albumRepository.deleteById(id);
    }
    
    @Override
    public List<Album> findAlbumsByArtistId(Long artistId) {
        return albumRepository.findByArtistsId(artistId);
    }
    
    @Override
    public List<Album> findAlbumsByGenre(String genre) {
        return albumRepository.findByGenreIgnoreCase(genre);
    }
    
    @Override
    public List<Album> searchAlbumsByTitle(String title) {
        return albumRepository.findByTitleContainingIgnoreCase(title);
    }
    
    @Override
    public Page<Album> findLatestReleases(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return albumRepository.findByReleaseDateBetweenOrderByReleaseDateDesc(startDate, endDate, pageable);
    }
} 