package com.shiyiju.modules.admin.service;

import com.shiyiju.common.exception.BusinessException;
import com.shiyiju.modules.admin.support.AdminUploadPaths;
import com.shiyiju.modules.admin.vo.AdminUploadVO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.UUID;

@Service
public class AdminAssetService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp", "gif");

    public AdminUploadVO uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(40001, "请选择图片文件");
        }
        String contentType = file.getContentType();
        if (!StringUtils.hasText(contentType) || !contentType.startsWith("image/")) {
            throw new BusinessException(40001, "仅支持图片上传");
        }

        String extension = resolveExtension(file.getOriginalFilename(), contentType);
        Path directory = AdminUploadPaths.root()
            .resolve("images")
            .resolve(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
        String fileName = UUID.randomUUID().toString().replace("-", "") + "." + extension;
        Path target = directory.resolve(fileName);
        try {
            Files.createDirectories(directory);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            throw new BusinessException(50001, "图片上传失败");
        }

        AdminUploadVO upload = new AdminUploadVO();
        upload.setName(fileName);
        upload.setUrl("/uploads/images/"
            + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
            + "/"
            + fileName);
        return upload;
    }

    private String resolveExtension(String originalFilename, String contentType) {
        String extension = StringUtils.getFilenameExtension(originalFilename);
        if (StringUtils.hasText(extension)) {
            String normalized = extension.trim().toLowerCase();
            if (ALLOWED_EXTENSIONS.contains(normalized)) {
                return normalized;
            }
        }
        return switch (contentType) {
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            case "image/gif" -> "gif";
            default -> throw new BusinessException(40001, "不支持的图片格式");
        };
    }
}
