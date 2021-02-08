package com.cdyw.swsw.data.common.notify.grapes3km;

import com.cdyw.swsw.data.domain.service.grapes_3km.Grapes3kmService;
import lombok.SneakyThrows;
import net.contentobjects.jnotify.JNotify;
import net.contentobjects.jnotify.JNotifyException;
import net.contentobjects.jnotify.JNotifyListener;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Grapes3kmNotifyClass extends Thread {

    private static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final String sourcePath;
    private final Grapes3kmService grapes3kmService;
    private List<String> container = Collections.synchronizedList(new ArrayList<>());

    public Grapes3kmNotifyClass(String sourcePath, Grapes3kmService grapes3kmService) {
        this.sourcePath = sourcePath;
        this.grapes3kmService = grapes3kmService;
    }


    @Override
    public void run() {
        // 设置监听的文件夹，或者文件
        String linstenerPath = sourcePath;
        // 指定监听的模式，创建
        int mask = JNotify.FILE_CREATED;
        // 是否监控子目录
        boolean watchSubtree = true;
        // 设置监听器，得到一个监听器id，可以通过这个id来移除监听器
        int watchId = -1;
        try {
            watchId = JNotify.addWatch(linstenerPath, mask, watchSubtree, new MyJnotifyListner());
        } catch (JNotifyException e) {
            e.printStackTrace();
        }
        // 启动监听容器内容线程，对内容进行处理
        new DynamicResolveFile().start();
        // 一定要让程序等待，只要程序不退出，那么上面的监听器就一直有效
        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    class MyJnotifyListner implements JNotifyListener {

        @Override
        public void fileCreated(int i, String rootPath, String name) {
            rootPath = rootPath.replaceAll("//", "/");
            String filePath = rootPath + name;
            logger.info(filePath + "创建了。。");
            container.add(filePath);
        }

        @Override
        public void fileDeleted(int i, String rootPath, String name) {

        }

        @Override
        public void fileModified(int i, String rootPath, String name) {

        }

        @Override
        public void fileRenamed(int i, String rootPath, String oldName, String newName) {

        }
    }

    /**
     * 监控容器，获取容器内容进行操作
     */
    class DynamicResolveFile extends Thread {


        @SneakyThrows
        @Override
        public void run() {
            String filePath;
            while (true) {
                filePath = "";
                for (int i = 0; i < container.size(); i++) {
                    filePath = container.get(0);
                    container.remove(0);
                    break;
                }
                if (!StringUtils.isEmpty(filePath)) {
                    // 防止文件写入未完成
                    Thread.sleep(30000);
                    fixedThreadPool.execute(new DynamicInsertFile(filePath));
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    class DynamicInsertFile implements Runnable {

        private final String filePath;

        public DynamicInsertFile(String filePath) {
            this.filePath = filePath;
        }

        @Override
        public void run() {
            try {
                grapes3kmService.getData(filePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
