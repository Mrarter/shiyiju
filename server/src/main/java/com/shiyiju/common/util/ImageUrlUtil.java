package com.shiyiju.common.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * 图片URL处理工具类
 * 统一处理数据库中的图片URL，确保兼容相对路径和绝对路径
 */
@Component
public class ImageUrlUtil {

    /**
     * 默认占位图种子
     */
    private static final String DEFAULT_PLACEHOLDER = "https://picsum.photos/seed/default/400/500";

    /**
     * 服务器基础URL（从配置文件读取）
     */
    @Value("${server.port:8080}")
    private int serverPort;

    @Value("${server.servlet.context-path:/}")
    private String contextPath;

    /**
     * 图片服务基础URL（支持通过环境变量配置）
     * 默认为 http://localhost:8080，开发环境真机调试可通过环境变量 IMAGE_BASE_URL 覆盖
     */
    @Value("${image.base-url:http://192.168.1.163:8080}")
    private String imageBaseUrl;

    /**
     * 获取服务器基础URL
     * 用于拼接相对路径图片
     */
    public String getServerBaseUrl() {
        // 如果配置了 image.base-url，使用配置的地址
        if (imageBaseUrl != null && !imageBaseUrl.trim().isEmpty()) {
            return imageBaseUrl.endsWith("/") 
                ? imageBaseUrl.substring(0, imageBaseUrl.length() - 1)
                : imageBaseUrl;
        }
        return "http://localhost:" + serverPort + contextPath;
    }

    /**
     * 设置服务器基础URL（用于测试或配置注入）
     */
    public void setServerBaseUrl(String baseUrl) {
        // 允许外部注入
    }

    /**
     * 标准化图片URL
     * - null/空字符串 → 返回占位图
     * - 相对路径(/开头) → 拼接服务器基础URL
     * - 绝对路径 → 直接返回
     *
     * @param url 数据库中的图片URL
     * @return 标准化后的完整URL
     */
    public String normalize(String url) {
        return normalize(url, DEFAULT_PLACEHOLDER);
    }

    /**
     * 标准化图片URL（指定占位图）
     *
     * @param url          数据库中的图片URL
     * @param placeholder  空值时的占位图URL
     * @return 标准化后的完整URL
     */
    public String normalize(String url, String placeholder) {
        if (url == null || url.trim().isEmpty()) {
            return placeholder != null ? placeholder : DEFAULT_PLACEHOLDER;
        }

        String trimmed = url.trim();

        // 已经是完整的HTTP(S) URL
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            return trimmed;
        }

        // 相对路径，需要拼接服务器地址
        if (trimmed.startsWith("/")) {
            return getServerBaseUrl() + trimmed;
        }

        // 其他情况（可能是协议相对路径如 //cdn.xxx.com/xxx）
        if (trimmed.startsWith("//")) {
            return "https:" + trimmed;
        }

        // 兜底：当作相对路径处理
        return getServerBaseUrl() + "/" + trimmed;
    }

    /**
     * 批量标准化图片URL
     *
     * @param urls 图片URL数组
     * @return 标准化后的URL数组
     */
    public String[] normalizeAll(String... urls) {
        if (urls == null) {
            return new String[0];
        }
        String[] result = new String[urls.length];
        for (int i = 0; i < urls.length; i++) {
            result[i] = normalize(urls[i]);
        }
        return result;
    }

    /**
     * 检查URL是否是有效的图片URL
     *
     * @param url 图片URL
     * @return 是否有效
     */
    public boolean isValidImageUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        String trimmed = url.trim().toLowerCase();
        return trimmed.startsWith("http://")
            || trimmed.startsWith("https://")
            || trimmed.startsWith("//")
            || trimmed.startsWith("/uploads/")
            || trimmed.startsWith("/images/");
    }

    /**
     * 获取占位图URL
     *
     * @param seed 用于生成不同占位图的种子
     * @return 占位图URL
     */
    public String getPlaceholderUrl(String seed) {
        if (seed == null || seed.isEmpty()) {
            return DEFAULT_PLACEHOLDER;
        }
        return "https://picsum.photos/seed/" + seed + "/400/500";
    }

    /**
     * 获取艺术家头像占位图
     */
    public String getArtistAvatarPlaceholder() {
        return "https://ui-avatars.com/api/?name=Artist&background=c9a96d&color=fff&size=128&rounded=true";
    }

    /**
     * 提取URL中的域名（用于CDN判断）
     */
    public String extractDomain(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        try {
            String urlToParse = url;
            if (!urlToParse.startsWith("http")) {
                urlToParse = "https://" + urlToParse;
            }
            URI uri = new URI(urlToParse);
            return uri.getHost();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断是否是外部URL（非本服务器）
     */
    public boolean isExternalUrl(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }
        String domain = extractDomain(url);
        if (domain == null) {
            return false;
        }
        // 如果包含localhost或本机IP，认为是本地图片
        return !domain.contains("localhost") && !domain.matches("\\d+\\.\\d+\\.\\d+\\.\\d+");
    }
}
