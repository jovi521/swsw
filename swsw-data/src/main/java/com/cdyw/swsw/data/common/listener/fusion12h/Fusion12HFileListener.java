package com.cdyw.swsw.data.common.listener.fusion12h;


import com.cdyw.swsw.data.domain.service.fusion_12h.Fusion12HService;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Created by cdyw
 * 文件变化监听器
 * 在Apache的Commons-IO中有关于文件的监控功能的代码. 文件监控的原理如下：
 * 由文件监控类FileAlterationMonitor中的线程不停的扫描文件观察器FileAlterationObserver，
 * 如果有文件的变化，则根据相关的文件比较器，判断文件时新增，还是删除，还是更改。（默认为1000毫秒执行一次扫描）
 *
 * @author liudong
 * @modified jovi
 */
@Component
public class Fusion12HFileListener extends FileAlterationListenerAdaptor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    // 声明业务服务
    private final Fusion12HService fusion12HService;

    // 采用构造函数注入服务
    @Autowired
    public Fusion12HFileListener(Fusion12HService fusion12HService) {
        this.fusion12HService = fusion12HService;
    }

    // 文件创建执行
    @Override
    public void onFileCreate(File file) {
        System.out.println("[新建]:" + file.getAbsolutePath());
        String path = file.getAbsolutePath();
        System.out.println(Thread.currentThread().getName() + ":" + System.currentTimeMillis() + " path:" + path);
        try {
            // 等待5s,防止文件处于正在传输状态，未写入完成
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        fusion12HService.getParseData(path);
        System.out.println("文件创建了。。" + path);
    }

    // 文件创建修改
    @Override
    public void onFileChange(File file) {
    }

    // 文件创建删除
    @Override
    public void onFileDelete(File file) {
    }

    // 目录创建
    @Override
    public void onDirectoryCreate(File directory) {
    }

    // 目录修改
    @Override
    public void onDirectoryChange(File directory) {
    }

    // 目录删除
    @Override
    public void onDirectoryDelete(File directory) {
    }


    // 轮询开始
    @Override
    public void onStart(FileAlterationObserver observer) {
    }

    // 轮询结束
    @Override
    public void onStop(FileAlterationObserver observer) {
    }

}