package com.shiyiju.modules.artist;

import com.shiyiju.config.GlobalExceptionHandler;
import com.shiyiju.modules.artist.controller.ArtistController;
import com.shiyiju.modules.artist.service.ArtistService;
import com.shiyiju.modules.artist.vo.ArtistCardVO;
import com.shiyiju.modules.artist.vo.ArtistDetailVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ArtistControllerTest {

    @Mock
    private ArtistService artistService;

    @InjectMocks
    private ArtistController artistController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(artistController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    @Test
    void shouldGetArtistDetail() throws Exception {
        ArtistDetailVO detail = new ArtistDetailVO();
        detail.setArtistId(1L);
        detail.setArtistName("王蒋远");
        when(artistService.getArtistDetail(1L)).thenReturn(detail);

        mockMvc.perform(get("/api/v1/artists/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data.artistName").value("王蒋远"));
    }

    @Test
    void shouldGetRecommendedArtists() throws Exception {
        ArtistCardVO artist = new ArtistCardVO();
        artist.setArtistId(1L);
        artist.setArtistName("王蒋远");
        when(artistService.listRecommendedArtists(4)).thenReturn(List.of(artist));

        mockMvc.perform(get("/api/v1/artists/recommend").param("limit", "4"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data[0].artistId").value(1));
    }
}
