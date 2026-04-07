package com.shiyiju.modules.artwork.mapper;

import com.shiyiju.modules.artwork.dto.ArtworkQueryDTO;
import com.shiyiju.modules.artwork.entity.ArtworkDetailEntity;
import com.shiyiju.modules.artwork.entity.ArtworkListItemEntity;
import com.shiyiju.modules.artwork.entity.ArtworkMediaEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArtworkMapper {

    List<ArtworkListItemEntity> findWorks(ArtworkQueryDTO queryDTO);

    ArtworkDetailEntity findWorkDetail(@Param("artworkId") Long artworkId);

    List<ArtworkMediaEntity> findArtworkMedia(@Param("artworkId") Long artworkId);
}
