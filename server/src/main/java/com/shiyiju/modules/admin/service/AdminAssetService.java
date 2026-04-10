package com.shiyiju.modules.admin.service;

import com.shiyiju.common.exception.BusinessException;
import com.shiyiju.common.util.ImageCropUtil;
import com.shiyiju.modules.admin.support.AdminUploadPaths;
import com.shiyiju.modules.admin.vo.AdminUploadVO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class AdminAssetService {

    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024; // 20MB

    private final ImageCropUtil imageCropUtil;

    public AdminAssetService(ImageCropUtil imageCropUtil) {
        this.imageCropUtil = imageCropUtil;
    }

    public AdminUploadVO uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(40001, "请选择图片文件");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(40001, "图片体积不能超过 20MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || contentType.isBlank() || !contentType.startsWith("image/")) {
            throw new BusinessException(40001, "仅支持图片上传");
        }

        Path directory = AdminUploadPaths.root()
            .resolve("images")
            .resolve(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
        String fileName = UUID.randomUUID().toString().replace("-", "") + ".jpg";
        Path target = directory.resolve(fileName);

        try {
            Files.createDirectories(directory);
            
            byte[] imageBytes = file.getBytes();
            
            // 检查是否是 HEIF 格式，如果是则转换为 JPEG
            if (isHeifFormat(imageBytes)) {
                imageBytes = convertHeifToJpegUsingSips(imageBytes, target);
            } else {
                // 非 HEIF 格式，直接保存
                try (InputStream inputStream = new ByteArrayInputStream(imageBytes)) {
                    Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } catch (IOException exception) {
            throw new BusinessException(50001, "图片上传失败: " + exception.getMessage());
        }

        AdminUploadVO upload = new AdminUploadVO();
        upload.setName(fileName);
        upload.setUrl("/uploads/images/"
            + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
            + "/"
            + fileName);
        return upload;
    }

    /**
     * 上传图片（支持裁剪）
     *
     * @param file    图片文件
     * @param cropX   裁剪起点X坐标（可选，为null表示不裁剪）
     * @param cropY   裁剪起点Y坐标（可选，为null表示不裁剪）
     * @param cropW   裁剪宽度（可选，为null表示不裁剪）
     * @param cropH   裁剪高度（可选，为null表示不裁剪）
     * @param scale   缩放比例（可选，为null表示不缩放）
     * @return 上传结果
     */
    public AdminUploadVO uploadImageWithCrop(MultipartFile file, 
                                              Integer cropX, Integer cropY, 
                                              Integer cropW, Integer cropH,
                                              Double scale) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(40001, "请选择图片文件");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(40001, "图片体积不能超过 20MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || contentType.isBlank() || !contentType.startsWith("image/")) {
            throw new BusinessException(40001, "仅支持图片上传");
        }

        Path directory = AdminUploadPaths.root()
            .resolve("images")
            .resolve(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
        String fileName = UUID.randomUUID().toString().replace("-", "") + ".jpg";
        Path target = directory.resolve(fileName);

        try {
            Files.createDirectories(directory);

            byte[] imageBytes = file.getBytes();

            // 如果需要裁剪或缩放，先处理图片
            if (cropX != null && cropY != null && cropW != null && cropH != null) {
                if (scale != null && scale != 1.0) {
                    // 缩放+裁剪
                    imageBytes = imageCropUtil.scaleAndCrop(imageBytes, scale, cropX, cropY, cropW, cropH);
                } else {
                    // 仅裁剪
                    imageBytes = imageCropUtil.crop(imageBytes, cropX, cropY, cropW, cropH);
                }
            } else if (scale != null && scale != 1.0) {
                // 仅缩放
                int[] size = imageCropUtil.getImageSize(imageBytes);
                imageBytes = imageCropUtil.scaleAndCrop(imageBytes, scale, 0, 0, 
                    (int)(size[0] * scale), (int)(size[1] * scale));
            }

            // 检查是否是 HEIF 格式，如果是则转换为 JPEG
            if (isHeifFormat(imageBytes)) {
                imageBytes = convertHeifToJpegUsingSips(imageBytes, target);
            } else {
                // 非 HEIF 格式，直接保存
                Files.write(target, imageBytes);
            }
        } catch (IOException exception) {
            throw new BusinessException(50001, "图片处理失败: " + exception.getMessage());
        }

        AdminUploadVO upload = new AdminUploadVO();
        upload.setName(fileName);
        upload.setUrl("/uploads/images/"
            + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
            + "/"
            + fileName);
        return upload;
    }

    /**
     * 使用 macOS 的 sips 命令将 HEIF 转换为 JPEG
     */
    private byte[] convertHeifToJpegUsingSips(byte[] heifBytes, Path targetPath) throws IOException {
        // 先保存 HEIF 临时文件
        Path tempHeif = targetPath.getParent().resolve(UUID.randomUUID().toString() + ".heic");
        Path tempJpg = targetPath.getParent().resolve(UUID.randomUUID().toString() + ".jpg");
        
        try {
            // 写入临时 HEIF 文件
            Files.write(tempHeif, heifBytes);
            
            // 使用 sips 转换为 JPEG
            ProcessBuilder pb = new ProcessBuilder("sips", "-s", "format", "jpeg", tempHeif.toString(), "--out", tempJpg.toString());
            pb.redirectErrorStream(true);
            Process process = pb.start();
            int exitCode = process.waitFor();
            
            if (exitCode == 0 && Files.exists(tempJpg)) {
                // 读取转换后的 JPEG
                byte[] jpegBytes = Files.readAllBytes(tempJpg);
                // 写入目标文件
                Files.write(targetPath, jpegBytes);
                return jpegBytes;
            } else {
                // 转换失败，尝试直接使用原文件
                String errorMsg = new String(process.getInputStream().readAllBytes());
                throw new IOException("HEIF 转换失败: " + errorMsg);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("HEIF 转换被中断");
        } finally {
            // 清理临时文件
            Files.deleteIfExists(tempHeif);
            Files.deleteIfExists(tempJpg);
        }
    }

    /**
     * 检查是否是 HEIF/HEIC 格式
     */
    private boolean isHeifFormat(byte[] imageBytes) {
        if (imageBytes == null || imageBytes.length < 12) {
            return false;
        }
        // HEIF/HEIC 文件签名: ftyp + heic/hevx/hvc/mif1
        if (imageBytes[4] == 'f' && imageBytes[5] == 't' && imageBytes[6] == 'y' && imageBytes[7] == 'p') {
            // 检查 brand 字段
            String brand = new String(new byte[]{imageBytes[8], imageBytes[9], imageBytes[10], imageBytes[11]});
            return brand.equals("heic") || brand.equals("hevx") || brand.equals("hvc1") || 
                   brand.equals("mif1") || brand.equals("heix") || brand.equals("hevc");
        }
        return false;
    }
}
