package com.shiyiju.modules.admin.service;

import com.shiyiju.modules.admin.vo.AdminArtistVO;
import com.shiyiju.modules.admin.vo.AdminArtworkVO;
import com.shiyiju.modules.admin.vo.AdminOperationVO;
import com.shiyiju.modules.admin.vo.AdminOrderVO;
import com.shiyiju.modules.admin.vo.AdminUserVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminContentService {

    public List<AdminOperationVO> listOperations() {
        return List.of(
            operation(1L, "春季主视觉 Banner", "Banner", "首页主视觉", "启用", "2026-04-07 14:20"),
            operation(2L, "热门藏品推荐", "热门藏品", "作品 8 个", "启用", "2026-04-07 13:45"),
            operation(3L, "推荐艺术家", "推荐艺术家", "艺术家 6 位", "草稿", "2026-04-07 12:15")
        );
    }

    public List<AdminArtistVO> listArtists() {
        return List.of(
            artist("林观山", "杭州", "油画, 当代", 12, "上线", 1),
            artist("周岚", "上海", "版画, 新锐", 8, "上线", 2),
            artist("陈河", "苏州", "水墨, 山水", 15, "下线", 3)
        );
    }

    public List<AdminArtworkVO> listArtworks() {
        return List.of(
            artwork("春山可望", "林观山", "¥12,800", 1, "上架", "热门藏品"),
            artwork("潮汐笔记", "周岚", "¥8,600", 2, "上架", "正在升值"),
            artwork("园林记忆", "陈河", "¥16,500", 1, "下架", "无")
        );
    }

    public List<AdminUserVO> listUsers() {
        return List.of(
            user("木木", "SYJ10021", "女", "正常", "2026-04-07 15:08"),
            user("阿泽", "SYJ10022", "男", "正常", "2026-04-07 14:41"),
            user("Suki", "SYJ10023", "未知", "禁用", "2026-04-06 20:15")
        );
    }

    public List<AdminOrderVO> listOrders() {
        return List.of(
            order("SYJ202604070001", "木木", "春山可望", "¥12,800", "待发货", "已支付", "未发货"),
            order("SYJ202604070002", "阿泽", "潮汐笔记", "¥8,600", "待支付", "待支付", "未发货"),
            order("SYJ202604060018", "Suki", "园林记忆", "¥16,500", "已完成", "已支付", "已发货")
        );
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
