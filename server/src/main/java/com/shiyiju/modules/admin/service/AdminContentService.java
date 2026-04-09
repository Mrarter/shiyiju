package com.shiyiju.modules.admin.service;

import com.shiyiju.common.exception.BusinessException;
import com.shiyiju.modules.admin.dto.AdminOperationSaveDTO;
import com.shiyiju.modules.admin.dto.AdminArtistSaveDTO;
import com.shiyiju.modules.admin.dto.AdminArtworkSaveDTO;
import com.shiyiju.modules.admin.dto.AdminRemarkUpdateDTO;
import com.shiyiju.modules.admin.dto.AdminShipmentUpdateDTO;
import com.shiyiju.modules.admin.dto.AdminDistributorSaveDTO;
import com.shiyiju.modules.admin.entity.AdminArtistEntity;
import com.shiyiju.modules.admin.entity.AdminArtworkEntity;
import com.shiyiju.modules.admin.entity.AdminOperationEntity;
import com.shiyiju.modules.admin.entity.AdminOrderEntity;
import com.shiyiju.modules.admin.entity.AdminUserEntity;
import com.shiyiju.modules.admin.entity.AdminDistributorEntity;
import com.shiyiju.modules.admin.mapper.AdminMapper;
import com.shiyiju.modules.admin.vo.AdminArtistVO;
import com.shiyiju.modules.admin.vo.AdminArtworkVO;
import com.shiyiju.modules.admin.vo.AdminOperationVO;
import com.shiyiju.modules.admin.vo.AdminOrderVO;
import com.shiyiju.modules.admin.vo.AdminUserVO;
import com.shiyiju.modules.admin.vo.AdminDistributorVO;
import com.shiyiju.modules.user.entity.UserAccountEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AdminContentService {

    private final AdminMapper adminMapper;

    public AdminContentService(AdminMapper adminMapper) {
        this.adminMapper = adminMapper;
    }

    public List<AdminOperationVO> listOperations() {
        List<AdminOperationEntity> entities = adminMapper.findOperations();
        if (entities == null || entities.isEmpty()) {
            // 占位图URL
            String[] bannerUrls = {
                "https://picsum.photos/seed/banner1/750/400",
                "https://picsum.photos/seed/banner2/750/400",
                "https://picsum.photos/seed/banner3/750/400"
            };
            return List.of(
                operation(1L, "春季主视觉 Banner", "Banner", "首页主视觉", bannerUrls[0], "ENABLED", 100, "2026-04-07 14:20"),
                operation(2L, "热门藏品推荐", "热门藏品", "作品 8 个", bannerUrls[1], "ENABLED", 80, "2026-04-07 13:45"),
                operation(3L, "推荐艺术家", "推荐艺术家", "艺术家 6 位", bannerUrls[2], "DRAFT", 60, "2026-04-07 12:15")
            );
        }
        return entities.stream().map(this::toOperationVO).toList();
    }

    public AdminOperationVO createOperation(AdminOperationSaveDTO request) {
        AdminOperationEntity entity = new AdminOperationEntity();
        entity.setTitle(request.getTitle());
        entity.setType(request.getType());
        entity.setTarget(request.getTarget());
        entity.setImageUrl(request.getImageUrl());
        entity.setStatus(request.getStatus());
        entity.setSortNo(request.getSortNo());
        adminMapper.insertOperation(entity);
        // 返回原始状态值，由前端显示层负责转换
        return operation(entity.getId(), request.getTitle(), request.getType(), request.getTarget(), request.getImageUrl(), request.getStatus(), request.getSortNo(), "刚刚");
    }

    public AdminOperationVO updateOperation(Long id, AdminOperationSaveDTO request) {
        AdminOperationEntity entity = new AdminOperationEntity();
        entity.setId(id);
        entity.setTitle(request.getTitle());
        entity.setType(request.getType());
        entity.setTarget(request.getTarget());
        entity.setImageUrl(request.getImageUrl());
        entity.setStatus(request.getStatus());
        entity.setSortNo(request.getSortNo());
        if (adminMapper.updateOperation(entity) <= 0) {
            throw new BusinessException(40404, "运营配置不存在");
        }
        // 返回原始状态值，由前端显示层负责转换
        return operation(id, request.getTitle(), request.getType(), request.getTarget(), request.getImageUrl(), request.getStatus(), request.getSortNo(), "刚刚");
    }

    public void updateOperationStatus(Long id, String status) {
        if (adminMapper.updateOperationStatus(id, status) <= 0) {
            throw new BusinessException(40404, "运营配置不存在");
        }
    }

    public List<AdminArtistVO> listArtists() {
        // 占位头像URL（使用 picsum.photos 提供1:1比例头像图）
        String[] avatarUrls = {
            "https://picsum.photos/seed/artist1/200/200",
            "https://picsum.photos/seed/artist2/200/200",
            "https://picsum.photos/seed/artist3/200/200",
            "https://picsum.photos/seed/artist4/200/200",
            "https://picsum.photos/seed/artist5/200/200",
            "https://picsum.photos/seed/artist6/200/200",
            "https://picsum.photos/seed/artist7/200/200",
            "https://picsum.photos/seed/artist8/200/200",
            "https://picsum.photos/seed/artist9/200/200",
            "https://picsum.photos/seed/artist10/200/200",
            "https://picsum.photos/seed/artist11/200/200",
            "https://picsum.photos/seed/artist12/200/200",
            "https://picsum.photos/seed/artist13/200/200",
            "https://picsum.photos/seed/artist14/200/200",
            "https://picsum.photos/seed/artist15/200/200"
        };
        
        // 演示模式：始终返回 15 名艺术家
        return List.of(
            artist(3001L, "林观山", "杭州", "油画, 当代", avatarUrls[0], 12, "上线", 1),
            artist(3002L, "周岚", "上海", "版画, 新锐", avatarUrls[1], 8, "上线", 2),
            artist(3003L, "陈河", "苏州", "水墨, 山水", avatarUrls[2], 15, "上线", 3),
            artist(3004L, "李明", "北京", "油画, 抽象", avatarUrls[3], 25, "上线", 4),
            artist(3005L, "王芳", "广州", "版画, 丝网", avatarUrls[4], 18, "上线", 5),
            artist(3006L, "张伟", "成都", "水墨, 写意", avatarUrls[5], 20, "上线", 6),
            artist(3007L, "陈静", "深圳", "油画, 风景", avatarUrls[6], 14, "上线", 7),
            artist(3008L, "赵磊", "西安", "雕塑, 青铜", avatarUrls[7], 10, "上线", 8),
            artist(3009L, "孙丽", "南京", "版画, 铜版", avatarUrls[8], 16, "上线", 9),
            artist(3010L, "周杰", "武汉", "水墨, 山水", avatarUrls[9], 22, "上线", 10),
            artist(3011L, "吴敏", "重庆", "油画, 人物", avatarUrls[10], 12, "上线", 11),
            artist(3012L, "郑强", "天津", "雕塑, 陶瓷", avatarUrls[11], 8, "上线", 12),
            artist(3013L, "林立", "青岛", "油画, 静物", avatarUrls[12], 19, "上线", 13),
            artist(3014L, "黄丽", "大连", "版画, 石版", avatarUrls[13], 11, "上线", 14),
            artist(3015L, "杨帆", "厦门", "水墨, 花鸟", avatarUrls[14], 17, "上线", 15)
        );
    }

    @Transactional
    public AdminArtistVO createArtist(AdminArtistSaveDTO request) {
        UserAccountEntity user = new UserAccountEntity();
        user.setUserNo(generateUserNo("SYJ9"));
        user.setNickname("艺术家" + request.getName());
        user.setAvatarUrl(request.getAvatarUrl());
        user.setGender(0);
        user.setStatus("ENABLED");
        user.setRegisterSource("ADMIN_CREATE");
        adminMapper.insertArtistUser(user);

        AdminArtistEntity entity = new AdminArtistEntity();
        entity.setUserId(user.getId());
        entity.setName(request.getName());
        entity.setTags(request.getTags());
        entity.setAvatarUrl(request.getAvatarUrl());
        entity.setWorks(request.getWorks());
        entity.setStatus(toArtistStatus(request.getStatus()));
        adminMapper.insertArtist(entity);
        return artist(entity.getId(), request.getName(), "", request.getTags(), request.getAvatarUrl(), request.getWorks(), displayArtistStatus(entity.getStatus()), entity.getId().intValue());
    }

    public AdminArtistVO updateArtist(Long id, AdminArtistSaveDTO request) {
        // 演示模式：模拟数据直接返回更新后的数据
        if (isMockArtistId(id)) {
            return artist(id, request.getName(), "", request.getTags(), request.getAvatarUrl(), 
                request.getWorks(), displayArtistStatus(toArtistStatus(request.getStatus())), id.intValue());
        }
        
        AdminArtistEntity entity = new AdminArtistEntity();
        entity.setId(id);
        entity.setName(request.getName());
        entity.setTags(request.getTags());
        entity.setAvatarUrl(request.getAvatarUrl());
        entity.setWorks(request.getWorks());
        entity.setStatus(toArtistStatus(request.getStatus()));
        if (adminMapper.updateArtist(entity) <= 0) {
            throw new BusinessException(40404, "艺术家不存在");
        }
        adminMapper.updateArtistUserAvatar(id, "艺术家" + request.getName(), request.getAvatarUrl());
        return artist(id, request.getName(), "", request.getTags(), request.getAvatarUrl(), request.getWorks(), displayArtistStatus(entity.getStatus()), id.intValue());
    }

    public void updateArtistStatus(Long id, String status) {
        String targetStatus = toArtistStatus(status);
        // 演示模式：检查是否是模拟数据的ID范围
        if (isMockArtistId(id)) {
            return;
        }
        if (adminMapper.updateArtistStatus(id, targetStatus) <= 0) {
            throw new BusinessException(40404, "艺术家不存在");
        }
    }

    public List<AdminArtworkVO> listArtworks() {
        // 占位图URL（使用 picsum.photos 提供可靠的可访问图片）
        String[] coverUrls = {
            "https://picsum.photos/seed/art1/400/500",
            "https://picsum.photos/seed/art2/400/500",
            "https://picsum.photos/seed/art3/400/500",
            "https://picsum.photos/seed/art4/400/500",
            "https://picsum.photos/seed/art5/400/500",
            "https://picsum.photos/seed/art6/400/500",
            "https://picsum.photos/seed/art7/400/500",
            "https://picsum.photos/seed/art8/400/500",
            "https://picsum.photos/seed/art9/400/500",
            "https://picsum.photos/seed/art10/400/500",
            "https://picsum.photos/seed/art11/400/500",
            "https://picsum.photos/seed/art12/400/500",
            "https://picsum.photos/seed/art13/400/500",
            "https://picsum.photos/seed/art14/400/500",
            "https://picsum.photos/seed/art15/400/500",
            "https://picsum.photos/seed/art16/400/500",
            "https://picsum.photos/seed/art17/400/500",
            "https://picsum.photos/seed/art18/400/500",
            "https://picsum.photos/seed/art19/400/500",
            "https://picsum.photos/seed/art20/400/500",
            "https://picsum.photos/seed/art21/400/500",
            "https://picsum.photos/seed/art22/400/500",
            "https://picsum.photos/seed/art23/400/500",
            "https://picsum.photos/seed/art24/400/500",
            "https://picsum.photos/seed/art25/400/500",
            "https://picsum.photos/seed/art26/400/500",
            "https://picsum.photos/seed/art27/400/500",
            "https://picsum.photos/seed/art28/400/500",
            "https://picsum.photos/seed/art29/400/500",
            "https://picsum.photos/seed/art30/400/500",
            "https://picsum.photos/seed/art31/400/500",
            "https://picsum.photos/seed/art32/400/500",
            "https://picsum.photos/seed/art33/400/500",
            "https://picsum.photos/seed/art34/400/500",
            "https://picsum.photos/seed/art35/400/500",
            "https://picsum.photos/seed/art36/400/500",
            "https://picsum.photos/seed/art37/400/500",
            "https://picsum.photos/seed/art38/400/500",
            "https://picsum.photos/seed/art39/400/500",
            "https://picsum.photos/seed/art40/400/500",
            "https://picsum.photos/seed/art41/400/500",
            "https://picsum.photos/seed/art42/400/500",
            "https://picsum.photos/seed/art43/400/500",
            "https://picsum.photos/seed/art44/400/500",
            "https://picsum.photos/seed/art45/400/500",
            "https://picsum.photos/seed/art46/400/500",
            "https://picsum.photos/seed/art47/400/500",
            "https://picsum.photos/seed/art48/400/500",
            "https://picsum.photos/seed/art49/400/500",
            "https://picsum.photos/seed/art50/400/500"
        };
        
        // 演示模式：始终返回 50 个模拟作品
        return List.of(
            artwork(1L, 1001L, "静谧的山谷", "李明", "¥12,800", 1, "上架", "特价", "当代绘画作品", coverUrls[0]),
            artwork(2L, 1002L, "城市光影系列", "王芳", "¥6,800", 2, "上架", "", "城市风光系列", coverUrls[1]),
            artwork(3L, 1003L, "水墨山水", "张伟", "¥28,000", 1, "上架", "新品", "传统水墨画", coverUrls[2]),
            artwork(4L, 1001L, "抽象艺术 No.7", "李明", "¥15,800", 1, "上架", "特价", "抽象油画", coverUrls[3]),
            artwork(5L, 1004L, "海边日落", "陈静", "¥8,800", 1, "上架", "", "风景油画", coverUrls[4]),
            artwork(6L, 1005L, "雕塑作品 #3", "赵磊", "¥45,000", 1, "上架", "独家", "青铜雕塑", coverUrls[5]),
            artwork(7L, 1002L, "花卉系列", "王芳", "¥5,200", 3, "上架", "", "花卉版画", coverUrls[6]),
            artwork(8L, 1003L, "竹林深处", "张伟", "¥19,800", 1, "上架", "热卖", "水墨山水", coverUrls[7]),
            artwork(9L, 1006L, "星空之下", "吴敏", "¥22,800", 1, "上架", "", "星空主题画", coverUrls[8]),
            artwork(10L, 1007L, "现代都市", "郑强", "¥7,800", 2, "上架", "新品", "都市版画", coverUrls[9]),
            artwork(11L, 1008L, "秋日私语", "林立", "¥16,800", 1, "上架", "", "风景油画", coverUrls[10]),
            artwork(12L, 1009L, "铜版画 #12", "黄丽", "¥9,800", 1, "上架", "限量", "铜版画", coverUrls[11]),
            artwork(13L, 1010L, "烟雨江南", "杨帆", "¥32,000", 1, "上架", "", "水墨画", coverUrls[12]),
            artwork(14L, 1011L, "粉色幻想", "马超", "¥12,800", 1, "上架", "", "抽象油画", coverUrls[13]),
            artwork(15L, 1012L, "青铜器系列", "徐磊", "¥58,000", 1, "上架", "新品", "青铜雕塑", coverUrls[14]),
            artwork(16L, 1013L, "黑白摄影", "钟华", "¥4,200", 5, "上架", "", "摄影版画", coverUrls[15]),
            artwork(17L, 1010L, "黄山云海", "杨帆", "¥45,000", 1, "上架", "热卖", "水墨山水", coverUrls[16]),
            artwork(18L, 1014L, "晨曦微露", "曾伟", "¥15,800", 1, "上架", "", "风景油画", coverUrls[17]),
            artwork(19L, 1009L, "丝网版画", "黄丽", "¥6,800", 2, "上架", "", "丝网版画", coverUrls[18]),
            artwork(20L, 1015L, "木雕艺术", "梁勇", "¥36,000", 1, "上架", "", "木雕摆件", coverUrls[19]),
            artwork(21L, 1016L, "落日晚霞", "宋涛", "¥18,800", 1, "上架", "特价", "风景油画", coverUrls[20]),
            artwork(22L, 1017L, "古韵新风", "卢敏", "¥22,000", 1, "上架", "", "水墨画", coverUrls[21]),
            artwork(23L, 1018L, "石版画 #5", "许刚", "¥12,000", 1, "上架", "新品", "石版画", coverUrls[22]),
            artwork(24L, 1019L, "夜的旋律", "钱丽", "¥9,800", 1, "上架", "", "夜景油画", coverUrls[23]),
            artwork(25L, 1020L, "陶瓷雕塑", "蒋伟", "¥28,000", 1, "上架", "", "陶瓷艺术", coverUrls[24]),
            artwork(26L, 1021L, "春意盎然", "沈明", "¥14,800", 1, "上架", "热卖", "风景油画", coverUrls[25]),
            artwork(27L, 1022L, "书法小品", "韩静", "¥8,500", 1, "上架", "", "书法作品", coverUrls[26]),
            artwork(28L, 1023L, "综合版画", "冯强", "¥5,600", 3, "上架", "", "综合版画", coverUrls[27]),
            artwork(29L, 1024L, "雪山之巅", "曹磊", "¥32,000", 1, "上架", "", "风景油画", coverUrls[28]),
            artwork(30L, 1025L, "装置艺术", "张莉", "¥68,000", 1, "上架", "独家", "装置艺术", coverUrls[29]),
            artwork(31L, 1026L, "金色年华", "程伟", "¥18,800", 1, "上架", "", "人物油画", coverUrls[30]),
            artwork(32L, 1027L, "水乡印象", "傅丽", "¥25,800", 1, "上架", "新品", "水墨画", coverUrls[31]),
            artwork(33L, 1028L, "限量复刻", "段勇", "¥18,000", 1, "上架", "", "限量版画", coverUrls[32]),
            artwork(34L, 1029L, "梦幻泡影", "夏敏", "¥12,800", 1, "上架", "", "抽象油画", coverUrls[33]),
            artwork(35L, 1030L, "玉雕摆件", "钟刚", "¥88,000", 1, "上架", "热卖", "玉雕艺术", coverUrls[34]),
            artwork(36L, 1031L, "暮色苍茫", "乔磊", "¥15,800", 1, "上架", "", "风景油画", coverUrls[35]),
            artwork(37L, 1032L, "行书书法", "翟丽", "¥12,800", 1, "上架", "", "书法作品", coverUrls[36]),
            artwork(38L, 1033L, "数字版画", "方伟", "¥3,800", 5, "上架", "特价", "数字艺术", coverUrls[37]),
            artwork(39L, 1034L, "荷塘月色", "康静", "¥16,800", 1, "上架", "", "水墨画", coverUrls[38]),
            artwork(40L, 1035L, "铁艺雕塑", "史强", "¥42,000", 1, "上架", "", "铁艺艺术", coverUrls[39]),
            artwork(41L, 1036L, "都市喧嚣", "薛磊", "¥22,800", 1, "上架", "新品", "都市油画", coverUrls[40]),
            artwork(42L, 1037L, "草书长卷", "叶丽", "¥38,000", 1, "上架", "", "书法长卷", coverUrls[41]),
            artwork(43L, 1038L, "石版艺术", "蒋伟", "¥15,800", 1, "上架", "", "石版艺术", coverUrls[42]),
            artwork(44L, 1039L, "童趣天真", "许静", "¥8,800", 2, "上架", "", "儿童画", coverUrls[43]),
            artwork(45L, 1040L, "玻璃艺术", "陆强", "¥35,000", 1, "上架", "热卖", "玻璃雕塑", coverUrls[44]),
            artwork(46L, 1041L, "四季如歌", "杜磊", "¥19,800", 1, "上架", "", "风景油画", coverUrls[45]),
            artwork(47L, 1042L, "写意山水", "苏丽", "¥42,000", 1, "上架", "精品", "写意山水", coverUrls[46]),
            artwork(48L, 1043L, "木刻版画", "韩伟", "¥9,800", 2, "上架", "", "木刻艺术", coverUrls[47]),
            artwork(49L, 1044L, "生命之树", "杨静", "¥25,800", 1, "上架", "", "生命主题画", coverUrls[48]),
            artwork(50L, 1045L, "玉雕挂件", "朱强", "¥58,000", 1, "上架", "限量", "玉雕挂件", coverUrls[49])
        );
    }

    public AdminArtworkVO createArtwork(AdminArtworkSaveDTO request) {
        AdminArtworkEntity entity = new AdminArtworkEntity();
        entity.setArtistId(request.getArtistId());
        entity.setName(request.getName());
        entity.setPrice(request.getPrice().toPlainString());
        entity.setStock(request.getStock());
        entity.setStatus(toArtworkStatus(request.getStatus()));
        entity.setDescription(request.getDescription());
        entity.setCoverUrl(request.getCoverUrl());
        entity.setTag(generateArtworkNo());
        adminMapper.insertArtwork(entity);
        syncArtworkCover(entity.getId(), request.getCoverUrl());
        return findArtworkVO(entity.getId());
    }

    public AdminArtworkVO updateArtwork(Long id, AdminArtworkSaveDTO request) {
        // 演示模式：模拟数据直接返回更新后的数据
        if (isMockArtworkId(id)) {
            return artwork(id, request.getArtistId(), request.getName(), 
                getArtistName(request.getArtistId()),
                "¥" + request.getPrice().toPlainString(),
                request.getStock(), 
                displayArtworkStatus(toArtworkStatus(request.getStatus())),
                request.getTag(), request.getDescription(), request.getCoverUrl());
        }
        
        AdminArtworkEntity entity = new AdminArtworkEntity();
        entity.setId(id);
        entity.setArtistId(request.getArtistId());
        entity.setName(request.getName());
        entity.setPrice(request.getPrice().toPlainString());
        entity.setStock(request.getStock());
        entity.setStatus(toArtworkStatus(request.getStatus()));
        entity.setDescription(request.getDescription());
        entity.setCoverUrl(request.getCoverUrl());
        if (adminMapper.updateArtwork(entity) <= 0) {
            throw new BusinessException(40404, "作品不存在");
        }
        syncArtworkCover(id, request.getCoverUrl());
        return findArtworkVO(id);
    }

    public void updateArtworkStatus(Long id, String status) {
        String targetStatus = toArtworkStatus(status);
        // 演示模式：检查是否是模拟数据的ID范围，如果是则静默成功
        if (isMockArtworkId(id)) {
            return; // 演示模式下直接成功
        }
        if (adminMapper.updateArtworkStatus(id, targetStatus) <= 0) {
            throw new BusinessException(40404, "作品不存在");
        }
    }
    
    private boolean isMockArtworkId(Long id) {
        // 模拟作品ID范围: 1-50
        return id != null && id >= 1 && id <= 50;
    }
    
    private boolean isMockArtistId(Long id) {
        // 模拟艺术家ID范围: 3001-3015
        return id != null && id >= 3001 && id <= 3015;
    }
    
    private boolean isMockDistributorId(Long id) {
        // 模拟艺荐官ID范围: 6001-6015
        return id != null && id >= 6001 && id <= 6015;
    }

    @Transactional
    public void updateOrderShipment(Long orderId, AdminShipmentUpdateDTO request) {
        int affected = adminMapper.updateOrderShipment(orderId, request.getCompany(), request.getTrackingNo());
        if (affected <= 0) {
            affected = adminMapper.insertOrderShipment(orderId, request.getCompany(), request.getTrackingNo());
        }
        if (affected <= 0) {
            throw new BusinessException(40404, "订单不存在或发货信息更新失败");
        }
        adminMapper.updateOrderStatus(orderId, "DELIVERED");
    }

    public void updateOrderRemark(Long orderId, AdminRemarkUpdateDTO request) {
        if (adminMapper.updateOrderRemark(orderId, request.getRemark()) <= 0) {
            throw new BusinessException(40404, "订单不存在");
        }
    }

    public List<AdminDistributorVO> listDistributors() {
        // 占位头像URL
        String[] avatarUrls = {
            "https://picsum.photos/seed/dist1/200/200",
            "https://picsum.photos/seed/dist2/200/200",
            "https://picsum.photos/seed/dist3/200/200",
            "https://picsum.photos/seed/dist4/200/200",
            "https://picsum.photos/seed/dist5/200/200",
            "https://picsum.photos/seed/dist6/200/200",
            "https://picsum.photos/seed/dist7/200/200",
            "https://picsum.photos/seed/dist8/200/200",
            "https://picsum.photos/seed/dist9/200/200",
            "https://picsum.photos/seed/dist10/200/200",
            "https://picsum.photos/seed/dist11/200/200",
            "https://picsum.photos/seed/dist12/200/200",
            "https://picsum.photos/seed/dist13/200/200",
            "https://picsum.photos/seed/dist14/200/200",
            "https://picsum.photos/seed/dist15/200/200"
        };
        
        // 演示模式：始终返回 15 名艺荐官
        return List.of(
            distributor(6001L, 1001L, "木木", avatarUrls[0], "艺术推广大使", "专注推荐新锐艺术家作品", 1, "艺荐官", "ACTIVE", "正常", null, null, 5, 12, "¥1,280.00", "¥860.00", "YJ20260401", "2026-04-01"),
            distributor(6002L, 1002L, "阿泽", avatarUrls[1], "资深艺荐官", "收藏当代艺术7年", 2, "高级艺荐官", "ACTIVE", "正常", null, null, 18, 45, "¥8,560.00", "¥2,300.00", "YJ20260402", "2026-03-28"),
            distributor(6003L, 1003L, "Suki", avatarUrls[2], "入门艺荐官", "艺术爱好者", 1, "艺荐官", "ACTIVE", "正常", null, null, 3, 8, "¥520.00", "¥320.00", "YJ20260403", "2026-04-05"),
            distributor(6004L, 1004L, "小鱼", avatarUrls[3], "资深艺荐官", "当代艺术策展人", 2, "高级艺荐官", "ACTIVE", "正常", null, null, 25, 68, "¥12,800.00", "¥4,500.00", "YJ20260404", "2026-03-20"),
            distributor(6005L, 1005L, "Leo", avatarUrls[4], "艺术顾问", "资深收藏家", 3, "明星艺荐官", "ACTIVE", "正常", null, null, 42, 120, "¥28,600.00", "¥9,200.00", "YJ20260405", "2026-02-15"),
            distributor(6006L, 1006L, "Coco", avatarUrls[5], "入门艺荐官", "艺术院校学生", 1, "艺荐官", "ACTIVE", "正常", null, null, 2, 5, "¥280.00", "¥180.00", "YJ20260406", "2026-04-08"),
            distributor(6007L, 1007L, "阿杰", avatarUrls[6], "艺术推广大使", "专注版画推广", 1, "艺荐官", "ACTIVE", "正常", null, null, 8, 22, "¥2,100.00", "¥1,200.00", "YJ20260407", "2026-03-25"),
            distributor(6008L, 1008L, "Linda", avatarUrls[7], "资深艺荐官", "画廊经营者", 2, "高级艺荐官", "ACTIVE", "正常", null, null, 32, 89, "¥18,900.00", "¥6,800.00", "YJ20260408", "2026-02-28"),
            distributor(6009L, 1009L, "David", avatarUrls[8], "艺术顾问", "艺术品投资人", 3, "明星艺荐官", "ACTIVE", "正常", null, null, 58, 156, "¥45,200.00", "¥15,600.00", "YJ20260409", "2026-01-10"),
            distributor(6010L, 1010L, "米粒", avatarUrls[9], "入门艺荐官", "艺术博主", 1, "艺荐官", "ACTIVE", "正常", null, null, 4, 10, "¥680.00", "¥420.00", "YJ20260410", "2026-04-06"),
            distributor(6011L, 1011L, "Kevin", avatarUrls[10], "艺术推广大使", "专注雕塑推广", 1, "艺荐官", "ACTIVE", "正常", null, null, 12, 35, "¥4,500.00", "¥2,800.00", "YJ20260411", "2026-03-15"),
            distributor(6012L, 1012L, "Amy", avatarUrls[11], "资深艺荐官", "艺术教育者", 2, "高级艺荐官", "ACTIVE", "正常", null, null, 28, 72, "¥15,800.00", "¥5,600.00", "YJ20260412", "2026-02-20"),
            distributor(6013L, 1013L, "Tom", avatarUrls[12], "入门艺荐官", "收藏爱好者", 1, "艺荐官", "INACTIVE", "禁用", null, null, 1, 3, "¥150.00", "¥0.00", "YJ20260413", "2026-04-09"),
            distributor(6014L, 1014L, "小雨", avatarUrls[13], "艺术顾问", "资深策展人", 3, "明星艺荐官", "ACTIVE", "正常", null, null, 65, 180, "¥52,000.00", "¥18,500.00", "YJ20260414", "2026-01-05"),
            distributor(6015L, 1015L, "Jack", avatarUrls[14], "艺术推广大使", "专注水墨推广", 1, "艺荐官", "ACTIVE", "正常", null, null, 9, 26, "¥3,200.00", "¥1,900.00", "YJ20260415", "2026-03-30")
        );
    }

    @Transactional
    public AdminDistributorVO createDistributor(AdminDistributorSaveDTO request) {
        AdminUserEntity user = adminMapper.findUsers().stream()
            .filter(u -> u.getId().equals(request.getUserId()))
            .findFirst()
            .orElseThrow(() -> new BusinessException(40404, "用户不存在"));

        AdminDistributorEntity entity = new AdminDistributorEntity();
        entity.setUserId(request.getUserId());
        entity.setDisplayName(request.getDisplayName());
        entity.setBio(request.getBio());
        entity.setTeamLevel(request.getTeamLevel() != null ? request.getTeamLevel() : 1);
        entity.setStatus(toDistributorStatus(request.getStatus()));
        entity.setParentDistributorId(request.getParentDistributorId());
        adminMapper.insertDistributor(entity);

        adminMapper.insertUserRoleRelation(request.getUserId(), "DISTRIBUTOR");

        String invitationCode = "YJ" + System.currentTimeMillis();
        adminMapper.insertInvitationCode(request.getUserId(), invitationCode, "DISTRIBUTOR");

        return distributor(
            entity.getId(), user.getId(), user.getNickname(), user.getAvatarUrl(),
            request.getDisplayName(), request.getBio(), entity.getTeamLevel(),
            getTeamLevelName(entity.getTeamLevel()), entity.getStatus(), "正常",
            request.getParentDistributorId(), null, 0, 0, "¥0.00", "¥0.00", invitationCode, "刚刚"
        );
    }

    public AdminDistributorVO updateDistributor(Long id, AdminDistributorSaveDTO request) {
        // 演示模式：模拟数据直接返回更新后的数据
        if (isMockDistributorId(id)) {
            return distributor(id, null, request.getDisplayName(), null,
                request.getDisplayName(), request.getBio(), 
                request.getTeamLevel() != null ? request.getTeamLevel() : 1,
                getTeamLevelName(request.getTeamLevel()),
                toDistributorStatus(request.getStatus()), displayDistributorStatus(toDistributorStatus(request.getStatus())),
                request.getParentDistributorId(), null, 0, 0, "¥0.00", "¥0.00", "", "刚刚");
        }
        
        AdminDistributorEntity entity = new AdminDistributorEntity();
        entity.setId(id);
        entity.setDisplayName(request.getDisplayName());
        entity.setBio(request.getBio());
        entity.setTeamLevel(request.getTeamLevel() != null ? request.getTeamLevel() : 1);
        entity.setStatus(toDistributorStatus(request.getStatus()));
        entity.setParentDistributorId(request.getParentDistributorId());
        if (adminMapper.updateDistributor(entity) <= 0) {
            throw new BusinessException(40404, "艺荐官不存在");
        }
        return findDistributorVO(id);
    }

    public void updateDistributorStatus(Long id, String status) {
        String targetStatus = toDistributorStatus(status);
        // 演示模式：检查是否是模拟数据的ID范围
        if (isMockDistributorId(id)) {
            return;
        }
        if (adminMapper.updateDistributorStatus(id, targetStatus) <= 0) {
            throw new BusinessException(40404, "艺荐官不存在");
        }
    }

    public List<AdminUserVO> listUsers() {
        List<AdminUserEntity> entities = adminMapper.findUsers();
        if (entities == null || entities.isEmpty()) {
            // 占位头像URL
            String[] avatarUrls = {
                "https://picsum.photos/seed/user1/200/200",
                "https://picsum.photos/seed/user2/200/200",
                "https://picsum.photos/seed/user3/200/200"
            };
            return List.of(
                user(1001L, "木木", "SYJ10021", "女", "正常", "2026-04-07 15:08", avatarUrls[0]),
                user(1002L, "阿泽", "SYJ10022", "男", "正常", "2026-04-07 14:41", avatarUrls[1]),
                user(1003L, "Suki", "SYJ10023", "未知", "禁用", "2026-04-06 20:15", avatarUrls[2])
            );
        }
        return entities.stream().map(this::toUserVO).toList();
    }

    public void updateUserStatus(Long id, String status) {
        String targetStatus = "禁用".equals(status) || "DISABLED".equalsIgnoreCase(status) ? "DISABLED" : "ENABLED";
        if (adminMapper.updateUserStatus(id, targetStatus) <= 0) {
            throw new BusinessException(40404, "用户不存在");
        }
    }

    public List<AdminOrderVO> listOrders() {
        List<AdminOrderEntity> entities = adminMapper.findOrders();
        if (entities == null || entities.isEmpty()) {
            return List.of(
                order(5001L, "SYJ202604070001", "木木", "春山可望", "¥12,800", "待发货", "已支付", "未发货"),
                order(5002L, "SYJ202604070002", "阿泽", "潮汐笔记", "¥8,600", "待支付", "待支付", "未发货"),
                order(5003L, "SYJ202604060018", "Suki", "园林记忆", "¥16,500", "已完成", "已支付", "已发货")
            );
        }
        return entities.stream().map(this::toOrderVO).toList();
    }

    private AdminOperationVO toOperationVO(AdminOperationEntity entity) {
        return operation(entity.getId(), entity.getTitle(), entity.getType(), entity.getTarget(), entity.getImageUrl(), entity.getStatus(), entity.getSortNo(), entity.getUpdatedAt());
    }

    private String displayOperationStatus(String status) {
        return switch (status) {
            case "ENABLED" -> "启用";
            case "DRAFT" -> "草稿";
            default -> "停用";
        };
    }

    private AdminArtistVO toArtistVO(AdminArtistEntity entity) {
        return artist(entity.getId(), entity.getName(), entity.getCity(), entity.getTags(), entity.getAvatarUrl(), entity.getWorks(), entity.getStatus(), entity.getSort());
    }

    private AdminArtworkVO toArtworkVO(AdminArtworkEntity entity) {
        return artwork(entity.getId(), entity.getArtistId(), entity.getName(), entity.getArtist(), entity.getPrice(), entity.getStock(), entity.getStatus(), entity.getTag(), entity.getDescription(), entity.getCoverUrl());
    }

    private AdminUserVO toUserVO(AdminUserEntity entity) {
        return user(entity.getId(), entity.getNickname(), entity.getUserNo(), entity.getGender(), entity.getStatus(), entity.getLastLogin(), entity.getAvatarUrl());
    }

    private AdminOrderVO toOrderVO(AdminOrderEntity entity) {
        return order(entity.getId(), entity.getOrderNo(), entity.getUser(), entity.getArtwork(), entity.getAmount(), entity.getStatus(), entity.getPayStatus(), entity.getShipStatus());
    }

    private AdminDistributorVO toDistributorVO(AdminDistributorEntity entity) {
        return distributor(
            entity.getId(), entity.getUserId(), entity.getNickname(), entity.getAvatarUrl(),
            entity.getDisplayName(), entity.getBio(), entity.getTeamLevel(),
            getTeamLevelName(entity.getTeamLevel()), entity.getStatus(),
            displayDistributorStatus(entity.getStatus()),
            entity.getParentDistributorId(), entity.getParentDistributorName(),
            entity.getDirectCount() != null ? entity.getDirectCount() : 0,
            entity.getTeamCount() != null ? entity.getTeamCount() : 0,
            entity.getTotalCommission() != null ? entity.getTotalCommission() : "¥0.00",
            entity.getAvailableCommission() != null ? entity.getAvailableCommission() : "¥0.00",
            entity.getInvitationCode() != null ? entity.getInvitationCode() : "",
            entity.getCreatedAt()
        );
    }

    private AdminDistributorVO distributor(Long id, Long userId, String nickname, String avatarUrl,
            String displayName, String bio, Integer teamLevel, String teamLevelName,
            String status, String statusName, Long parentDistributorId, String parentDistributorName,
            Integer directCount, Integer teamCount, String totalCommission, String availableCommission,
            String invitationCode, String createdAt) {
        AdminDistributorVO item = new AdminDistributorVO();
        item.setId(id);
        item.setUserId(userId);
        item.setNickname(nickname);
        item.setAvatarUrl(avatarUrl);
        item.setDisplayName(displayName);
        item.setBio(bio);
        item.setTeamLevel(teamLevel);
        item.setTeamLevelName(teamLevelName);
        item.setStatus(status);
        item.setStatusName(statusName);
        item.setParentDistributorId(parentDistributorId);
        item.setParentDistributorName(parentDistributorName);
        item.setDirectCount(directCount);
        item.setTeamCount(teamCount);
        item.setTotalCommission(totalCommission);
        item.setAvailableCommission(availableCommission);
        item.setInvitationCode(invitationCode);
        item.setCreatedAt(createdAt);
        return item;
    }

    private String getTeamLevelName(Integer level) {
        if (level == null) return "艺荐官";
        return switch (level) {
            case 1 -> "艺荐官";
            case 2 -> "高级艺荐官";
            case 3 -> "资深艺荐官";
            default -> "艺荐官";
        };
    }

    private String toDistributorStatus(String status) {
        if ("正常".equals(status) || "ACTIVE".equalsIgnoreCase(status) || "启用".equals(status)) {
            return "ACTIVE";
        }
        return "INACTIVE";
    }

    private String displayDistributorStatus(String status) {
        if ("ACTIVE".equalsIgnoreCase(status)) {
            return "正常";
        }
        return "禁用";
    }

    private AdminDistributorVO findDistributorVO(Long id) {
        return listDistributors().stream()
            .filter(item -> id.equals(item.getId()))
            .findFirst()
            .orElseThrow(() -> new BusinessException(40404, "艺荐官不存在"));
    }

    private AdminOperationVO operation(Long id, String title, String type, String target, String imageUrl, String status, Integer sortNo, String updatedAt) {
        AdminOperationVO item = new AdminOperationVO();
        item.setId(id);
        item.setTitle(title);
        item.setType(type);
        item.setTarget(target);
        item.setImageUrl(imageUrl);
        item.setStatus(status);
        item.setSortNo(sortNo);
        item.setUpdatedAt(updatedAt);
        return item;
    }

    private AdminArtistVO artist(Long id, String name, String city, String tags, String avatarUrl, Integer works, String status, Integer sort) {
        AdminArtistVO item = new AdminArtistVO();
        item.setId(id);
        item.setName(name);
        item.setCity(city);
        item.setTags(tags);
        item.setAvatarUrl(avatarUrl);
        item.setWorks(works);
        item.setStatus(status);
        item.setSort(sort);
        return item;
    }

    private AdminArtworkVO artwork(Long id, Long artistId, String name, String artist, String price, Integer stock, String status, String tag, String description, String coverUrl) {
        AdminArtworkVO item = new AdminArtworkVO();
        item.setId(id);
        item.setArtistId(artistId);
        item.setName(name);
        item.setArtist(artist);
        item.setPrice(price);
        item.setStock(stock);
        item.setStatus(status);
        item.setTag(tag);
        item.setDescription(description);
        item.setCoverUrl(coverUrl);
        return item;
    }

    private AdminUserVO user(Long id, String nickname, String userNo, String gender, String status, String lastLogin, String avatarUrl) {
        AdminUserVO item = new AdminUserVO();
        item.setId(id);
        item.setNickname(nickname);
        item.setUserNo(userNo);
        item.setGender(gender);
        item.setStatus(status);
        item.setLastLogin(lastLogin);
        item.setAvatarUrl(avatarUrl);
        return item;
    }

    private AdminOrderVO order(Long id, String orderNo, String user, String artwork, String amount, String status, String payStatus, String shipStatus) {
        AdminOrderVO item = new AdminOrderVO();
        item.setId(id);
        item.setOrderNo(orderNo);
        item.setUser(user);
        item.setArtwork(artwork);
        item.setAmount(amount);
        item.setStatus(status);
        item.setPayStatus(payStatus);
        item.setShipStatus(shipStatus);
        return item;
    }

    private AdminArtworkVO findArtworkVO(Long id) {
        return listArtworks().stream()
            .filter(item -> id.equals(item.getId()))
            .findFirst()
            .orElseThrow(() -> new BusinessException(40404, "作品不存在"));
    }

    private String toArtistStatus(String status) {
        return "ONLINE".equalsIgnoreCase(status) || "ACTIVE".equalsIgnoreCase(status) ? "ACTIVE" : "INACTIVE";
    }

    private String displayArtistStatus(String status) {
        return "ACTIVE".equalsIgnoreCase(status) ? "上线" : "下线";
    }

    private void syncArtworkCover(Long artworkId, String coverUrl) {
        if (artworkId == null) {
            return;
        }
        if (coverUrl == null || coverUrl.isBlank()) {
            adminMapper.deleteArtworkCover(artworkId);
            return;
        }
        if (adminMapper.updateArtworkCover(artworkId, coverUrl.trim()) <= 0) {
            adminMapper.insertArtworkCover(artworkId, coverUrl.trim());
        }
    }

    private String toArtworkStatus(String status) {
        return switch (status == null ? "" : status.toUpperCase()) {
            case "ONLINE", "PUBLISHED", "上架" -> "PUBLISHED";
            case "OFFLINE", "OFF_SHELF", "下架" -> "OFF_SHELF";
            case "COLLECTED", "已收藏" -> "COLLECTED";
            case "SOLD_OUT", "售罄" -> "SOLD_OUT";
            default -> "DRAFT";
        };
    }
    
    private String displayArtworkStatus(String status) {
        return switch (status) {
            case "PUBLISHED" -> "上架";
            case "OFF_SHELF" -> "下架";
            case "COLLECTED" -> "已收藏";
            case "SOLD_OUT" -> "售罄";
            default -> "草稿";
        };
    }
    
    private String getArtistName(Long artistId) {
        if (artistId == null) return "";
        // 模拟艺术家ID到名字的映射
        java.util.Map<Long, String> artistNames = java.util.Map.of(
            1001L, "李明", 1002L, "王芳", 1003L, "张伟", 1004L, "陈静",
            1005L, "赵磊", 1006L, "吴敏", 1007L, "郑强", 1008L, "林立",
            1009L, "黄丽", 1010L, "杨帆", 1011L, "马超", 1012L, "徐磊",
            1013L, "钟华", 1014L, "曾伟", 1015L, "梁勇", 1016L, "宋涛",
            1017L, "卢敏", 1018L, "许刚", 1019L, "钱丽", 1020L, "蒋伟",
            1021L, "沈明", 1022L, "韩静", 1023L, "冯强", 1024L, "曹磊",
            1025L, "张莉", 1026L, "程伟", 1027L, "傅丽", 1028L, "段勇",
            1029L, "夏敏", 1030L, "钟刚", 1031L, "乔磊", 1032L, "翟丽",
            1033L, "方伟", 1034L, "康静", 1035L, "史强", 1036L, "薛磊",
            1037L, "叶丽", 1038L, "蒋伟", 1039L, "许静", 1040L, "陆强",
            1041L, "杜磊", 1042L, "苏丽", 1043L, "韩伟", 1044L, "杨静",
            1045L, "朱强"
        );
        return artistNames.getOrDefault(artistId, "");
    }

    private String generateUserNo(String prefix) {
        return prefix + String.valueOf(System.currentTimeMillis()).substring(3);
    }

    private String generateArtworkNo() {
        return "ART" + String.valueOf(System.currentTimeMillis());
    }
}
