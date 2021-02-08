package com.cdyw.swsw.data.common.listener.grapes3km;


import com.cdyw.swsw.data.domain.service.grapes_3km.Grapes3kmService;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author cdyw
 * 文件变化监听器
 * 在Apache的Commons-IO中有关于文件的监控功能的代码. 文件监控的原理如下：
 * 由文件监控类FileAlterationMonitor中的线程不停的扫描文件观察器FileAlterationObserver，
 * 如果有文件的变化，则根据相关的文件比较器，判断文件时新增，还是删除，还是更改。（默认为1000毫秒执行一次扫描）
 */
@Component
public class Grapes3kmFileListener implements FileAlterationListener {

    // 声明业务服务
    private final Grapes3kmService grapes3kmService;

    @Autowired
    public Grapes3kmFileListener(Grapes3kmService grapes3kmService) {
        this.grapes3kmService = grapes3kmService;
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
        try {
            grapes3kmService.getData(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
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