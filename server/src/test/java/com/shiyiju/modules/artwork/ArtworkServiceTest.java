package com.shiyiju.modules.artwork;

import com.shiyiju.common.exception.BusinessException;
import com.shiyiju.modules.artwork.dto.ArtworkQueryDTO;
import com.shiyiju.modules.artwork.entity.ArtworkDetailEntity;
import com.shiyiju.modules.artwork.entity.ArtworkListItemEntity;
import com.shiyiju.modules.artwork.entity.ArtworkMediaEntity;
import com.shiyiju.modules.artwork.mapper.ArtworkMapper;
import com.shiyiju.modules.artwork.service.ArtworkService;
import com.shiyiju.modules.artwork.vo.ArtworkDetailVO;
import com.shiyiju.modules.artwork.vo.ArtworkListItemVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArtworkServiceTest {

    @Mock
    private ArtworkMapper artworkMapper;

    @InjectMocks
    private ArtworkService artworkService;

    @Test
    void shouldListPublishedWorksByDefault() {
        ArtworkListItemEntity entity = new ArtworkListItemEntity();
        entity.setArtworkId(101L);
        entity.setTitle("山间云起");
        entity.setStatus("PUBLISHED");
        entity.setCurrentPrice(new BigDecimal("8800.00"));

        when(artworkMapper.findWorks(any(ArtworkQueryDTO.class))).thenReturn(List.of(entity));

        List<ArtworkListItemVO> result = artworkService.listWorks(new ArtworkQueryDTO());

        assertEquals(1, result.size());
        assertEquals("ON_SALE", result.get(0).getSaleStatus());
        verify(artworkMapper).findWorks(any(ArtworkQueryDTO.class));
    }

    @Test
    void shouldBuildWorkDetail() {
        ArtworkDetailEntity entity = new ArtworkDetailEntity();
        entity.setArtworkId(100L);
        entity.setTitle("春山图");
        entity.setArtistName("王蒋远");
        entity.setStatus("COLLECTED");
        entity.setSupportGroupBuy(0);
        entity.setSupportResale(1);
        entity.setCurrentPrice(new BigDecimal("5200.00"));

        ArtworkMediaEntity mediaEntity = new ArtworkMediaEntity();
        mediaEntity.setMediaUrl("https://example.com/work.png");

        when(artworkMapper.findWorkDetail(100L)).thenReturn(entity);
        when(artworkMapper.findArtworkMedia(100L)).thenReturn(List.of(mediaEntity));

        ArtworkDetailVO result = artworkService.getWorkDetail(100L);

        assertEquals("COLLECTED", result.getSaleStatus());
        assertFalse(result.isGroupEnabled());
        assertEquals(1, result.getMediaUrls().size());
    }

    @Test
    void shouldThrowWhenWorkMissing() {
        when(artworkMapper.findWorkDetail(1L)).thenReturn(null);
        assertThrows(BusinessException.class, () -> artworkService.getWorkDetail(1L));
    }
}
