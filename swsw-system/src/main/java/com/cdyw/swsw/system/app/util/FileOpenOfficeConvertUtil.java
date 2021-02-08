package com.cdyw.swsw.system.app.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.StreamOpenOfficeDocumentConverter;
import com.cdyw.swsw.common.domain.ao.enums.FileTypeEnum;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;

/**
 * 实现文档的在线预览功能：OpenOffice实现
 * 使用前确保OpenOffice服务启动起来：soffice -headless -accept="socket,host=127.0.0.1,port=8100;urp;" -nofirststartwizard
 * 目前仅支持21种基础文件格式
 * jodconverter使用2.2.2版本完美支持word2007更新版本
 *
 * @author jovi
 */
@Getter
@Component
public class FileOpenOfficeConvertUtil {

    @Value("${application.openOffice4.host}")
    private String host;

    @Value("${application.openOffice4.port}")
    private String port;

    @Value("${application.openOffice4.path}")
    private String path;

    @Value("${application.openOffice4.command}")
    private String command;

    /**
     * 转换文件成html
     *
     * @param destFilePath :
     * @throws IOException e
     */
    public File file2Html(File srcFile, String destFilePath) throws IOException {
        String type = FileUtil.getType(srcFile);
        String fileName = StrUtil.subBefore(srcFile.getName(), type, false);
        // 指定路径和转换后的文件名
        File destFile = new File(destFilePath + "/" + fileName + FileTypeEnum.FILE_TYPE_HTML.getSuffix());
        return commonFileConvert(srcFile, destFile);
    }

    /**
     * 转换文件成pdf
     *
     * @param srcFile :
     * @throws IOException e
     */
    public File file2Pdf(File srcFile, String destFilePath) throws IOException {
        String type = FileUtil.getType(srcFile);
        String fileName = StrUtil.subBefore(srcFile.getName(), type, false);
        // 指定路径和转换后的文件名
        File destFile = new File(destFilePath + "/" + fileName + FileTypeEnum.FILE_TYPE_PDF.getSuffix());
        return commonFileConvert(srcFile, destFile);
    }

    private File commonFileConvert(File srcFile, File destFile) throws IOException {
        try {
            OpenOfficeConnection connection = new SocketOpenOfficeConnection(host, Integer.parseInt(port));
            Process process = null;
            if (!connection.isConnected()) {
                process = Runtime.getRuntime().exec(path + " " + command);
            }
            connection.connect();
//            DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
            // 为了支持docx格式，用一下2.2.2jar包下的 StreamOpenOfficeDocumentConverter
            DocumentConverter converter = new StreamOpenOfficeDocumentConverter(connection);
            if (!destFile.exists()) {
                converter.convert(srcFile, destFile);
            }
            connection.disconnect();
            if (process != null) {
                process.destroy();
            }
        } catch (ConnectException e) {
            System.err.println("OpenOffice连接出错，请检查OpenOffice服务是否启动。" + e.getMessage());
        } catch (IllegalArgumentException o) {
            System.out.println("转化出错：(请先检查是个否支持21种基本格式)" + o.getMessage());
        }
        return destFile;
    }
}
