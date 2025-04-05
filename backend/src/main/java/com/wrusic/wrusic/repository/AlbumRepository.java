package com.wrusic.wrusic.repository;

import com.wrusic.wrusic.model.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
    
    Page<Album> findByReleaseDateBetweenOrderByReleaseDateDesc(LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    List<Album> findByArtistsId(Long artistId);
    
    List<Album> findByGenreIgnoreCase(String genre);
    
    List<Album> findByTitleContainingIgnoreCase(String title);
} 