package com.shiyiju.modules.artist.mapper;

import com.shiyiju.modules.artist.entity.ArtistHomeEntity;
import com.shiyiju.modules.artist.entity.ArtistWorkEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ArtistMapper {

    ArtistHomeEntity findArtistHome(@Param("artistId") Long artistId);

    List<ArtistWorkEntity> findArtistWorks(@Param("artistId") Long artistId);

    List<ArtistHomeEntity> findRecommendedArtists(@Param("limit") Integer limit);
}
