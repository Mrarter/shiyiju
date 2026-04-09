package com.shiyiju.modules.artwork.service;

import com.shiyiju.common.exception.BusinessException;
import com.shiyiju.modules.artwork.dto.ArtworkQueryDTO;
import com.shiyiju.modules.artwork.entity.ArtworkDetailEntity;
import com.shiyiju.modules.artwork.mapper.ArtworkMapper;
import com.shiyiju.modules.artwork.vo.ArtworkDetailVO;
import com.shiyiju.modules.artwork.vo.ArtworkListItemVO;
import com.shiyiju.modules.artwork.vo.ArtworkPriceRuleVO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

import java.util.List;

@Service
public class ArtworkService {

    private final ArtworkMapper artworkMapper;

    public ArtworkService(ArtworkMapper artworkMapper) {
        this.artworkMapper = artworkMapper;
    }

    public List<ArtworkListItemVO> listWorks(ArtworkQueryDTO queryDTO) {
        ArtworkQueryDTO effectiveQuery = normalizeQuery(queryDTO);
        List<ArtworkListItemVO> works = artworkMapper.findWorks(effectiveQuery).stream().map(entity -> {
            ArtworkListItemVO vo = new ArtworkListItemVO();
            vo.setArtworkId(entity.getArtworkId());
            vo.setArtworkNo(entity.getArtworkNo());
            vo.setArtistId(entity.getArtistId());
            vo.setArtistName(entity.getArtistName());
            vo.setArtistLevelName(entity.getArtistLevelName());
            vo.setTitle(entity.getTitle());
            vo.setCategory(entity.getCategory());
            vo.setSaleMode(entity.getSaleMode());
            vo.setSaleStatus(mapSaleStatus(entity.getStatus()));
            vo.setCoverUrl(entity.getCoverUrl());
            vo.setCurrentPrice(entity.getCurrentPrice());
            vo.setFavoriteCount(defaultZero(entity.getFavoriteCount()));
            vo.setViewCount(defaultZero(entity.getViewCount()));
            return vo;
        }).toList();

        // 如果数据库没有数据，返回模拟数据
        if (works.isEmpty()) {
            return getMockWorks();
        }
        return works;
    }

    public ArtworkDetailVO getWorkDetail(Long artworkId) {
        ArtworkDetailEntity entity = artworkMapper.findWorkDetail(artworkId);
        if (entity == null) {
            throw new BusinessException(40004, "作品不存在");
        }

        ArtworkDetailVO vo = new ArtworkDetailVO();
        vo.setArtworkId(entity.getArtworkId());
        vo.setArtworkNo(entity.getArtworkNo());
        vo.setArtistId(entity.getArtistId());
        vo.setArtistName(entity.getArtistName());
        vo.setArtistLevelName(entity.getArtistLevelName());
        vo.setTitle(entity.getTitle());
        vo.setCategory(entity.getCategory());
        vo.setStyle(entity.getStyle());
        vo.setWidthCm(entity.getWidthCm());
        vo.setHeightCm(entity.getHeightCm());
        vo.setDepthCm(entity.getDepthCm());
        vo.setMaterial(entity.getMaterial());
        vo.setCreationYear(entity.getCreationYear());
        vo.setDescription(entity.getDescription());
        vo.setBasePrice(entity.getBasePrice());
        vo.setCurrentPrice(entity.getCurrentPrice());
        vo.setSaleStatus(mapSaleStatus(entity.getStatus()));
        vo.setPriceStatus(entity.getPriceStatus());
        vo.setOnlineDays(defaultZero(entity.getOnlineDays()));
        vo.setFavoriteCount(defaultZero(entity.getFavoriteCount()));
        vo.setViewCount(defaultZero(entity.getViewCount()));
        vo.setGroupEnabled(entity.getSupportGroupBuy() != null && entity.getSupportGroupBuy() == 1);
        vo.setResaleEnabled(entity.getSupportResale() == null || entity.getSupportResale() == 1);
        vo.setCertificateType("ELECTRONIC");
        vo.setSaleMode(entity.getSaleMode());
        vo.setCoverUrl(entity.getCoverUrl());
        vo.setMediaUrls(artworkMapper.findArtworkMedia(artworkId).stream().map(media -> media.getMediaUrl()).toList());
        vo.setPriceRule(buildPriceRule(entity));
        return vo;
    }

