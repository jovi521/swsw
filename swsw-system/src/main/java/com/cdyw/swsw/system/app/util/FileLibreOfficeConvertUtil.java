package com.cdyw.swsw.system.app.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.cdyw.swsw.common.domain.ao.enums.FileTypeEnum;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * 实现文档的在线预览功能：LibreOffice实现
 * 与OpenOffice不同，不需要借助jodconverter中间件，也不需要占用端口启动，直接使用命令行的形式转化
 *
 * @author jovi
 */
@Getter
@Component
public class FileLibreOfficeConvertUtil {

    private final String COMMAND_OUT_DIR = " --outdir ";
    @Value("${application.libreOffice.path}")
    private String path;
    @Value("${application.libreOffice.command}")
    private String command;

    /**
     * 转换文件成html
     *
     * @param destFilePath :
     */
    public File file2Html(File srcFile, String destFilePath) {
        return commonFileConvert(srcFile, destFilePath, FileTypeEnum.FILE_TYPE_HTML);
    }

    /**
     * 转换文件成pdf
     *
     * @param srcFile :
     */
    public File file2Pdf(File srcFile, String destFilePath) {
        return commonFileConvert(srcFile, destFilePath, FileTypeEnum.FILE_TYPE_PDF);
    }

    private File commonFileConvert(File srcFile, String destFilePath, FileTypeEnum fileTypeEnum) {
        File destFile = null;
        try {
            Process process = null;
            // 判断文件是否存在，已经转化过了
            String type = FileUtil.getType(srcFile);
            String fileName = StrUtil.subBefore(srcFile.getName(), type, false);
            destFile = new File(destFilePath + "/" + fileName + fileTypeEnum.getSuffix());
            if (!destFile.exists()) {
                command = path + " " + command + " " + fileTypeEnum.getSuffix() + " " + srcFile.getPath() + COMMAND_OUT_DIR + destFilePath;
                process = Runtime.getRuntime().exec(command);
            }
            if (process != null) {
                int waitFor = process.waitFor();
                int exitValue = process.exitValue();
                if (waitFor != 0) {
                    System.err.println("LireOffice 未正常结束（正常结束为0），状态码为： " + waitFor);
                } else {
                    System.out.println("LibreOffice 结束状态码为： " + exitValue);
                }
                process.destroy();
                command = null;
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("LireOffice 连接出错，请检查 LireOffice 服务是否启动： " + e.getMessage());
        }
        return destFile;
    }
}
