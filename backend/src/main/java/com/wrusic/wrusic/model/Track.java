package com.wrusic.wrusic.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tracks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Track {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(length = 1000)
    private String description;
    
    private String coverImageUrl;
    
    private String audioUrl;
    
    private LocalDate releaseDate;
    
    private Integer duration; // in seconds
    
    private String genre;
    
    private Integer chartPosition;
    
    private String country;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id")
    private Artist artist;
    
    @ManyToMany(mappedBy = "tracks")
    private Set<Album> albums = new HashSet<>();
    
    @OneToMany(mappedBy = "track", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ChartEntry> chartEntries = new HashSet<>();

    private String spotifyId;
    private String previewUrl;
    private String imageUrl;
    private String artistName;
    private String albumName;
} 