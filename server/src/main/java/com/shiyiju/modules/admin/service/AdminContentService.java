package com.shiyiju.modules.admin.service;

import com.shiyiju.common.exception.BusinessException;
import com.shiyiju.modules.admin.dto.AdminOperationSaveDTO;
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
import org.springframework.stereotype.Service;

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
                artist("林观山", "杭州", "油画, 当代", 12, "上线", 1),
                artist("周岚", "上海", "版画, 新锐", 8, "上线", 2),
                artist("陈河", "苏州", "水墨, 山水", 15, "下线", 3)
            );
        }
        return entities.stream().map(this::toArtistVO).toList();
    }

    public void updateArtistStatus(Long id, String status) {
        String targetStatus = "ONLINE".equalsIgnoreCase(status) ? "ACTIVE" : "INACTIVE";
        if (adminMapper.updateArtistStatus(id, targetStatus) <= 0) {
            throw new BusinessException(40404, "艺术家不存在");
        }
    }

    public List<AdminArtworkVO> listArtworks() {
        List<AdminArtworkEntity> entities = adminMapper.findArtworks();
        if (entities == null || entities.isEmpty()) {
            return List.of(
                artwork("春山可望", "林观山", "¥12,800", 1, "上架", "热门藏品"),
                artwork("潮汐笔记", "周岚", "¥8,600", 2, "上架", "正在升值"),
                artwork("园林记忆", "陈河", "¥16,500", 1, "下架", "无")
            );
        }
        return entities.stream().map(this::toArtworkVO).toList();
    }

    public void updateArtworkStatus(Long id, String status) {
        String targetStatus = "ONLINE".equalsIgnoreCase(status) ? "PUBLISHED" : "DRAFT";
        if (adminMapper.updateArtworkStatus(id, targetStatus) <= 0) {
            throw new BusinessException(40404, "作品不存在");
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
                order("SYJ202604070001", "木木", "春山可望", "¥12,800", "待发货", "已支付", "未发货"),
                order("SYJ202604070002", "阿泽", "潮汐笔记", "¥8,600", "待支付", "待支付", "未发货"),
                order("SYJ202604060018", "Suki", "园林记忆", "¥16,500", "已完成", "已支付", "已发货")
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
        return artist(entity.getName(), entity.getCity(), entity.getTags(), entity.getWorks(), entity.getStatus(), entity.getSort());
    }

    private AdminArtworkVO toArtworkVO(AdminArtworkEntity entity) {
        return artwork(entity.getName(), entity.getArtist(), entity.getPrice(), entity.getStock(), entity.getStatus(), entity.getTag());
    }

    private AdminUserVO toUserVO(AdminUserEntity entity) {
        return user(entity.getNickname(), entity.getUserNo(), entity.getGender(), entity.getStatus(), entity.getLastLogin());
    }

    private AdminOrderVO toOrderVO(AdminOrderEntity entity) {
        return order(entity.getOrderNo(), entity.getUser(), entity.getArtwork(), entity.getAmount(), entity.getStatus(), entity.getPayStatus(), entity.getShipStatus());
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

    private AdminArtistVO artist(String name, String city, String tags, Integer works, String status, Integer sort) {
        AdminArtistVO item = new AdminArtistVO();
        item.setName(name);
        item.setCity(city);
        item.setTags(tags);
        item.setWorks(works);
        item.setStatus(status);
        item.setSort(sort);
        return item;
    }

    private AdminArtworkVO artwork(String name, String artist, String price, Integer stock, String status, String tag) {
        AdminArtworkVO item = new AdminArtworkVO();
        item.setName(name);
        item.setArtist(artist);
        item.setPrice(price);
        item.setStock(stock);
        item.setStatus(status);
        item.setTag(tag);
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

    private AdminOrderVO order(String orderNo, String user, String artwork, String amount, String status, String payStatus, String shipStatus) {
        AdminOrderVO item = new AdminOrderVO();
        item.setOrderNo(orderNo);
        item.setUser(user);
        item.setArtwork(artwork);
        item.setAmount(amount);
        item.setStatus(status);
        item.setPayStatus(payStatus);
        item.setShipStatus(shipStatus);
        return item;
    }
}
