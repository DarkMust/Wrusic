package com.wrusic.wrusic.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wrusic.wrusic.model.Artist;
import com.wrusic.wrusic.repository.ArtistRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ArtistIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ArtistRepository artistRepository;

    private Artist testArtist;

    @BeforeEach
    void setUp() {
        testArtist = new Artist();
        testArtist.setName("Test Artist");
        testArtist.setBio("Test Bio");
        testArtist.setImageUrl("http://example.com/image.jpg");
        testArtist = artistRepository.save(testArtist);
    }

    @AfterEach
    void tearDown() {
        artistRepository.deleteAll();
    }

    @Test
    void getAllArtists_ShouldReturnAllArtists() throws Exception {
        mockMvc.perform(get("/api/artists"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value(testArtist.getName()))
                .andExpect(jsonPath("$[0].bio").value(testArtist.getBio()));
    }

    @Test
    void getArtistById_WhenArtistExists_ShouldReturnArtist() throws Exception {
        mockMvc.perform(get("/api/artists/{id}", testArtist.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(testArtist.getName()))
                .andExpect(jsonPath("$.bio").value(testArtist.getBio()));
    }

    @Test
    void getArtistById_WhenArtistDoesNotExist_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/artists/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createArtist_ShouldCreateAndReturnNewArtist() throws Exception {
        Artist newArtist = new Artist();
        newArtist.setName("New Artist");
        newArtist.setBio("New Bio");
        newArtist.setImageUrl("http://example.com/new-image.jpg");

        mockMvc.perform(post("/api/artists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newArtist)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(newArtist.getName()))
                .andExpect(jsonPath("$.bio").value(newArtist.getBio()));
    }

    @Test
    void updateArtist_WhenArtistExists_ShouldUpdateAndReturnArtist() throws Exception {
        testArtist.setName("Updated Artist");
        testArtist.setBio("Updated Bio");

        mockMvc.perform(put("/api/artists/{id}", testArtist.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testArtist)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Artist"))
                .andExpect(jsonPath("$.bio").value("Updated Bio"));
    }

    @Test
    void updateArtist_WhenArtistDoesNotExist_ShouldReturnNotFound() throws Exception {
        testArtist.setId(999L);

        mockMvc.perform(put("/api/artists/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testArtist)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteArtist_WhenArtistExists_ShouldDeleteArtist() throws Exception {
        mockMvc.perform(delete("/api/artists/{id}", testArtist.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/artists/{id}", testArtist.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchArtists_ShouldReturnMatchingArtists() throws Exception {
        mockMvc.perform(get("/api/artists/search")
                .param("name", "Test"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value(testArtist.getName()));
    }
} 