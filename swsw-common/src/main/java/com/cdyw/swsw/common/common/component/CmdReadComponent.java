package com.cdyw.swsw.common.common.component;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author cdyw
 * @description
 */
@Component
public class CmdReadComponent {

    /**
     * 读取流中的数据(cmd输入流和错误流)
     *
     * @param is
     */
    @Async("asyncServiceExecutor")
    public void readInputStream(InputStream is) {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        try {
            String line;
            while ((line = br.readLine()) != null) {
                if (line != null) {
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
