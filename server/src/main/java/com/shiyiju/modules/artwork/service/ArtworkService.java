package com.shiyiju.modules.artwork.service;

import com.shiyiju.common.exception.BusinessException;
import com.shiyiju.common.util.ImageUrlUtil;
import com.shiyiju.modules.artwork.dto.ArtworkQueryDTO;
import com.shiyiju.modules.artwork.entity.ArtworkDetailEntity;
import com.shiyiju.modules.artwork.entity.ArtworkListItemEntity;
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
    private final ImageUrlUtil imageUrlUtil;

    public ArtworkService(ArtworkMapper artworkMapper, ImageUrlUtil imageUrlUtil) {
        this.artworkMapper = artworkMapper;
        this.imageUrlUtil = imageUrlUtil;
    }

    public List<ArtworkListItemVO> listWorks(ArtworkQueryDTO queryDTO) {
        ArtworkQueryDTO effectiveQuery = normalizeQuery(queryDTO);
        
        // 优先从数据库读取作品数据
        List<ArtworkListItemEntity> entities = artworkMapper.findWorks(effectiveQuery);
        List<ArtworkListItemVO> works;
        
        if (entities == null || entities.isEmpty()) {
            // 数据库为空时返回mock数据兜底
            works = getMockWorks();
        } else {
            // 转换Entity为VO
            works = entities.stream().map(this::toArtworkListVO).collect(java.util.stream.Collectors.toList());
        }
        
        // 按权重排序
        return sortByWeight(works);
    }
    
    private ArtworkListItemVO toArtworkListVO(ArtworkListItemEntity entity) {
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
        vo.setFavoriteCount(entity.getFavoriteCount());
        vo.setViewCount(entity.getViewCount());
        // 设置艺术家头像
        vo.setArtistAvatar(generateArtistAvatar(entity.getArtistName()));
        // 处理封面图URL，确保是完整路径
        vo.setCoverUrl(normalizeImageUrl(entity.getCoverUrl()));
        // 权重相关字段
        vo.setCartCount(0);
        // 从数据库读取admin_weight，如果为null则默认为1
        vo.setAdminWeight(entity.getAdminWeight() != null ? entity.getAdminWeight() : 1);
        return vo;
    }
    
    private String normalizeImageUrl(String url) {
        return imageUrlUtil.normalize(url);
    }
    
    private String normalizeImageUrl(String url, String placeholder) {
        return imageUrlUtil.normalize(url, placeholder);
    }
    
    private String generateArtistAvatar(String artistName) {
        if (artistName == null || artistName.isEmpty()) {
            return "https://ui-avatars.com/api/?name=U&background=c9a96d&color=fff&size=128";
        }
        try {
            return "https://ui-avatars.com/api/?name=" + java.net.URLEncoder.encode(artistName, "UTF-8") 
                   + "&background=c9a96d&color=fff&size=128&font-size=0.4&bold=true&rounded=true";
        } catch (Exception e) {
            return "https://ui-avatars.com/api/?name=" + artistName + "&background=c9a96d&color=fff&size=128&rounded=true";
        }
    }
    
    // 权重排序：后台配置权重*10 + 点击次数*1 + 收藏次数*3 + 加入购物车次数*5
    private List<ArtworkListItemVO> sortByWeight(List<ArtworkListItemVO> works) {
        return works.stream()
            .map(work -> {
                // 计算权重得分
                int adminWeight = work.getAdminWeight() != null ? work.getAdminWeight() : 1;
                int viewScore = (work.getViewCount() != null ? work.getViewCount() : 0) * 1;
                int favoriteScore = (work.getFavoriteCount() != null ? work.getFavoriteCount() : 0) * 3;
                int cartScore = (work.getCartCount() != null ? work.getCartCount() : 0) * 5;
                int totalWeight = adminWeight * 10 + viewScore + favoriteScore + cartScore;
                work.setWeight(totalWeight);
                return work;
            })
            .sorted((a, b) -> {
                // 权重高的在前
                int weightCompare = Integer.compare(b.getWeight(), a.getWeight());
                if (weightCompare != 0) return weightCompare;
                // 权重相同按原始顺序
                return a.getArtworkId().compareTo(b.getArtworkId());
            })
            .toList();
    }
    
    // 记录点击次数
    public void recordClick(Long artworkId) {
        // 演示模式：只打印日志，不实际存储
        System.out.println("Record click for artwork: " + artworkId);
    }
    
    // 记录收藏状态
    public void recordFavorite(Long artworkId, boolean isFavorite) {
        // 演示模式：只打印日志，不实际存储
        System.out.println("Record favorite for artwork: " + artworkId + ", isFavorite: " + isFavorite);
    }
    
    // 记录加入购物车
    public void recordCart(Long artworkId) {
        // 演示模式：只打印日志，不实际存储
        System.out.println("Record cart for artwork: " + artworkId);
    }

    public ArtworkDetailVO getWorkDetail(Long artworkId) {
        // 演示模式：检查是否是模拟数据的ID范围 (1-50)
        if (isMockArtworkId(artworkId)) {
            return getMockWorkDetail(artworkId);
        }
        
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
        vo.setArtistAvatar(entity.getArtistAvatar());
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
        // 标准化图片URL
        vo.setCoverUrl(imageUrlUtil.normalize(entity.getCoverUrl()));
        vo.setMediaUrls(artworkMapper.findArtworkMedia(artworkId).stream()
            .map(media -> imageUrlUtil.normalize(media.getMediaUrl()))
            .toList());
        return vo;
    }
    
    private boolean isMockArtworkId(Long artworkId) {
        return artworkId != null && artworkId >= 1 && artworkId <= 50;
    }
    
    private ArtworkDetailVO getMockWorkDetail(Long artworkId) {
        // 占位图URL
        String coverUrl = "https://picsum.photos/seed/art" + artworkId + "/400/500";
        
        ArtworkDetailVO vo = new ArtworkDetailVO();
        vo.setArtworkId(artworkId);
        vo.setArtworkNo("SYJ2026" + String.format("%04d", artworkId));
        vo.setArtistId(1000L + artworkId);
        vo.setArtistName(getArtistNameForDetail(artworkId));
        vo.setArtistLevelName(artworkId % 3 == 0 ? "资深艺术家" : "签约艺术家");
        vo.setArtistAvatar(getArtistAvatarForDetail(artworkId));
        vo.setTitle(getTitleForDetail(artworkId));
        vo.setCategory(getCategoryForDetail(artworkId));
        vo.setStyle("当代艺术");
        vo.setWidthCm(new BigDecimal("80"));
        vo.setHeightCm(new BigDecimal("100"));
        vo.setDepthCm(null);
        vo.setMaterial("布面油画");
        vo.setCreationYear("2025");
        vo.setDescription("这是一幅精美的艺术作品，展现了艺术家的独特风格和深厚功底。");
        vo.setBasePrice(new BigDecimal("10000"));
        vo.setCurrentPrice(new BigDecimal(String.valueOf(10000 + artworkId * 1000)));
        vo.setSaleStatus("ON_SALE");
        vo.setPriceStatus("FIXED");
        vo.setOnlineDays(artworkId.intValue() * 5);
        vo.setFavoriteCount(artworkId.intValue() * 10);
        vo.setViewCount(artworkId.intValue() * 50);
        vo.setGroupEnabled(false);
        vo.setResaleEnabled(true);
        vo.setCertificateType("ELECTRONIC");
        vo.setSaleMode("FIXED");
        vo.setCoverUrl(coverUrl);
        vo.setMediaUrls(List.of(coverUrl));
        vo.setPriceRule(null);
        return vo;
    }
    
    private String getArtistNameForDetail(Long artworkId) {
        String[] names = {"李明", "王芳", "张伟", "刘涛", "陈静", "赵磊", "孙丽", "周杰", "吴敏", "郑强",
                          "林立", "黄丽", "杨帆", "马超", "徐磊", "钟华", "何静", "曾伟", "丁丽", "梁勇",
                          "宋涛", "卢敏", "许刚", "钱丽", "蒋伟", "沈明", "韩静", "冯强", "曹磊", "张莉",
                          "程伟", "傅丽", "段勇", "夏敏", "钟刚", "乔磊", "翟丽", "方伟", "康静", "史强",
                          "薛磊", "叶丽", "蒋伟", "许静", "陆强", "杜磊", "苏丽", "韩伟", "杨静", "朱强"};
        return names[(artworkId.intValue() - 1) % names.length];
    }

    // 艺术家头像 - 使用占位头像
    private String getArtistAvatarForDetail(Long artworkId) {
        // 使用 ui-avatars.com 生成带名字的头像
        String name = getArtistNameForDetail(artworkId);
        return "https://ui-avatars.com/api/?name=" + java.net.URLEncoder.encode(name) + "&background=c9a96d&color=fff&size=128&font-size=0.4&bold=true";
    }
    
    private String getTitleForDetail(Long artworkId) {
        String[] titles = {
            "静谧的山谷", "城市光影系列", "水墨山水", "抽象艺术 No.7", "海边日落",
            "雕塑作品 #3", "花卉系列", "竹林深处", "星空之下", "现代都市",
            "秋日私语", "铜版画 #12", "烟雨江南", "粉色幻想", "青铜器系列",
            "黑白摄影", "黄山云海", "晨曦微露", "丝网版画", "木雕艺术",
            "落日晚霞", "古韵新风", "石版画 #5", "夜的旋律", "陶瓷雕塑",
            "春意盎然", "书法小品", "综合版画", "雪山之巅", "装置艺术",
            "金色年华", "水乡印象", "限量复刻", "梦幻泡影", "玉雕摆件",
            "暮色苍茫", "行书书法", "数字版画", "荷塘月色", "铁艺雕塑",
            "都市喧嚣", "草书长卷", "石版艺术", "童趣天真", "玻璃艺术",
            "四季如歌", "写意山水", "木刻版画", "生命之树", "玉雕挂件"
        };
        return titles[(artworkId.intValue() - 1) % titles.length];
    }
    
    private String getCategoryForDetail(Long artworkId) {
        String[] categories = {"PAINTING", "PRINT", "INK", "SCULPTURE", "PRINT"};
        return categories[(artworkId.intValue() - 1) % categories.length];
    }

    private List<ArtworkListItemVO> getMockWorks() {
        return List.of(
            createWork(1L, "SYJ20260001", 1001L, "李明", "签约艺术家", "静谧的山谷", "PAINTING", "FIXED", "ON_SALE", new BigDecimal("12800"), 158, 326, "https://picsum.photos/seed/art1/400/500"),
            createWork(2L, "SYJ20260002", 1002L, "王芳", "签约艺术家", "城市光影系列", "PRINT", "FIXED", "ON_SALE", new BigDecimal("6800"), 89, 215, "https://picsum.photos/seed/art2/400/500"),
            createWork(3L, "SYJ20260003", 1003L, "张伟", "资深艺术家", "水墨山水", "INK", "FIXED", "ON_SALE", new BigDecimal("28000"), 234, 567, "https://picsum.photos/seed/art3/400/500"),
            createWork(4L, "SYJ20260004", 1001L, "李明", "签约艺术家", "抽象艺术 No.7", "PAINTING", "FIXED", "ON_SALE", new BigDecimal("15800"), 156, 423, "https://picsum.photos/seed/art4/400/500"),
            createWork(5L, "SYJ20260005", 1004L, "陈静", "签约艺术家", "海边日落", "PAINTING", "FIXED", "ON_SALE", new BigDecimal("8800"), 67, 198, "https://picsum.photos/seed/art5/400/500"),
            createWork(6L, "SYJ20260006", 1005L, "赵磊", "资深艺术家", "雕塑作品 #3", "SCULPTURE", "FIXED", "ON_SALE", new BigDecimal("45000"), 45, 123, "https://picsum.photos/seed/art6/400/500"),
            createWork(7L, "SYJ20260007", 1002L, "王芳", "签约艺术家", "花卉系列", "PRINT", "FIXED", "ON_SALE", new BigDecimal("5200"), 78, 189, "https://picsum.photos/seed/art7/400/500"),
            createWork(8L, "SYJ20260008", 1003L, "张伟", "资深艺术家", "竹林深处", "INK", "FIXED", "ON_SALE", new BigDecimal("19800"), 123, 345, "https://picsum.photos/seed/art8/400/500"),
            createWork(9L, "SYJ20260009", 1006L, "吴敏", "签约艺术家", "星空之下", "PAINTING", "FIXED", "ON_SALE", new BigDecimal("22800"), 98, 267, "https://picsum.photos/seed/art9/400/500"),
            createWork(10L, "SYJ20260010", 1007L, "郑强", "签约艺术家", "现代都市", "PRINT", "FIXED", "ON_SALE", new BigDecimal("7800"), 56, 145, "https://picsum.photos/seed/art10/400/500"),
            createWork(11L, "SYJ20260011", 1008L, "林立", "签约艺术家", "秋日私语", "PAINTING", "FIXED", "ON_SALE", new BigDecimal("16800"), 112, 298, "https://picsum.photos/seed/art11/400/500"),
            createWork(12L, "SYJ20260012", 1009L, "黄丽", "签约艺术家", "铜版画 #12", "PRINT", "FIXED", "ON_SALE", new BigDecimal("9800"), 89, 234, "https://picsum.photos/seed/art12/400/500"),
            createWork(13L, "SYJ20260013", 1010L, "杨帆", "资深艺术家", "烟雨江南", "INK", "FIXED", "ON_SALE", new BigDecimal("32000"), 178, 456, "https://picsum.photos/seed/art13/400/500"),
            createWork(14L, "SYJ20260014", 1011L, "马超", "签约艺术家", "粉色幻想", "PAINTING", "FIXED", "ON_SALE", new BigDecimal("12800"), 145, 378, "https://picsum.photos/seed/art14/400/500"),
            createWork(15L, "SYJ20260015", 1012L, "徐磊", "资深艺术家", "青铜器系列", "SCULPTURE", "FIXED", "ON_SALE", new BigDecimal("58000"), 34, 89, "https://picsum.photos/seed/art15/400/500"),
            createWork(16L, "SYJ20260016", 1013L, "钟华", "签约艺术家", "黑白摄影", "PRINT", "FIXED", "ON_SALE", new BigDecimal("4200"), 67, 156, "https://picsum.photos/seed/art16/400/500"),
            createWork(17L, "SYJ20260017", 1010L, "杨帆", "资深艺术家", "黄山云海", "INK", "FIXED", "ON_SALE", new BigDecimal("45000"), 234, 567, "https://picsum.photos/seed/art17/400/500"),
            createWork(18L, "SYJ20260018", 1014L, "曾伟", "签约艺术家", "晨曦微露", "PAINTING", "FIXED", "ON_SALE", new BigDecimal("15800"), 123, 312, "https://picsum.photos/seed/art18/400/500"),
            createWork(19L, "SYJ20260019", 1009L, "黄丽", "签约艺术家", "丝网版画", "PRINT", "FIXED", "ON_SALE", new BigDecimal("6800"), 78, 198, "https://picsum.photos/seed/art19/400/500"),
            createWork(20L, "SYJ20260020", 1015L, "梁勇", "资深艺术家", "木雕艺术", "SCULPTURE", "FIXED", "ON_SALE", new BigDecimal("36000"), 56, 134, "https://picsum.photos/seed/art20/400/500"),
            createWork(21L, "SYJ20260021", 1016L, "宋涛", "签约艺术家", "落日晚霞", "PAINTING", "FIXED", "ON_SALE", new BigDecimal("18800"), 145, 389, "https://picsum.photos/seed/art21/400/500"),
            createWork(22L, "SYJ20260022", 1017L, "卢敏", "签约艺术家", "古韵新风", "INK", "FIXED", "ON_SALE", new BigDecimal("22000"), 167, 423, "https://picsum.photos/seed/art22/400/500"),
            createWork(23L, "SYJ20260023", 1018L, "许刚", "签约艺术家", "石版画 #5", "PRINT", "FIXED", "ON_SALE", new BigDecimal("12000"), 89, 234, "https://picsum.photos/seed/art23/400/500"),
            createWork(24L, "SYJ20260024", 1019L, "钱丽", "签约艺术家", "夜的旋律", "PAINTING", "FIXED", "ON_SALE", new BigDecimal("9800"), 98, 267, "https://picsum.photos/seed/art24/400/500"),
            createWork(25L, "SYJ20260025", 1020L, "蒋伟", "资深艺术家", "陶瓷雕塑", "SCULPTURE", "FIXED", "ON_SALE", new BigDecimal("28000"), 45, 112, "https://picsum.photos/seed/art25/400/500"),
            createWork(26L, "SYJ20260026", 1021L, "沈明", "签约艺术家", "春意盎然", "PAINTING", "FIXED", "ON_SALE", new BigDecimal("14800"), 123, 312, "https://picsum.photos/seed/art26/400/500"),
            createWork(27L, "SYJ20260027", 1022L, "韩静", "签约艺术家", "书法小品", "INK", "FIXED", "ON_SALE", new BigDecimal("8500"), 56, 145, "https://picsum.photos/seed/art27/400/500"),
            createWork(28L, "SYJ20260028", 1023L, "冯强", "签约艺术家", "综合版画", "PRINT", "FIXED", "ON_SALE", new BigDecimal("5600"), 67, 178, "https://picsum.photos/seed/art28/400/500"),
            createWork(29L, "SYJ20260029", 1024L, "曹磊", "资深艺术家", "雪山之巅", "PAINTING", "FIXED", "ON_SALE", new BigDecimal("32000"), 234, 567, "https://picsum.photos/seed/art29/400/500"),
            createWork(30L, "SYJ20260030", 1025L, "张莉", "资深艺术家", "装置艺术", "SCULPTURE", "FIXED", "ON_SALE", new BigDecimal("68000"), 23, 67, "https://picsum.photos/seed/art30/400/500"),
            createWork(31L, "SYJ20260031", 1026L, "程伟", "签约艺术家", "金色年华", "PAINTING", "FIXED", "ON_SALE", new BigDecimal("18800"), 156, 412, "https://picsum.photos/seed/art31/400/500"),
            createWork(32L, "SYJ20260032", 1027L, "傅丽", "签约艺术家", "水乡印象", "INK", "FIXED", "ON_SALE", new BigDecimal("25800"), 178, 456, "https://picsum.photos/seed/art32/400/500"),
            createWork(33L, "SYJ20260033", 1028L, "段勇", "签约艺术家", "限量复刻", "PRINT", "FIXED", "ON_SALE", new BigDecimal("18000"), 112, 289, "https://picsum.photos/seed/art33/400/500"),
            createWork(34L, "SYJ20260034", 1029L, "夏敏", "签约艺术家", "梦幻泡影", "PAINTING", "FIXED", "ON_SALE", new BigDecimal("12800"), 134, 356, "https://picsum.photos/seed/art34/400/500"),
            createWork(35L, "SYJ20260035", 1030L, "钟刚", "资深艺术家", "玉雕摆件", "SCULPTURE", "FIXED", "ON_SALE", new BigDecimal("88000"), 34, 89, "https://picsum.photos/seed/art35/400/500"),
            createWork(36L, "SYJ20260036", 1031L, "乔磊", "签约艺术家", "暮色苍茫", "PAINTING", "FIXED", "ON_SALE", new BigDecimal("15800"), 145, 378, "https://picsum.photos/seed/art36/400/500"),
            createWork(37L, "SYJ20260037", 1032L, "翟丽", "签约艺术家", "行书书法", "INK", "FIXED", "ON_SALE", new BigDecimal("12800"), 98, 245, "https://picsum.photos/seed/art37/400/500"),
            createWork(38L, "SYJ20260038", 1033L, "方伟", "签约艺术家", "数字版画", "PRINT", "FIXED", "ON_SALE", new BigDecimal("3800"), 56, 134, "https://picsum.photos/seed/art38/400/500"),
            createWork(39L, "SYJ20260039", 1034L, "康静", "签约艺术家", "荷塘月色", "PAINTING", "FIXED", "ON_SALE", new BigDecimal("16800"), 167, 423, "https://picsum.photos/seed/art39/400/500"),
            createWork(40L, "SYJ20260040", 1035L, "史强", "资深艺术家", "铁艺雕塑", "SCULPTURE", "FIXED", "ON_SALE", new BigDecimal("42000"), 45, 112, "https://picsum.photos/seed/art40/400/500"),
            createWork(41L, "SYJ20260041", 1036L, "薛磊", "签约艺术家", "都市喧嚣", "PAINTING", "FIXED", "ON_SALE", new BigDecimal("22800"), 178, 456, "https://picsum.photos/seed/art41/400/500"),
            createWork(42L, "SYJ20260042", 1037L, "叶丽", "资深艺术家", "草书长卷", "INK", "FIXED", "ON_SALE", new BigDecimal("38000"), 234, 589, "https://picsum.photos/seed/art42/400/500"),
            createWork(43L, "SYJ20260043", 1038L, "蒋伟", "签约艺术家", "石版艺术", "PRINT", "FIXED", "ON_SALE", new BigDecimal("15800"), 89, 223, "https://picsum.photos/seed/art43/400/500"),
            createWork(44L, "SYJ20260044", 1039L, "许静", "签约艺术家", "童趣天真", "PAINTING", "FIXED", "ON_SALE", new BigDecimal("8800"), 67, 178, "https://picsum.photos/seed/art44/400/500"),
            createWork(45L, "SYJ20260045", 1040L, "陆强", "资深艺术家", "玻璃艺术", "SCULPTURE", "FIXED", "ON_SALE", new BigDecimal("35000"), 34, 89, "https://picsum.photos/seed/art45/400/500"),
            createWork(46L, "SYJ20260046", 1041L, "杜磊", "签约艺术家", "四季如歌", "PAINTING", "FIXED", "ON_SALE", new BigDecimal("19800"), 156, 412, "https://picsum.photos/seed/art46/400/500"),
            createWork(47L, "SYJ20260047", 1042L, "苏丽", "资深艺术家", "写意山水", "INK", "FIXED", "ON_SALE", new BigDecimal("42000"), 267, 678, "https://picsum.photos/seed/art47/400/500"),
            createWork(48L, "SYJ20260048", 1043L, "韩伟", "签约艺术家", "木刻版画", "PRINT", "FIXED", "ON_SALE", new BigDecimal("9800"), 78, 198, "https://picsum.photos/seed/art48/400/500"),
            createWork(49L, "SYJ20260049", 1044L, "杨静", "签约艺术家", "生命之树", "PAINTING", "FIXED", "ON_SALE", new BigDecimal("25800"), 189, 489, "https://picsum.photos/seed/art49/400/500"),
            createWork(50L, "SYJ20260050", 1045L, "朱强", "资深艺术家", "玉雕挂件", "SCULPTURE", "FIXED", "ON_SALE", new BigDecimal("58000"), 45, 123, "https://picsum.photos/seed/art50/400/500")
        );
    }

    private ArtworkListItemVO createWork(Long id, String artworkNo, Long artistId, String artistName, 
            String artistLevelName, String title, String category, String saleMode, 
            String saleStatus, BigDecimal currentPrice, int favoriteCount, int viewCount, String coverUrl) {
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
        vo.setCoverUrl(coverUrl);
        vo.setCurrentPrice(currentPrice);
        vo.setFavoriteCount(favoriteCount);
        vo.setViewCount(viewCount);
        return vo;
    }

    private ArtworkQueryDTO normalizeQuery(ArtworkQueryDTO queryDTO) {
        ArtworkQueryDTO normalized = queryDTO == null ? new ArtworkQueryDTO() : queryDTO;
        // 默认显示所有非草稿状态的作品（PUBLISHED, COLLECTED, SOLD_OUT），确保小程序能看到后台配置的作品
        if (!StringUtils.hasText(normalized.getStatus())) {
            // 不限制状态，返回所有非草稿的作品
            normalized.setStatus(null);
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
