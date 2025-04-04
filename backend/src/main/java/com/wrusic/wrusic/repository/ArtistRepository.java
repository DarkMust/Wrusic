package com.wrusic.wrusic.repository;

import com.wrusic.wrusic.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {
    
    List<Artist> findByNameContainingIgnoreCase(String name);
    
    boolean existsByNameIgnoreCase(String name);
} 