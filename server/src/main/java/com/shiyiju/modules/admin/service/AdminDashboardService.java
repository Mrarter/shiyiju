package com.shiyiju.modules.admin.service;

import com.shiyiju.modules.admin.vo.AdminDashboardMetricVO;
import com.shiyiju.modules.admin.vo.AdminDashboardVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminDashboardService {

    public AdminDashboardVO getDashboard() {
        AdminDashboardVO result = new AdminDashboardVO();
        result.setMetrics(List.of(
            metric("总用户数", "1,286", "较昨日 +28"),
            metric("艺术家数量", "36", "待完善资料 3"),
            metric("已上架作品", "218", "待上架 12"),
            metric("待处理订单", "15", "待发货 6")
        ));
        result.setTodos(List.of(
            "补齐首页推荐与艺术家展示数据",
            "修正首页空数据误提示",
            "补齐订单后台处理动作"
        ));
        return result;
    }

    private AdminDashboardMetricVO metric(String label, String value, String delta) {
        AdminDashboardMetricVO item = new AdminDashboardMetricVO();
        item.setLabel(label);
        item.setValue(value);
        item.setDelta(delta);
        return item;
    }
}
