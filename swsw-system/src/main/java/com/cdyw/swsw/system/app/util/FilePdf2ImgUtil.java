package com.cdyw.swsw.system.app.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 利用 apache 的 pdfbox 将 pdf 文件转化成 img
 *
 * @author jovi
 */
public class FilePdf2ImgUtil {

    /**
     * 经过测试,dpi为96,100,105,120,150,200中,105显示效果较为清晰,体积稳定,dpi越高图片体积越大,一般电脑显示分辨率为96
     */
    public static final float DEFAULT_DPI = 105;
    /**
     * 默认转换的图片格式为jpg、png
     */
    public static final String DEFAULT_FORMAT = "png";

    public static void toImg(File srcFile, String imgPath) throws IOException {
        boolean isSuccess = false;
        System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
        //图像合并使用参数
        // 总宽度
        int width = 0;
        // 保存一张图片中的RGB数据
        int[] singleimgRgb;
        int shiftHeight = 0;
        //保存每张图片的像素值
        BufferedImage imageResult = null;
        //利用PdfBox生成图像
        PDDocument pdDocument = PDDocument.load(srcFile);
        PDFRenderer renderer = new PDFRenderer(pdDocument);
        int pages = pdDocument.getNumberOfPages();
        //循环每个页码
        for (int i = 0; i < pages; i++) {
            BufferedImage image = renderer.renderImageWithDPI(i, DEFAULT_DPI, ImageType.RGB);
            int imageHeight = image.getHeight();
            int imageWidth = image.getWidth();
            //计算高度和偏移量
            if (i == 0) {
                //使用第一张图片宽度;
                width = imageWidth;
                //保存每页图片的像素值
                imageResult = new BufferedImage(width, imageHeight * pages, BufferedImage.TYPE_INT_RGB);
            } else {
                // 计算偏移高度
                shiftHeight += imageHeight;
            }
            singleimgRgb = image.getRGB(0, 0, width, imageHeight, null, 0, width);
            // 写入流中
            imageResult.setRGB(0, shiftHeight, width, imageHeight, singleimgRgb, 0, width);
        }
        pdDocument.close();
        // 写图片
        if (imageResult != null) {
            isSuccess = ImageIO.write(imageResult, DEFAULT_FORMAT, new File(imgPath));
            System.out.println("Image created");
        }
        // 如果转化不成功，使用另外一种继续转化
        if (!isSuccess) {
            for (int j = 0; j < pages; j++) {
                //Rendering an image from the PDF document
                BufferedImage image = renderer.renderImage(j, DEFAULT_DPI, ImageType.RGB);
                //Writing the image to a file
                ImageIO.write(image, DEFAULT_FORMAT, new File(imgPath));
                System.out.println("Image created");
                //Closing the document
                pdDocument.close();
            }
        }
    }
}
