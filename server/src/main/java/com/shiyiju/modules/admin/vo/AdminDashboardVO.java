package com.shiyiju.modules.admin.vo;

import java.util.List;

public class AdminDashboardVO {

    private List<AdminDashboardMetricVO> metrics;
    private List<String> todos;

    public List<AdminDashboardMetricVO> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<AdminDashboardMetricVO> metrics) {
        this.metrics = metrics;
    }

    public List<String> getTodos() {
        return todos;
    }

    public void setTodos(List<String> todos) {
        this.todos = todos;
    }
}
