package com.wrusic.wrusic.service;

import com.wrusic.wrusic.model.Artist;
import com.wrusic.wrusic.repository.ArtistRepository;
import com.wrusic.wrusic.service.impl.ArtistServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ArtistServiceTest {

    @Mock
    private ArtistRepository artistRepository;

    @InjectMocks
    private ArtistServiceImpl artistService;

    private Artist testArtist;

    @BeforeEach
    void setUp() {
        testArtist = new Artist();
        testArtist.setId(1L);
        testArtist.setName("Test Artist");
        testArtist.setBio("Test Bio");
        testArtist.setImageUrl("http://example.com/image.jpg");
    }

    @Test
    void findAllArtists_ShouldReturnListOfArtists() {
        when(artistRepository.findAll()).thenReturn(Arrays.asList(testArtist));

        List<Artist> result = artistService.findAllArtists();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo(testArtist.getName());
        verify(artistRepository, times(1)).findAll();
    }

    @Test
    void findArtistById_WhenArtistExists_ShouldReturnArtist() {
        when(artistRepository.findById(1L)).thenReturn(Optional.of(testArtist));

        Optional<Artist> result = artistService.findArtistById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo(testArtist.getName());
        verify(artistRepository, times(1)).findById(1L);
    }

    @Test
    void findArtistById_WhenArtistDoesNotExist_ShouldReturnEmpty() {
        when(artistRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Artist> result = artistService.findArtistById(1L);

        assertThat(result).isEmpty();
        verify(artistRepository, times(1)).findById(1L);
    }

    @Test
    void saveArtist_ShouldReturnSavedArtist() {
        when(artistRepository.save(any(Artist.class))).thenReturn(testArtist);

        Artist result = artistService.saveArtist(testArtist);

        assertThat(result.getName()).isEqualTo(testArtist.getName());
        verify(artistRepository, times(1)).save(testArtist);
    }

    @Test
    void updateArtist_WhenArtistExists_ShouldReturnUpdatedArtist() {
        when(artistRepository.existsById(1L)).thenReturn(true);
        when(artistRepository.save(any(Artist.class))).thenReturn(testArtist);

        Artist result = artistService.updateArtist(1L, testArtist);

        assertThat(result.getName()).isEqualTo(testArtist.getName());
        verify(artistRepository, times(1)).existsById(1L);
        verify(artistRepository, times(1)).save(testArtist);
    }

    @Test
    void updateArtist_WhenArtistDoesNotExist_ShouldThrowException() {
        when(artistRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> artistService.updateArtist(1L, testArtist))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Artist with ID 1 not found");

        verify(artistRepository, times(1)).existsById(1L);
        verify(artistRepository, never()).save(any());
    }

    @Test
    void deleteArtist_WhenArtistExists_ShouldDeleteArtist() {
        when(artistRepository.existsById(1L)).thenReturn(true);

        artistService.deleteArtist(1L);

        verify(artistRepository, times(1)).existsById(1L);
        verify(artistRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteArtist_WhenArtistDoesNotExist_ShouldThrowException() {
        when(artistRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> artistService.deleteArtist(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Artist with ID 1 not found");

        verify(artistRepository, times(1)).existsById(1L);
        verify(artistRepository, never()).deleteById(any());
    }

    @Test
    void searchArtistsByName_ShouldReturnMatchingArtists() {
        when(artistRepository.findByNameContainingIgnoreCase("Test"))
                .thenReturn(Arrays.asList(testArtist));

        List<Artist> result = artistService.searchArtistsByName("Test");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo(testArtist.getName());
        verify(artistRepository, times(1)).findByNameContainingIgnoreCase("Test");
    }

    @Test
    void existsByName_WhenArtistExists_ShouldReturnTrue() {
        when(artistRepository.existsByNameIgnoreCase("Test Artist")).thenReturn(true);

        boolean result = artistService.existsByName("Test Artist");

        assertThat(result).isTrue();
        verify(artistRepository, times(1)).existsByNameIgnoreCase("Test Artist");
    }

    @Test
    void existsByName_WhenArtistDoesNotExist_ShouldReturnFalse() {
        when(artistRepository.existsByNameIgnoreCase("Test Artist")).thenReturn(false);

        boolean result = artistService.existsByName("Test Artist");

        assertThat(result).isFalse();
        verify(artistRepository, times(1)).existsByNameIgnoreCase("Test Artist");
    }
} 