    private List<ArtworkListItemVO> getMockWorks() {
        return List.of(
            createWork(1L, "SYJ20260001", 1001L, "李明", "签约艺术家", "静谧的山谷", "PAINTING", "FIXED", "ON_SALE", new BigDecimal("12800"), 158, 326),
            createWork(2L, "SYJ20260002", 1002L, "王芳", "签约艺术家", "城市光影系列", "PRINT", "FIXED", "ON_SALE", new BigDecimal("6800"), 89, 215),
            createWork(3L, "SYJ20260003", 1003L, "张伟", "资深艺术家", "水墨山水", "INK", "FIXED", "ON_SALE", new BigDecimal("28000"), 234, 567),
            createWork(4L, "SYJ20260004", 1001L, "李明", "签约艺术家", "抽象艺术 No.7", "PAINTING", "FIXED", "ON_SALE", new BigDecimal("15800"), 156, 423),
            createWork(5L, "SYJ20260005", 1004L, "陈静", "签约艺术家", "海边日落", "PAINTING", "FIXED", "ON_SALE", new BigDecimal("8800"), 67, 198),
            createWork(6L, "SYJ20260006", 1005L, "赵磊", "资深艺术家", "雕塑作品 #3", "SCULPTURE", "FIXED", "ON_SALE", new BigDecimal("45000"), 45, 123),
            createWork(7L, "SYJ20260007", 1002L, "王芳", "签约艺术家", "花卉系列", "PRINT", "FIXED", "ON_SALE", new BigDecimal("5200"), 78, 189),
            createWork(8L, "SYJ20260008", 1003L, "张伟", "资深艺术家", "竹林深处", "INK", "FIXED", "ON_SALE", new BigDecimal("19800"), 123, 345),
            createWork(9L, "SYJ20260009", 1006L, "吴敏", "签约艺术家", "星空之下", "PAINTING", "FIXED", "ON_SALE", new BigDecimal("22800"), 98, 267),
            createWork(10L, "SYJ20260010", 1007L, "郑强", "签约艺术家", "现代都市", "PRINT", "FIXED", "ON_SALE", new BigDecimal("7800"), 56, 145),
            createWork(11L, "SYJ20260011", 1008L, "林立", "签约艺术家", "秋日私语", "PAINTING", "FIXED", "ON_SALE", new BigDecimal("16800"), 112, 298),
            createWork(12L, "SYJ20260012", 1009L, "黄丽", "签约艺术家", "铜版画 #12", "PRINT", "FIXED", "ON_SALE", new BigDecimal("9800"), 89, 234),
            createWork(13L, "SYJ20260013", 1010L, "杨帆", "资深艺术家", "烟雨江南", "INK", "FIXED", "ON_SALE", new BigDecimal("32000"), 178, 456),
            createWork(14L, "SYJ20260014", 1011L, "马超", "签约艺术家", "粉色幻想", "PAINTING", "FIXED", "ON_SALE", new BigDecimal("12800"), 145, 378),
            createWork(15L, "SYJ20260015", 1012L, "徐磊", "资深艺术家", "青铜器系列", "SCULPTURE", "FIXED", "ON_SALE", new BigDecimal("58000"), 34, 89),
            createWork(16L, "SYJ20260016", 1013L, "钟华", "签约艺术家", "黑白摄影", "PRINT", "FIXED", "ON_SALE", new BigDecimal("4200"), 67, 156),
            createWork(17L, "SYJ20260017", 1010L, "杨帆", "资深艺术家", "黄山云海", "INK", "FIXED", "ON_SALE", new BigDecimal("45000"), 234, 567),
            createWork(18L, "SYJ20260018", 1014L, "曾伟", "签约艺术家", "晨曦微露", "PAINTING", "FIXED", "ON_SALE", new BigDecimal("15800"), 123, 312),
            createWork(19L, "SYJ20260019", 1009L, "黄丽", "签约艺术家", "丝网版画", "PRINT", "FIXED", "ON_SALE", new BigDecimal("6800"), 78, 198),
            createWork(20L, "SYJ20260020", 1015L, "梁勇", "资深艺术家", "木雕艺术", "SCULPTURE", "FIXED", "ON_SALE", new BigDecimal("36000"), 56, 134),
            createWork(21L, "SYJ20260021", 1016L, "宋涛", "签约艺术家", "落日晚霞", "PAINTING", "FIXED", "ON_SALE", new BigDecimal("18800"), 145, 389),
            createWork(22L, "SYJ20260022", 1017L, "卢敏", "签约艺术家", "古韵新风", "INK", "FIXED", "ON_SALE", new BigDecimal("22000"), 167, 423),
            createWork(23L, "SYJ20260023", 1018L, "许刚", "签约艺术家", "石版画 #5", "PRINT", "FIXED", "ON_SALE", new BigDecimal("12000"), 89, 234),
            createWork(24L, "SYJ20260024", 1019L, "钱丽", "签约艺术家", "夜的旋律", "PAINTING", "FIXED", "ON_SALE", new BigDecimal("9800"), 98, 267),
            createWork(25L, "SYJ20260025", 1020L, "蒋伟", "资深艺术家", "陶瓷雕塑", "SCULPTURE", "FIXED", "ON_SALE", new BigDecimal("28000"), 45, 112),
            createWork(26L, "SYJ20260026", 1021L, "沈明", "签约艺术家", "春意盎然", "PAINTING", "FIXED", "ON_SALE", new BigDecimal("14800"), 123, 312),
            createWork(27L, "SYJ20260027", 1022L, "韩静", "签约艺术家", "书法小品", "INK", "FIXED", "ON_SALE", new BigDecimal("8500"), 56, 145),
            createWork(28L, "SYJ20260028", 1023L, "冯强", "签约艺术家", "综合版画", "PRINT", "FIXED", "ON_SALE", new BigDecimal("5600"), 67, 178),
            createWork(29L, "SYJ20260029", 1024L, "曹磊", "资深艺术家", "雪山之巅", "PAINTING", "FIXED", "ON_SALE", new BigDecimal("32000"), 234, 567),
            createWork(30L, "SYJ20260030", 1025L, "张莉", "资深艺术家", "装置艺术", "SCULPTURE", "FIXED", "ON_SALE", new BigDecimal("68000"), 23, 67),
            createWork(31L, "SYJ20260031", 1026L, "程伟", "签约艺术家", "金色年华", "PAINTING", "FIXED", "ON_SALE", new BigDecimal("18800"), 156, 412),
            createWork(32L, "SYJ20260032", 1027L, "傅丽", "签约艺术家", "水乡印象", "INK", "FIXED", "ON_SALE", new BigDecimal("25800"), 178, 456),
            createWork(33L, "SYJ20260033", 1028L, "段勇", "签约艺术家", "限量复刻", "PRINT", "FIXED", "ON_SALE", new BigDecimal("18000"), 112, 289),
            createWork(34L, "SYJ20260034", 1029L, "夏敏", "签约艺术家", "梦幻泡影", "PAINTING", "FIXED", "ON_SALE", new BigDecimal("12800"), 134, 356),
            createWork(35L, "SYJ20260035", 1030L, "钟刚", "资深艺术家", "玉雕摆件", "SCULPTURE", "FIXED", "ON_SALE", new BigDecimal("88000"), 34, 89),
            createWork(36L, "SYJ20260036", 1031L, "乔磊", "签约艺术家", "暮色苍茫", "PAINTING", "FIXED", "ON_SALE", new BigDecimal("15800"), 145, 378),
            createWork(37L, "SYJ20260037", 1032L, "翟丽", "签约艺术家", "行书书法", "INK", "FIXED", "ON_SALE", new BigDecimal("12800"), 98, 245),
            createWork(38L, "SYJ20260038", 1033L, "方伟", "签约艺术家", "数字版画", "PRINT", "FIXED", "ON_SALE", new BigDecimal("3800"), 56, 134),
            createWork(39L, "SYJ20260039", 1034L, "康静", "签约艺术家", "荷塘月色", "PAINTING", "FIXED", "ON_SALE", new BigDecimal("16800"), 167, 423),
            createWork(40L, "SYJ20260040", 1035L, "史强", "资深艺术家", "铁艺雕塑", "SCULPTURE", "FIXED", "ON_SALE", new BigDecimal("42000"), 45, 112),
            createWork(41L, "SYJ20260041", 1036L, "薛磊", "签约艺术家", "都市喧嚣", "PAINTING", "FIXED", "ON_SALE", new BigDecimal("22800"), 178, 456),
            createWork(42L, "SYJ20260042", 1037L, "叶丽", "资深艺术家", "草书长卷", "INK", "FIXED", "ON_SALE", new BigDecimal("38000"), 234, 589),
            createWork(43L, "SYJ20260043", 1038L, "蒋伟", "签约艺术家", "石版艺术", "PRINT", "FIXED", "ON_SALE", new BigDecimal("15800"), 89, 223),
            createWork(44L, "SYJ20260044", 1039L, "许静", "签约艺术家", "童趣天真", "PAINTING", "FIXED", "ON_SALE", new BigDecimal("8800"), 67, 178),
            createWork(45L, "SYJ20260045", 1040L, "陆强", "资深艺术家", "玻璃艺术", "SCULPTURE", "FIXED", "ON_SALE", new BigDecimal("35000"), 34, 89),
            createWork(46L, "SYJ20260046", 1041L, "杜磊", "签约艺术家", "四季如歌", "PAINTING", "FIXED", "ON_SALE", new BigDecimal("19800"), 156, 412),
            createWork(47L, "SYJ20260047", 1042L, "苏丽", "资深艺术家", "写意山水", "INK", "FIXED", "ON_SALE", new BigDecimal("42000"), 267, 678),
            createWork(48L, "SYJ20260048", 1043L, "韩伟", "签约艺术家", "木刻版画", "PRINT", "FIXED", "ON_SALE", new BigDecimal("9800"), 78, 198),
            createWork(49L, "SYJ20260049", 1044L, "杨静", "签约艺术家", "生命之树", "PAINTING", "FIXED", "ON_SALE", new BigDecimal("25800"), 189, 489),
            createWork(50L, "SYJ20260050", 1045L, "朱强", "资深艺术家", "玉雕挂件", "SCULPTURE", "FIXED", "ON_SALE", new BigDecimal("58000"), 45, 123)
        );
    }

