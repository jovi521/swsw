package com.cdyw.swsw.system.app.util;

import cn.hutool.core.io.FileUtil;
import com.cdyw.swsw.common.common.component.CmdReadComponent;
import com.cdyw.swsw.common.domain.ao.enums.FileTypeEnum;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.*;

/**
 * 实现文档的在线预览功能：Python实现
 * 与LibreOffice类似，直接使用命令行的形式执行脚本即可
 *
 * @author jovi
 */
@Getter
@Component
public class FilePythonConvertUtil {

    private final CmdReadComponent cmdReadComponent;
    @Value("${application.python.path}")
    private String path;
    @Value("${application.python.command}")
    private String command;

    @Autowired
    public FilePythonConvertUtil(CmdReadComponent cmdReadComponent) {
        this.cmdReadComponent = cmdReadComponent;
    }

    /**
     * 转换文件成pdf
     *
     * @param srcFile :
     */
    public File file2Pdf(File srcFile, String destFilePath) throws IOException {
        File destFile;
        Process process = null;
        String commandFinal;
        // 判断文件是否存在，已经转化过了
        destFile = new File(destFilePath + "/" + FileUtil.getPrefix(srcFile) + "." + FileTypeEnum.FILE_TYPE_PDF.getSuffix());
        if (!destFile.exists()) {
            String path = ResourceUtils.getURL("classpath:").getPath();
            String finalPath = path + this.path;
            // 去除 /D:/svn-projects/SourceCode/swsw/swsw-system/target/classes/python/word2pdf.py 第一个斜杠
            commandFinal = command + " " + finalPath.substring(1) + " " + srcFile;
            System.out.println("********** " + commandFinal);
            process = Runtime.getRuntime().exec(commandFinal);
        }
        if (process != null) {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println(line);
                }
                //获取进程的标准输入流
                final InputStream is1 = process.getInputStream();
                //获取进程的错误流
                final InputStream is2 = process.getErrorStream();
                //启动两个线程，一个线程负责读标准输出流，另一个负责读标准错误流
                cmdReadComponent.readInputStream(is1);
                cmdReadComponent.readInputStream(is2);
                //可能导致进程阻塞，甚至死锁,所以必须读取输入流的内容避免死锁现象
                int waitFor = process.waitFor();
                int exitValue = process.exitValue();
                if (waitFor != 0) {
                    System.err.println("Python 未正常结束，（正常结束为0），状态码为： " + waitFor);
                } else {
                    System.out.println("Python 正常结束，状态码为： " + exitValue);
                }
                process.destroy();
            } catch (Exception ex) {
                ex.printStackTrace();
                try {
                    process.getErrorStream().close();
                    process.getInputStream().close();
                    process.getOutputStream().close();
                } catch (Exception ignored) {
                }
            }
        }
        return destFile;
    }
}
