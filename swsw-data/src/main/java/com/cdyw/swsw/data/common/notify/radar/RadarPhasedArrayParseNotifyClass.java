package com.cdyw.swsw.data.common.notify.radar;

import com.cdyw.swsw.common.common.util.FileUtils;
import com.cdyw.swsw.common.domain.ao.enums.TypeEnum;
import com.cdyw.swsw.data.domain.service.radar.RadarPhasedArrayService;
import lombok.SneakyThrows;
import net.contentobjects.jnotify.JNotify;
import net.contentobjects.jnotify.JNotifyException;
import net.contentobjects.jnotify.JNotifyListener;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RadarPhasedArrayParseNotifyClass extends Thread {

    private static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final TypeEnum TYPE = TypeEnum.TYPE_RADAR_PHASED_ARRAY;
    private final String sourcePath;
    private final RadarPhasedArrayService radarPhasedArrayService;
    private List<String> container = Collections.synchronizedList(new ArrayList<>());

    public RadarPhasedArrayParseNotifyClass(String sourcePath, RadarPhasedArrayService radarPhasedArrayService) {
        this.sourcePath = sourcePath;
        this.radarPhasedArrayService = radarPhasedArrayService;
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
            File fileSource = new File(filePath);
            String tmpFile = "tmp";
            // 对部分产品进行过滤
            String EB = "EB";
            String ET = "ET";
            String VIL = "VIL";
            String RZ = "RZ";
            String MAXREF = "MAXREF";
            String CRPZ = "CRPZ";
            String EBPZ = "EBPZ";
            String ETPZ = "ETPZ";
            String VILPZ = "VILPZ";
            String RZPZ = "RZPZ";
            if (filePath.contains("/ZCD01") || filePath.contains("/ZCD02") || filePath.contains("/ZCD03")) {
                if (filePath.contains(EB) || filePath.contains(ET) || filePath.contains(VIL) || filePath.contains(RZ) || filePath.contains(MAXREF)) {
                    // 进入方法之前必须判断文件是否有效
                    if (!fileSource.getName().contains(tmpFile)) {
//                        logger.info("filePath:" + filePath);
                        // 将文件添加到容器中
                        container.add(filePath);
                    } else {
                        logger.info(TYPE.getName() + " 新增无效文件：" + fileSource.getName() + "。请检查数据源是否正确");
                    }
                }
            } else if (filePath.contains("/Multistation")) {
                if (filePath.contains(MAXREF) || filePath.contains(CRPZ) || filePath.contains(EBPZ) || filePath.contains(ETPZ) || filePath.contains(VILPZ) || filePath.contains(RZPZ)) {
                    // 进入方法之前必须判断文件是否有效
                    if (!fileSource.getName().contains(tmpFile)) {
//                        logger.info("filePath:" + filePath);
                        // 将文件添加到容器中
                        container.add(filePath);
                    } else {
                        logger.info(TYPE.getName() + " 新增无效文件：" + fileSource.getName() + "。请检查数据源是否正确");
                    }
                }
            }
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
            boolean flag;
            while (true) {
                flag = false;
                filePath = "";
                for (int i = 0; i < container.size(); i++) {
                    filePath = container.get(0);
                    container.remove(0);
                    // 检查该文件是否正在被写入，如果是，则将其塞入容器
                    if (FileUtils.isFileWriting(filePath)) {
                        container.add(filePath);
                        flag = true;
                    }
                    break;
                }
                if (flag) {
                    continue;
                }
                if (!StringUtils.isEmpty(filePath)) {
                    Thread.sleep(1000);
                    fixedThreadPool.execute(new DynamicInsertFile(filePath));
//                    logger.info("线程池中线程数目：" + ((ThreadPoolExecutor) fixedThreadPool).getActiveCount() + "，队列中等待执行的任务数目：" +
//                            ((ThreadPoolExecutor) fixedThreadPool).getQueue().size() + "，已执行玩别的任务数目：" + ((ThreadPoolExecutor) fixedThreadPool).getCompletedTaskCount());
                } else {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
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
            int size = radarPhasedArrayService.insertRadarPhasedArrayParse(new File(filePath));
//            logger.info(TypeEnum.TYPE_RADAR_PHASED_ARRAY.getName() + " onFileCreated: " + size + " 条数据");
        }
    }


}