    private ArtworkListItemVO createWork(Long id, String artworkNo, Long artistId, String artistName, 
            String artistLevelName, String title, String category, String saleMode, 
            String saleStatus, BigDecimal currentPrice, int favoriteCount, int viewCount) {
        ArtworkListItemVO vo = new ArtworkListItemVO();
        vo.setArtworkId(id);
        vo.setArtworkNo(artworkNo);
        vo.setArtistId(artistId);
        vo.setArtistName(artistName);
        vo.setArtistLevelName(artistLevelName);
        vo.setTitle(title);
        vo.setCategory(category);
        vo.setSaleMode(saleMode);
        vo.setSaleStatus(saleStatus);
        vo.setCoverUrl("");
        vo.setCurrentPrice(currentPrice);
        vo.setFavoriteCount(favoriteCount);
        vo.setViewCount(viewCount);
        return vo;
    }

    private ArtworkQueryDTO normalizeQuery(ArtworkQueryDTO queryDTO) {
        ArtworkQueryDTO normalized = queryDTO == null ? new ArtworkQueryDTO() : queryDTO;
        if (!StringUtils.hasText(normalized.getStatus())) {
            normalized.setStatus("PUBLISHED");
        }
        if (StringUtils.hasText(normalized.getKeyword())) {
            normalized.setKeyword(normalized.getKeyword().trim());
        }
        if (StringUtils.hasText(normalized.getCategory())) {
            normalized.setCategory(normalized.getCategory().trim());
        }
        if (StringUtils.hasText(normalized.getStatus())) {
            normalized.setStatus(normalized.getStatus().trim());
        }
        return normalized;
    }

