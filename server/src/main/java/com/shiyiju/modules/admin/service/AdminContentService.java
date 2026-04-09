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
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminContentService {

    private final AdminMapper adminMapper;
    
    // 可变的模拟数据列表
    private static List<AdminArtistVO> mockArtists = new ArrayList<>();
    private static List<AdminArtworkVO> mockArtworks = new ArrayList<>();
    private static boolean mockDataInitialized = false;

    public AdminContentService(AdminMapper adminMapper) {
        this.adminMapper = adminMapper;
    }
    
    // 初始化模拟数据
    private void initMockData() {
        if (mockDataInitialized) return;
        synchronized (AdminContentService.class) {
            if (mockDataInitialized) return;
            initMockArtists();
            initMockArtworks();
            mockDataInitialized = true;
        }
    }
    
    private void initMockArtists() {
        Object[][] artists = {
            {"林观山", "杭州", "油画, 当代", "5B8C5A", "中央美术学院", "2020年青年艺术展金奖", 38},
            {"周岚", "上海", "版画, 新锐", "8B5A8B", "中国美术学院", "2022年新锐艺术家奖", 32},
            {"陈河", "苏州", "水墨, 山水", "5A7B8C", "南京艺术学院", "2019年当代水墨提名奖", 45},
            {"李明", "北京", "油画, 抽象", "8C7A5B", "清华大学美术学院", "", 40},
            {"王芳", "广州", "版画, 丝网", "8B5B5B", "广州美术学院", "2021年青年版画家奖", 35},
            {"张伟", "成都", "水墨, 写意", "5B8B8B", "四川美术学院", "", 50},
            {"陈静", "深圳", "油画, 风景", "7A5B8C", "湖北美术学院", "2023年风景画大展银奖", 42},
            {"赵磊", "西安", "雕塑, 青铜", "8C5A6A", "西安美术学院", "", 48},
            {"孙丽", "南京", "版画, 铜版", "5A6A8C", "中央美术学院", "2018年全国美展入选", 36},
            {"周杰", "武汉", "水墨, 山水", "8C7A6A", "湖北美术学院", "", 44},
            {"吴敏", "重庆", "油画, 人物", "6A8C5A", "四川美术学院", "2022年人物画展优秀奖", 33},
            {"郑强", "天津", "雕塑, 陶瓷", "8C5A8C", "天津美术学院", "", 52},
            {"林立", "青岛", "油画, 静物", "5A8C7A", "山东艺术学院", "", 39},
            {"黄丽", "大连", "版画, 石版", "8B7A5A", "鲁迅美术学院", "2020年石版画展提名", 37},
            {"杨帆", "厦门", "水墨, 花鸟", "5B5B8C", "福建师范大学", "", 41}
        };
        mockArtists = new ArrayList<>();
        for (int i = 0; i < artists.length; i++) {
            mockArtists.add(artist(3001L + i, (String)artists[i][0], (String)artists[i][1], (String)artists[i][2], 
                genAvatarUrl((String)artists[i][0], (String)artists[i][3]), 10 + i, "上线", i + 1, 
                artists[i][0] + "是当代知名的艺术家，作品风格独特，在国内外多次举办个人展览。", 
                (String)artists[i][4], (String)artists[i][5], (Integer)artists[i][6]));
        }
    }
    
    private void initMockArtworks() {
        String[] coverUrls = new String[50];
        for (int i = 0; i < 50; i++) {
            coverUrls[i] = "https://picsum.photos/seed/art" + (i + 1) + "/400/500";
        }
        String[][] artworks = {
            {"静谧的山谷", "1001", "李明", "¥12,800", "特价"}, {"城市光影系列", "1002", "王芳", "¥6,800", ""},
            {"水墨山水", "1003", "张伟", "¥28,000", "新品"}, {"抽象艺术 No.7", "1001", "李明", "¥15,800", "特价"},
            {"海边日落", "1004", "陈静", "¥8,800", ""}, {"雕塑作品 #3", "1005", "赵磊", "¥45,000", "独家"},
            {"花卉系列", "1002", "王芳", "¥5,200", ""}, {"竹林深处", "1003", "张伟", "¥19,800", "热卖"},
            {"星空之下", "1006", "吴敏", "¥22,800", ""}, {"现代都市", "1007", "郑强", "¥7,800", "新品"},
            {"秋日私语", "1008", "林立", "¥16,800", ""}, {"铜版画 #12", "1009", "黄丽", "¥9,800", "限量"},
            {"烟雨江南", "1010", "杨帆", "¥32,000", ""}, {"粉色幻想", "1011", "马超", "¥12,800", ""},
            {"青铜器系列", "1012", "徐磊", "¥58,000", "新品"}, {"黑白摄影", "1013", "钟华", "¥4,200", ""},
            {"黄山云海", "1010", "杨帆", "¥45,000", "热卖"}, {"晨曦微露", "1014", "曾伟", "¥15,800", ""},
            {"丝网版画", "1009", "黄丽", "¥6,800", ""}, {"木雕艺术", "1015", "梁勇", "¥36,000", ""}
        };
        mockArtworks = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            int artistIdx = i < artworks.length ? i : artworks.length - 1;
            mockArtworks.add(artwork(1L + i, Long.parseLong(artworks[artistIdx][1]), artworks[artistIdx][0],
                artworks[artistIdx][2], artworks[artistIdx][3], 1, "上架", artworks[artistIdx][4],
                "作品描述", coverUrls[i]));
        }
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
        initMockData();
        List<AdminArtistEntity> entities = adminMapper.findArtists();
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>(mockArtists);
        }
        return entities.stream().map(this::toArtistVO).collect(java.util.stream.Collectors.toList());
    }
    
    // 生成艺术家头像URL（使用ui-avatars带名字的彩色头像）
    private String genAvatarUrl(String name, String bgColor) {
        try {
            return "https://ui-avatars.com/api/?name=" + java.net.URLEncoder.encode(name, "UTF-8") 
                   + "&background=" + bgColor + "&color=fff&size=128&font-size=0.4&bold=true&rounded=true";
        } catch (Exception e) {
            return "https://ui-avatars.com/api/?name=" + name + "&background=5B8C5A&color=fff&size=128&rounded=true";
        }
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
        entity.setCity(request.getCity());
        entity.setTags(request.getTags());
        entity.setAvatarUrl(request.getAvatarUrl());
        entity.setWorks(request.getWorks());
        entity.setStatus(toArtistStatus(request.getStatus()));
        entity.setSort(request.getSort() != null ? request.getSort() : 0);
        entity.setBio(request.getBio());
        entity.setGraduatedFrom(request.getGraduatedFrom());
        entity.setAwards(request.getAwards());
        entity.setAge(request.getAge());
        adminMapper.insertArtist(entity);
        return artist(entity.getId(), request.getName(), request.getCity() != null ? request.getCity() : "", request.getTags(), request.getAvatarUrl(), request.getWorks(), displayArtistStatus(entity.getStatus()), entity.getSort(), request.getBio(), request.getGraduatedFrom(), request.getAwards(), request.getAge());
    }

    public AdminArtistVO updateArtist(Long id, AdminArtistSaveDTO request) {
        initMockData();
        // 演示模式：更新mock数据并返回
        if (isMockArtistId(id)) {
            String city = request.getCity() != null ? request.getCity() : "";
            Integer sort = request.getSort() != null ? request.getSort() : id.intValue();
            AdminArtistVO updated = artist(id, request.getName(), city, request.getTags(), request.getAvatarUrl(), 
                request.getWorks(), displayArtistStatus(toArtistStatus(request.getStatus())), sort, request.getBio(), request.getGraduatedFrom(), request.getAwards(), request.getAge());
            // 更新mock列表中的数据
            for (int i = 0; i < mockArtists.size(); i++) {
                if (mockArtists.get(i).getId().equals(id)) {
                    mockArtists.set(i, updated);
                    break;
                }
            }
            return updated;
        }
        
        AdminArtistEntity entity = new AdminArtistEntity();
        entity.setId(id);
        entity.setName(request.getName());
        entity.setCity(request.getCity());
        entity.setTags(request.getTags());
        entity.setAvatarUrl(request.getAvatarUrl());
        entity.setWorks(request.getWorks());
        entity.setStatus(toArtistStatus(request.getStatus()));
        entity.setSort(request.getSort() != null ? request.getSort() : id.intValue());
        entity.setBio(request.getBio());
        entity.setGraduatedFrom(request.getGraduatedFrom());
        entity.setAwards(request.getAwards());
        entity.setAge(request.getAge());
        if (adminMapper.updateArtist(entity) <= 0) {
            throw new BusinessException(40404, "艺术家不存在");
        }
        adminMapper.updateArtistUserAvatar(id, "艺术家" + request.getName(), request.getAvatarUrl());
        return artist(id, request.getName(), request.getCity() != null ? request.getCity() : "", request.getTags(), request.getAvatarUrl(), request.getWorks(), displayArtistStatus(entity.getStatus()), entity.getSort(), request.getBio(), request.getGraduatedFrom(), request.getAwards(), request.getAge());
    }

    public void updateArtistStatus(Long id, String status) {
        initMockData();
        String targetStatus = toArtistStatus(status);
        // 演示模式：更新mock数据
        if (isMockArtistId(id)) {
            for (AdminArtistVO artist : mockArtists) {
                if (artist.getId().equals(id)) {
                    artist.setStatus(displayArtistStatus(targetStatus));
                    break;
                }
            }
            return;
        }
        if (adminMapper.updateArtistStatus(id, targetStatus) <= 0) {
            throw new BusinessException(40404, "艺术家不存在");
        }
    }

    public List<AdminArtworkVO> listArtworks() {
        initMockData();
        List<AdminArtworkEntity> entities = adminMapper.findArtworks();
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>(mockArtworks);
        }
        return entities.stream().map(this::toArtworkVO).collect(java.util.stream.Collectors.toList());
    }

    public AdminArtworkVO createArtwork(AdminArtworkSaveDTO request) {
        initMockData();
        AdminArtworkEntity entity = new AdminArtworkEntity();
        entity.setArtistId(request.getArtistId());
        entity.setName(request.getName());
        entity.setCategory(request.getCategory());
        entity.setMaterial(request.getMaterial());
        entity.setCreationYear(request.getCreationYear());
        entity.setWidthCm(request.getWidthCm());
        entity.setHeightCm(request.getHeightCm());
        entity.setDepthCm(request.getDepthCm());
        entity.setPrice(request.getPrice().toPlainString());
        entity.setStock(request.getStock());
        entity.setStatus(toArtworkStatus(request.getStatus()));
        entity.setAdminWeight(request.getAdminWeight());
        entity.setDescription(request.getDescription());
        entity.setCoverUrl(request.getCoverUrl());
        entity.setTag(generateArtworkNo());
        adminMapper.insertArtwork(entity);
        syncArtworkCover(entity.getId(), request.getCoverUrl());
        // 直接构建返回VO
        return artwork(entity.getId(), request.getArtistId(), request.getName(),
            getArtistName(request.getArtistId()),
            "¥" + request.getPrice().toPlainString(),
            request.getStock(),
            displayArtworkStatus(toArtworkStatus(request.getStatus())),
            "", request.getDescription(), request.getCoverUrl());
    }

    public AdminArtworkVO updateArtwork(Long id, AdminArtworkSaveDTO request) {
        initMockData();
        // 演示模式：更新mock数据
        if (isMockArtworkId(id)) {
            AdminArtworkVO updated = artwork(id, request.getArtistId(), request.getName(), 
                getArtistName(request.getArtistId()),
                "¥" + request.getPrice().toPlainString(),
                request.getStock(), 
                displayArtworkStatus(toArtworkStatus(request.getStatus())),
                "", request.getDescription(), request.getCoverUrl());
            // 更新mock列表中的数据
            for (int i = 0; i < mockArtworks.size(); i++) {
                if (mockArtworks.get(i).getId().equals(id)) {
                    mockArtworks.set(i, updated);
                    break;
                }
            }
            return updated;
        }
        
        AdminArtworkEntity entity = new AdminArtworkEntity();
        entity.setId(id);
        entity.setArtistId(request.getArtistId());
        entity.setName(request.getName());
        entity.setCategory(request.getCategory());
        entity.setMaterial(request.getMaterial());
        entity.setCreationYear(request.getCreationYear());
        entity.setWidthCm(request.getWidthCm());
        entity.setHeightCm(request.getHeightCm());
        entity.setDepthCm(request.getDepthCm());
        entity.setPrice(request.getPrice().toPlainString());
        entity.setStock(request.getStock());
        entity.setStatus(toArtworkStatus(request.getStatus()));
        entity.setAdminWeight(request.getAdminWeight());
        entity.setDescription(request.getDescription());
        entity.setCoverUrl(request.getCoverUrl());
        if (adminMapper.updateArtwork(entity) <= 0) {
            throw new BusinessException(40404, "作品不存在");
        }
        syncArtworkCover(id, request.getCoverUrl());
        // 非模拟数据：直接构建返回VO
        return artwork(id, request.getArtistId(), request.getName(),
            getArtistName(request.getArtistId()),
            "¥" + request.getPrice().toPlainString(),
            request.getStock(),
            displayArtworkStatus(toArtworkStatus(request.getStatus())),
            "", request.getDescription(), request.getCoverUrl());
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
        return artist(entity.getId(), entity.getName(), entity.getCity(), entity.getTags(), entity.getAvatarUrl(), entity.getWorks(), entity.getStatus(), entity.getSort(), entity.getBio(), entity.getGraduatedFrom(), entity.getAwards(), entity.getAge());
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

    private AdminArtistVO artist(Long id, String name, String city, String tags, String avatarUrl, Integer works, String status, Integer sort, String bio, String graduatedFrom, String awards, Integer age) {
        AdminArtistVO item = new AdminArtistVO();
        item.setId(id);
        item.setName(name);
        item.setCity(city);
        item.setTags(tags);
        item.setAvatarUrl(avatarUrl);
        item.setWorks(works);
        item.setStatus(status);
        item.setSort(sort);
        item.setBio(bio);
        item.setGraduatedFrom(graduatedFrom);
        item.setAwards(awards);
        item.setAge(age);
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
        java.util.Map<Long, String> artistNames = new java.util.HashMap<>();
        artistNames.put(1001L, "李明"); artistNames.put(1002L, "王芳"); artistNames.put(1003L, "张伟"); artistNames.put(1004L, "陈静");
        artistNames.put(1005L, "赵磊"); artistNames.put(1006L, "吴敏"); artistNames.put(1007L, "郑强"); artistNames.put(1008L, "林立");
        artistNames.put(1009L, "黄丽"); artistNames.put(1010L, "杨帆"); artistNames.put(1011L, "马超"); artistNames.put(1012L, "徐磊");
        artistNames.put(1013L, "钟华"); artistNames.put(1014L, "曾伟"); artistNames.put(1015L, "梁勇"); artistNames.put(1016L, "宋涛");
        artistNames.put(1017L, "卢敏"); artistNames.put(1018L, "许刚"); artistNames.put(1019L, "钱丽"); artistNames.put(1020L, "蒋伟");
        artistNames.put(1021L, "沈明"); artistNames.put(1022L, "韩静"); artistNames.put(1023L, "冯强"); artistNames.put(1024L, "曹磊");
        artistNames.put(1025L, "张莉"); artistNames.put(1026L, "程伟"); artistNames.put(1027L, "傅丽"); artistNames.put(1028L, "段勇");
        artistNames.put(1029L, "夏敏"); artistNames.put(1030L, "钟刚"); artistNames.put(1031L, "乔磊"); artistNames.put(1032L, "翟丽");
        artistNames.put(1033L, "方伟"); artistNames.put(1034L, "康静"); artistNames.put(1035L, "史强"); artistNames.put(1036L, "薛磊");
        artistNames.put(1037L, "叶丽"); artistNames.put(1038L, "蒋伟"); artistNames.put(1039L, "许静"); artistNames.put(1040L, "陆强");
        artistNames.put(1041L, "杜磊"); artistNames.put(1042L, "苏丽"); artistNames.put(1043L, "韩伟"); artistNames.put(1044L, "杨静");
        artistNames.put(1045L, "朱强");
        return artistNames.getOrDefault(artistId, "");
    }

    private String generateUserNo(String prefix) {
        return prefix + String.valueOf(System.currentTimeMillis()).substring(3);
    }

    private String generateArtworkNo() {
        return "ART" + String.valueOf(System.currentTimeMillis());
    }
}
