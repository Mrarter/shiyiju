package com.shiyiju.modules.artist;

import com.shiyiju.common.exception.BusinessException;
import com.shiyiju.modules.artist.entity.ArtistHomeEntity;
import com.shiyiju.modules.artist.entity.ArtistWorkEntity;
import com.shiyiju.modules.artist.mapper.ArtistMapper;
import com.shiyiju.modules.artist.service.ArtistService;
import com.shiyiju.modules.artist.vo.ArtistDetailVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArtistServiceTest {

    @Mock
    private ArtistMapper artistMapper;

    @InjectMocks
    private ArtistService artistService;

    @Test
    void shouldBuildArtistDetail() {
        ArtistHomeEntity home = new ArtistHomeEntity();
        home.setArtistId(1L);
        home.setArtistName("王蒋远");
        home.setLevelName("签约艺术家");
        home.setStyleTags("风景叙事,抽象");
        home.setSignedFlag(1);
        home.setFanCount(120);
        home.setWorkCount(8);
        home.setSoldCount(3);

        ArtistWorkEntity work = new ArtistWorkEntity();
        work.setArtworkId(100L);
        work.setArtworkNo("A001");
        work.setTitle("山间云起");
        work.setCurrentPrice(new BigDecimal("8800.00"));
        work.setSaleStatus("PUBLISHED");

        when(artistMapper.findArtistHome(1L)).thenReturn(home);
        when(artistMapper.findArtistWorks(1L)).thenReturn(List.of(work));

        ArtistDetailVO result = artistService.getArtistDetail(1L);

        assertEquals(1L, result.getArtistId());
        assertEquals(1, result.getWorks().size());
        assertEquals("山间云起", result.getWorks().get(0).getTitle());
        assertEquals(2, result.getStyleTags().size());
        assertEquals(true, result.getSigned());
    }

    @Test
    void shouldThrowWhenArtistMissing() {
        when(artistMapper.findArtistHome(1L)).thenReturn(null);
        assertThrows(BusinessException.class, () -> artistService.getArtistDetail(1L));
    }

    @Test
    void shouldListRecommendedArtists() {
        ArtistHomeEntity home = new ArtistHomeEntity();
        home.setArtistId(1L);
        home.setArtistName("王蒋远");
        home.setLevelName("签约艺术家");
        home.setSlogan("风景叙事");
        when(artistMapper.findRecommendedArtists(6)).thenReturn(List.of(home));

        var result = artistService.listRecommendedArtists(6);

        assertEquals(1, result.size());
        assertEquals("王蒋远", result.get(0).getArtistName());
    }
}
