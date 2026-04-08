package com.shiyiju.modules.admin.service;

import com.shiyiju.common.exception.BusinessException;
import com.shiyiju.modules.admin.dto.AdminOperationSaveDTO;
import com.shiyiju.modules.admin.dto.AdminArtistSaveDTO;
import com.shiyiju.modules.admin.dto.AdminArtworkSaveDTO;
import com.shiyiju.modules.admin.dto.AdminRemarkUpdateDTO;
import com.shiyiju.modules.admin.dto.AdminShipmentUpdateDTO;
import com.shiyiju.modules.admin.entity.AdminArtistEntity;
import com.shiyiju.modules.admin.entity.AdminArtworkEntity;
import com.shiyiju.modules.admin.entity.AdminOperationEntity;
import com.shiyiju.modules.admin.entity.AdminOrderEntity;
import com.shiyiju.modules.admin.entity.AdminUserEntity;
import com.shiyiju.modules.admin.mapper.AdminMapper;
import com.shiyiju.modules.admin.vo.AdminArtistVO;
import com.shiyiju.modules.admin.vo.AdminArtworkVO;
import com.shiyiju.modules.admin.vo.AdminOperationVO;
import com.shiyiju.modules.admin.vo.AdminOrderVO;
import com.shiyiju.modules.admin.vo.AdminUserVO;
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
                operation(1L, "春季主视觉 Banner", "Banner", "首页主视觉", "启用", "2026-04-07 14:20"),
                operation(2L, "热门藏品推荐", "热门藏品", "作品 8 个", "启用", "2026-04-07 13:45"),
                operation(3L, "推荐艺术家", "推荐艺术家", "艺术家 6 位", "草稿", "2026-04-07 12:15")
            );
        }
        return entities.stream().map(this::toOperationVO).toList();
    }

    public AdminOperationVO createOperation(AdminOperationSaveDTO request) {
        AdminOperationEntity entity = new AdminOperationEntity();
        entity.setTitle(request.getTitle());
        entity.setType(request.getType());
        entity.setTarget(request.getTarget());
        entity.setStatus(request.getStatus());
        entity.setSortNo(request.getSortNo());
        adminMapper.insertOperation(entity);
        return operation(entity.getId(), request.getTitle(), request.getType(), request.getTarget(), displayOperationStatus(request.getStatus()), "刚刚");
    }

    public AdminOperationVO updateOperation(Long id, AdminOperationSaveDTO request) {
        AdminOperationEntity entity = new AdminOperationEntity();
        entity.setId(id);
        entity.setTitle(request.getTitle());
        entity.setType(request.getType());
        entity.setTarget(request.getTarget());
        entity.setStatus(request.getStatus());
        entity.setSortNo(request.getSortNo());
        if (adminMapper.updateOperation(entity) <= 0) {
            throw new BusinessException(40404, "运营配置不存在");
        }
        return operation(id, request.getTitle(), request.getType(), request.getTarget(), displayOperationStatus(request.getStatus()), "刚刚");
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
                artist(3001L, "林观山", "杭州", "油画, 当代", 12, "上线", 1),
                artist(3002L, "周岚", "上海", "版画, 新锐", 8, "上线", 2),
                artist(3003L, "陈河", "苏州", "水墨, 山水", 15, "下线", 3)
            );
        }
        return entities.stream().map(this::toArtistVO).toList();
    }

    @Transactional
    public AdminArtistVO createArtist(AdminArtistSaveDTO request) {
        UserAccountEntity user = new UserAccountEntity();
        user.setUserNo(generateUserNo("SYJ9"));
        user.setNickname("艺术家" + request.getName());
        user.setGender(0);
        user.setStatus("ENABLED");
        user.setRegisterSource("ADMIN_CREATE");
        adminMapper.insertArtistUser(user);

        AdminArtistEntity entity = new AdminArtistEntity();
        entity.setUserId(user.getId());
        entity.setName(request.getName());
        entity.setTags(request.getTags());
        entity.setWorks(request.getWorks());
        entity.setStatus(toArtistStatus(request.getStatus()));
        adminMapper.insertArtist(entity);
        return artist(entity.getId(), request.getName(), "", request.getTags(), request.getWorks(), displayArtistStatus(entity.getStatus()), entity.getId().intValue());
    }

    public AdminArtistVO updateArtist(Long id, AdminArtistSaveDTO request) {
        AdminArtistEntity entity = new AdminArtistEntity();
        entity.setId(id);
        entity.setName(request.getName());
        entity.setTags(request.getTags());
        entity.setWorks(request.getWorks());
        entity.setStatus(toArtistStatus(request.getStatus()));
        if (adminMapper.updateArtist(entity) <= 0) {
            throw new BusinessException(40404, "艺术家不存在");
        }
        return artist(id, request.getName(), "", request.getTags(), request.getWorks(), displayArtistStatus(entity.getStatus()), id.intValue());
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
                artwork(4001L, 3001L, "春山可望", "林观山", "¥12,800", 1, "上架", "热门藏品", ""),
                artwork(4002L, 3002L, "潮汐笔记", "周岚", "¥8,600", 2, "上架", "正在升值", ""),
                artwork(4003L, 3003L, "园林记忆", "陈河", "¥16,500", 1, "下架", "无", "")
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
        entity.setTag(generateArtworkNo());
        adminMapper.insertArtwork(entity);
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
        if (adminMapper.updateArtwork(entity) <= 0) {
            throw new BusinessException(40404, "作品不存在");
        }
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

    public List<AdminUserVO> listUsers() {
        List<AdminUserEntity> entities = adminMapper.findUsers();
        if (entities == null || entities.isEmpty()) {
            return List.of(
                user("木木", "SYJ10021", "女", "正常", "2026-04-07 15:08"),
                user("阿泽", "SYJ10022", "男", "正常", "2026-04-07 14:41"),
                user("Suki", "SYJ10023", "未知", "禁用", "2026-04-06 20:15")
            );
        }
        return entities.stream().map(this::toUserVO).toList();
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
        return operation(entity.getId(), entity.getTitle(), entity.getType(), entity.getTarget(), entity.getStatus(), entity.getUpdatedAt());
    }

    private String displayOperationStatus(String status) {
        return switch (status) {
            case "ENABLED" -> "启用";
            case "DRAFT" -> "草稿";
            default -> "停用";
        };
    }

    private AdminArtistVO toArtistVO(AdminArtistEntity entity) {
        return artist(entity.getId(), entity.getName(), entity.getCity(), entity.getTags(), entity.getWorks(), entity.getStatus(), entity.getSort());
    }

    private AdminArtworkVO toArtworkVO(AdminArtworkEntity entity) {
        return artwork(entity.getId(), entity.getArtistId(), entity.getName(), entity.getArtist(), entity.getPrice(), entity.getStock(), entity.getStatus(), entity.getTag(), entity.getDescription());
    }

    private AdminUserVO toUserVO(AdminUserEntity entity) {
        return user(entity.getNickname(), entity.getUserNo(), entity.getGender(), entity.getStatus(), entity.getLastLogin());
    }

    private AdminOrderVO toOrderVO(AdminOrderEntity entity) {
        return order(entity.getId(), entity.getOrderNo(), entity.getUser(), entity.getArtwork(), entity.getAmount(), entity.getStatus(), entity.getPayStatus(), entity.getShipStatus());
    }

    private AdminOperationVO operation(Long id, String title, String type, String target, String status, String updatedAt) {
        AdminOperationVO item = new AdminOperationVO();
        item.setId(id);
        item.setTitle(title);
        item.setType(type);
        item.setTarget(target);
        item.setStatus(status);
        item.setUpdatedAt(updatedAt);
        return item;
    }

    private AdminArtistVO artist(Long id, String name, String city, String tags, Integer works, String status, Integer sort) {
        AdminArtistVO item = new AdminArtistVO();
        item.setId(id);
        item.setName(name);
        item.setCity(city);
        item.setTags(tags);
        item.setWorks(works);
        item.setStatus(status);
        item.setSort(sort);
        return item;
    }

    private AdminArtworkVO artwork(Long id, Long artistId, String name, String artist, String price, Integer stock, String status, String tag, String description) {
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
        return item;
    }

    private AdminUserVO user(String nickname, String userNo, String gender, String status, String lastLogin) {
        AdminUserVO item = new AdminUserVO();
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
