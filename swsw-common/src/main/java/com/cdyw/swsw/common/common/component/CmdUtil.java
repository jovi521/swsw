package com.cdyw.swsw.common.common.component;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Component
public class CmdUtil {

    @Autowired
    private CmdReadComponent cmdReadComponent;

    /**
     * 程序执行正常返回0，非0表示异常
     *
     * @param command
     * @return
     */
    public int executeCMDCommand(String command) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (process != null) {
            try {
                // in.readLine()可能会造成阻塞，所以暂时注释
//                BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));
//                String line;
//                while ((line = in.readLine()) != null) {
//                    // System.out.println(line);
//                }
                //获取进程的标准输入流
                final InputStream is1 = process.getInputStream();
                //获取进程的错误流
                final InputStream is2 = process.getErrorStream();
                //启动两个线程，一个线程负责读标准输出流，另一个负责读标准错误流
                cmdReadComponent.readInputStream(is1);
                cmdReadComponent.readInputStream(is2);
                //可能导致进程阻塞，甚至死锁,所以必须读取输入流的内容避免死锁现象
                int waitFor = process.waitFor();
                //boolean isExit = process.waitFor(100, TimeUnit.SECONDS);
                int exitValue = process.exitValue();
                process.destroy();
                if (waitFor != 0) {
                    return waitFor;
                } else {
                    return exitValue;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                try {
                    process.getErrorStream().close();
                    process.getInputStream().close();
                    process.getOutputStream().close();
                } catch (Exception ee) {
                    return -1;
                }
            }
        }
        return -1;
    }
}
