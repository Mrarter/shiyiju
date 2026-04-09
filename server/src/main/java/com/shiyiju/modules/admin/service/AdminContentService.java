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
            return List.of(
                operation(1L, "春季主视觉 Banner", "Banner", "首页主视觉", null, "启用", "2026-04-07 14:20"),
                operation(2L, "热门藏品推荐", "热门藏品", "作品 8 个", null, "启用", "2026-04-07 13:45"),
                operation(3L, "推荐艺术家", "推荐艺术家", "艺术家 6 位", null, "草稿", "2026-04-07 12:15")
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
        return operation(entity.getId(), request.getTitle(), request.getType(), request.getTarget(), request.getImageUrl(), displayOperationStatus(request.getStatus()), "刚刚");
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
        return operation(id, request.getTitle(), request.getType(), request.getTarget(), request.getImageUrl(), displayOperationStatus(request.getStatus()), "刚刚");
    }

    public void updateOperationStatus(Long id, String status) {
        if (adminMapper.updateOperationStatus(id, status) <= 0) {
            throw new BusinessException(40404, "运营配置不存在");
        }
    }

    public List<AdminArtistVO> listArtists() {
        List<AdminArtistEntity> entities = adminMapper.findArtists();
        if (entities == null || entities.isEmpty()) {
            return List.of(
                artist(3001L, "林观山", "杭州", "油画, 当代", null, 12, "上线", 1),
                artist(3002L, "周岚", "上海", "版画, 新锐", null, 8, "上线", 2),
                artist(3003L, "陈河", "苏州", "水墨, 山水", null, 15, "下线", 3)
            );
        }
        return entities.stream().map(this::toArtistVO).toList();
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
        if (adminMapper.updateArtistStatus(id, targetStatus) <= 0) {
            throw new BusinessException(40404, "艺术家不存在");
        }
    }

    public List<AdminArtworkVO> listArtworks() {
        List<AdminArtworkEntity> entities = adminMapper.findArtworks();
        if (entities == null || entities.isEmpty()) {
            return List.of(
                artwork(4001L, 3001L, "春山可望", "林观山", "¥12,800", 1, "上架", "热门藏品", "", null),
                artwork(4002L, 3002L, "潮汐笔记", "周岚", "¥8,600", 2, "上架", "正在升值", "", null),
                artwork(4003L, 3003L, "园林记忆", "陈河", "¥16,500", 1, "下架", "无", "", null)
            );
        }
        return entities.stream().map(this::toArtworkVO).toList();
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
        if (adminMapper.updateArtworkStatus(id, targetStatus) <= 0) {
            throw new BusinessException(40404, "作品不存在");
        }
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
        List<AdminDistributorEntity> entities = adminMapper.findDistributors();
        if (entities == null || entities.isEmpty()) {
            return List.of(
                distributor(6001L, 1001L, "木木", null, "艺术推广大使", "专注推荐新锐艺术家作品", 1, "艺荐官", "ACTIVE", "正常", null, null, 5, 12, "¥1,280.00", "¥860.00", "YJ20260401", "2026-04-01"),
                distributor(6002L, 1002L, "阿泽", null, "资深艺荐官", "收藏当代艺术7年", 2, "高级艺荐官", "ACTIVE", "正常", null, null, 18, 45, "¥8,560.00", "¥2,300.00", "YJ20260402", "2026-03-28"),
                distributor(6003L, 1003L, "Suki", null, "入门艺荐官", "艺术爱好者", 1, "艺荐官", "INACTIVE", "禁用", null, null, 2, 5, "¥320.00", "¥0.00", "YJ20260403", "2026-04-05")
            );
        }
        return entities.stream().map(this::toDistributorVO).toList();
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
        if (adminMapper.updateDistributorStatus(id, targetStatus) <= 0) {
            throw new BusinessException(40404, "艺荐官不存在");
        }
    }

    public List<AdminUserVO> listUsers() {
        List<AdminUserEntity> entities = adminMapper.findUsers();
        if (entities == null || entities.isEmpty()) {
            return List.of(
                user(1001L, "木木", "SYJ10021", "女", "正常", "2026-04-07 15:08"),
                user(1002L, "阿泽", "SYJ10022", "男", "正常", "2026-04-07 14:41"),
                user(1003L, "Suki", "SYJ10023", "未知", "禁用", "2026-04-06 20:15")
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
        return operation(entity.getId(), entity.getTitle(), entity.getType(), entity.getTarget(), entity.getImageUrl(), entity.getStatus(), entity.getUpdatedAt());
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
        return user(entity.getId(), entity.getNickname(), entity.getUserNo(), entity.getGender(), entity.getStatus(), entity.getLastLogin());
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

    private AdminOperationVO operation(Long id, String title, String type, String target, String imageUrl, String status, String updatedAt) {
        AdminOperationVO item = new AdminOperationVO();
        item.setId(id);
        item.setTitle(title);
        item.setType(type);
        item.setTarget(target);
        item.setImageUrl(imageUrl);
        item.setStatus(status);
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

    private AdminUserVO user(Long id, String nickname, String userNo, String gender, String status, String lastLogin) {
        AdminUserVO item = new AdminUserVO();
        item.setId(id);
        item.setNickname(nickname);
        item.setUserNo(userNo);
        item.setGender(gender);
        item.setStatus(status);
        item.setLastLogin(lastLogin);
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

    private String generateUserNo(String prefix) {
        return prefix + String.valueOf(System.currentTimeMillis()).substring(3);
    }

    private String generateArtworkNo() {
        return "ART" + String.valueOf(System.currentTimeMillis());
    }
}