    private ArtworkPriceRuleVO buildPriceRule(ArtworkDetailEntity entity) {
        if (entity.getOnlineDaysWeight() == null
            && entity.getFollowCountWeight() == null
            && entity.getArtistFloatMin() == null
            && entity.getArtistFloatMax() == null
            && entity.getPlatformMaxGrowthRate() == null
            && entity.getManualAdjustRate() == null) {
            return null;
        }
        ArtworkPriceRuleVO vo = new ArtworkPriceRuleVO();
        vo.setOnlineDaysWeight(entity.getOnlineDaysWeight());
        vo.setFollowCountWeight(entity.getFollowCountWeight());
        vo.setArtistFloatMin(entity.getArtistFloatMin());
        vo.setArtistFloatMax(entity.getArtistFloatMax());
        vo.setPlatformMaxGrowthRate(entity.getPlatformMaxGrowthRate());
        vo.setManualAdjustRate(entity.getManualAdjustRate());
        return vo;
    }

    private String mapSaleStatus(String status) {
        return switch (status) {
            case "PUBLISHED" -> "ON_SALE";
            case "COLLECTED", "SOLD_OUT" -> "COLLECTED";
            default -> status;
        };
    }

    private Integer defaultZero(Integer value) {
        return value == null ? 0 : value;
    }
}
