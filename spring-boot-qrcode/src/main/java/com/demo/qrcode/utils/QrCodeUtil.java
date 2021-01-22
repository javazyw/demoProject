package com.demo.qrcode.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright: Copyright (c) 2020 yeyongxi. All Rights Reserved.
 * <p>
 * 功能描述：
 *
 * @author yeyongxi
 * Create Date:  2020-06-16 10:59
 * @version v1.0.0
 */
@Slf4j
public class QrCodeUtil {
    private static final int BLACK = 0xFF000000;
    private static final int WHITE = 0xFFFFFFFF;

    /**
     * 生成二维码图片
     *
     * @param url
     * @param path
     * @param fileName
     * @return
     */
    public static String createQrCode(String url, String path, String fileName) {
        try {
            Map<EncodeHintType, String> hints = new HashMap<>(16);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            BitMatrix bitMatrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, 400, 400, hints);
            File file = new File(path, fileName);
            boolean iscreate = ((file.getParentFile().exists() || file.getParentFile().mkdirs()) && file.createNewFile());
            if (file.exists() || iscreate) {
                writeToFile(bitMatrix, "jpg", file);
                return file.getPath();
            }
        } catch (Exception e) {
           log.error("error", e);
        }
        return null;
    }

    /**
     * 生成二维码图片+文字描述
     * @param width
     * @param height
     * @param imagesX
     * @param imagesY
     * @param qrWidth
     * @param qrHeight
     * @param url
     * @param path
     * @param fileName
     * @param title
     * @param titleX
     * @param titleY
     * @param size
     * @return
     */
    public static File createQrCodeWithTitle(Integer width, Integer height, Integer imagesX, Integer imagesY, Integer qrWidth, Integer qrHeight, String url, String path, String fileName, String title, Integer titleX, Integer titleY,Integer size){
        try {
            Map<EncodeHintType, String> hints = new HashMap<>(16);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            BitMatrix bitMatrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, qrWidth, qrHeight, hints);
            BufferedImage image = new BufferedImage(qrWidth, qrHeight, BufferedImage.TYPE_INT_RGB);
            /**  开始利用二维码数据创建Bitmap图片，分别设为黑(0xFFFFFFFF) 白(0xFF000000)两色**/
            for (int x = 0; x < qrWidth; x++) {
                for (int y = 0; y < qrHeight; y++) {
                    image.setRGB(x, y, bitMatrix.get(x, y) ? BLACK : WHITE);
                }
            }
            BufferedImage backgroundImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
            int bgWidth=backgroundImage.getWidth();
            int qrwidth=image.getWidth();
            //距离背景图片x边的距离，居中显示
            int disx=(bgWidth-qrwidth)-imagesX;
            //距离y边距离 * * * *
            int disy=imagesY;
            Graphics2D rng=backgroundImage.createGraphics();
            rng.setBackground(Color.WHITE);
            rng.setColor(new Color(255, 255, 255));
            rng.fillRect(0, 0, width, height);
            rng.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP));
            rng.drawImage(image,disx,disy,qrWidth,qrHeight,null);
            /** 文字描述参数设置 */
            Color textColor=Color.white;
            rng.setColor(textColor);
            rng.drawImage(backgroundImage,0,0,null);
            /**设置字体类型和大小(BOLD加粗/ PLAIN平常)*/
            rng.setFont(new Font("WenQuanYi Micro Hei",Font.BOLD,size));
            /**设置字体颜色*/
            rng.setColor(Color.black);
            int strWidth=rng.getFontMetrics().stringWidth(title);
            /**文字1显示位置*/
            /**左右*/
            int disx1=(bgWidth-strWidth)-titleX;
            /**上下*/
            rng.drawString(title,disx1,titleY);
            rng.dispose();
            image=backgroundImage;
            image.flush();
            File file = new File(path, fileName);
            boolean iscreate = ((file.getParentFile().exists() || file.getParentFile().mkdirs()) && file.createNewFile());
            if (file.exists() || iscreate) {
                ImageIO.write(image, "png", file);
                return file;
            }
        } catch (Exception e) {
            log.error("error", e);
        }
        return null;
    }

    static void writeToFile(BitMatrix matrix, String format, File file) throws IOException {
        BufferedImage image = toBufferedImage(matrix);
        if (!ImageIO.write(image, format, file)) {
            throw new IOException("Could not write an image of format " + format + " to " + file);
        }
    }

    static void writeToStream(BitMatrix matrix, String format, OutputStream stream) throws IOException {
        BufferedImage image = toBufferedImage(matrix);
        if (!ImageIO.write(image, format, stream)) {
            throw new IOException("Could not write an image of format " + format);
        }
    }

    private static BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
            }
        }
        return image;
    }

    public static void main(String[] args) {
        String url = "http://www.baidu.com";
        System.out.println(url);
        createQrCodeWithTitle(500, 520, 60, 110, 400, 400, "www.baidu.com", "d://", "a.png", "来访登记二维码", 120, 110,38);
    }
}
