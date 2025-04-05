package com.wrusic.wrusic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wrusic.wrusic.model.Artist;
import com.wrusic.wrusic.service.ArtistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ArtistController.class)
public class ArtistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArtistService artistService;

    @Autowired
    private ObjectMapper objectMapper;

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
    void getAllArtists_ShouldReturnArtistList() throws Exception {
        when(artistService.findAllArtists()).thenReturn(Arrays.asList(testArtist));

        mockMvc.perform(get("/api/artists"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(testArtist.getId()))
                .andExpect(jsonPath("$[0].name").value(testArtist.getName()));
    }

    @Test
    void getArtistById_WhenArtistExists_ShouldReturnArtist() throws Exception {
        when(artistService.findArtistById(1L)).thenReturn(Optional.of(testArtist));

        mockMvc.perform(get("/api/artists/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testArtist.getId()))
                .andExpect(jsonPath("$.name").value(testArtist.getName()));
    }

    @Test
    void getArtistById_WhenArtistDoesNotExist_ShouldReturnNotFound() throws Exception {
        when(artistService.findArtistById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/artists/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createArtist_ShouldReturnCreatedArtist() throws Exception {
        when(artistService.saveArtist(any(Artist.class))).thenReturn(testArtist);

        mockMvc.perform(post("/api/artists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testArtist)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testArtist.getId()))
                .andExpect(jsonPath("$.name").value(testArtist.getName()));
    }

    @Test
    void updateArtist_WhenArtistExists_ShouldReturnUpdatedArtist() throws Exception {
        when(artistService.updateArtist(eq(1L), any(Artist.class))).thenReturn(testArtist);

        mockMvc.perform(put("/api/artists/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testArtist)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testArtist.getId()))
                .andExpect(jsonPath("$.name").value(testArtist.getName()));
    }

    @Test
    void updateArtist_WhenArtistDoesNotExist_ShouldReturnNotFound() throws Exception {
        when(artistService.updateArtist(eq(1L), any(Artist.class)))
                .thenThrow(new IllegalArgumentException("Artist not found"));

        mockMvc.perform(put("/api/artists/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testArtist)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteArtist_WhenArtistExists_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/artists/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void searchArtists_ShouldReturnMatchingArtists() throws Exception {
        when(artistService.searchArtistsByName("Test")).thenReturn(Arrays.asList(testArtist));

        mockMvc.perform(get("/api/artists/search").param("name", "Test"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(testArtist.getId()))
                .andExpect(jsonPath("$[0].name").value(testArtist.getName()));
    }
} 