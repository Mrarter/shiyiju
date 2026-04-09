package com.shiyiju.common.util;

import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 图片裁剪工具类
 * 支持指定坐标和尺寸裁剪
 */
@Component
public class ImageCropUtil {

    /**
     * 裁剪图片
     *
     * @param imageBytes 原始图片字节数组
     * @param x          裁剪起点X坐标
     * @param y          裁剪起点Y坐标
     * @param width      裁剪宽度
     * @param height     裁剪高度
     * @return 裁剪后的图片字节数组（JPEG格式）
     */
    public byte[] crop(byte[] imageBytes, int x, int y, int width, int height) throws IOException {
        if (imageBytes == null || imageBytes.length == 0) {
            throw new IOException("图片数据为空");
        }

        try (ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            BufferedImage originalImage = ImageIO.read(bais);
            if (originalImage == null) {
                throw new IOException("无法解析图片格式");
            }

            int imgWidth = originalImage.getWidth();
            int imgHeight = originalImage.getHeight();

            // 边界检查和修正
            x = Math.max(0, Math.min(x, imgWidth - 1));
            y = Math.max(0, Math.min(y, imgHeight - 1));
            width = Math.min(width, imgWidth - x);
            height = Math.min(height, imgHeight - y);

            if (width <= 0 || height <= 0) {
                throw new IOException("裁剪区域无效");
            }

            // 执行裁剪
            BufferedImage croppedImage = originalImage.getSubimage(x, y, width, height);

            // 转换为 RGB 模式（处理透明通道问题）
            BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = outputImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, width, height);
            g2d.drawImage(croppedImage, 0, 0, width, height, null);
            g2d.dispose();

            // 输出为 JPEG
            ImageIO.write(outputImage, "jpg", baos);
            return baos.toByteArray();
        }
    }

    /**
     * 缩放裁剪图片（按比例缩放后裁剪）
     *
     * @param imageBytes    原始图片字节数组
     * @param scale         缩放比例 (1.0 = 不缩放)
     * @param cropX         裁剪起点X坐标（相对于缩放后图片）
     * @param cropY         裁剪起点Y坐标（相对于缩放后图片）
     * @param cropWidth     裁剪宽度
     * @param cropHeight    裁剪高度
     * @return 缩放裁剪后的图片字节数组（JPEG格式）
     */
    public byte[] scaleAndCrop(byte[] imageBytes, double scale, int cropX, int cropY, int cropWidth, int cropHeight) throws IOException {
        if (imageBytes == null || imageBytes.length == 0) {
            throw new IOException("图片数据为空");
        }

        try (ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            BufferedImage originalImage = ImageIO.read(bais);
            if (originalImage == null) {
                throw new IOException("无法解析图片格式");
            }

            // 计算缩放后的尺寸
            int scaledWidth = (int) (originalImage.getWidth() * scale);
            int scaledHeight = (int) (originalImage.getHeight() * scale);

            // 缩放图片
            BufferedImage scaledImage = resize(originalImage, scaledWidth, scaledHeight);

            // 边界检查
            cropX = Math.max(0, Math.min(cropX, scaledWidth - 1));
            cropY = Math.max(0, Math.min(cropY, scaledHeight - 1));
            cropWidth = Math.min(cropWidth, scaledWidth - cropX);
            cropHeight = Math.min(cropHeight, scaledHeight - cropY);

            if (cropWidth <= 0 || cropHeight <= 0) {
                throw new IOException("裁剪区域无效");
            }

            // 执行裁剪
            BufferedImage croppedImage = scaledImage.getSubimage(cropX, cropY, cropWidth, cropHeight);

            // 转换为 RGB 模式
            BufferedImage outputImage = new BufferedImage(cropWidth, cropHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = outputImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, cropWidth, cropHeight);
            g2d.drawImage(croppedImage, 0, 0, cropWidth, cropHeight, null);
            g2d.dispose();

            ImageIO.write(outputImage, "jpg", baos);
            return baos.toByteArray();
        }
    }

    /**
     * 等比例缩放图片
     */
    private BufferedImage resize(BufferedImage originalImage, int targetWidth, int targetHeight) {
        int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();

        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, type);
        Graphics2D g2d = resizedImage.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();

        return resizedImage;
    }

    /**
     * 获取图片尺寸
     *
     * @param imageBytes 图片字节数组
     * @return int数组 [width, height]
     */
    public int[] getImageSize(byte[] imageBytes) throws IOException {
        if (imageBytes == null || imageBytes.length == 0) {
            throw new IOException("图片数据为空");
        }

        try (ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes)) {
            BufferedImage image = ImageIO.read(bais);
            if (image == null) {
                throw new IOException("无法解析图片格式");
            }
            return new int[]{image.getWidth(), image.getHeight()};
        }
    }
}
