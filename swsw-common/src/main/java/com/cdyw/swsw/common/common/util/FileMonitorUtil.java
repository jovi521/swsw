package com.cdyw.swsw.common.common.util;

import cn.hutool.core.date.DateUtil;
import com.cdyw.swsw.common.common.listener.MyListenerIo;
import com.cdyw.swsw.common.common.listener.MyListenerJNotify;
import com.cdyw.swsw.common.domain.ao.enums.FileMonitorTypeEnum;
import net.contentobjects.jnotify.JNotify;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author jovi
 */
public class FileMonitorUtil {

    /**
     * 根据文件监控的类型和监控路径，具体实现监控步骤:
     * 1: IO实现-主要借助common-io实现
     * 2: JDK实现-主要借助 WatchService 实现
     * 3: JNotify实现-主要借助开源工具实现，注意将jnotify_64bit.dll文件复制到 c:/windows 下；在Linux上，可以复制libjnotify.so到/usr/lib64目录下。
     *
     * @param fileMonitorTypeEnum FileMonitorTypeEnum
     * @param fileMonitorPath     String
     * @throws IOException          io
     * @throws InterruptedException ie
     */
    @SuppressWarnings("InfiniteLoopStatement")
    public static void fileMonitor(FileMonitorTypeEnum fileMonitorTypeEnum, String fileMonitorPath) throws Exception {
        switch (fileMonitorTypeEnum) {
            case TYPE_IO:
                // 轮询间隔 3 秒
                long interval = TimeUnit.SECONDS.toMillis(3);
                // 创建一个文件观察器用于处理文件的格式,过滤文件格式
                FileAlterationObserver observerFilter = new FileAlterationObserver(
                        fileMonitorPath,
                        FileFilterUtils.and(FileFilterUtils.fileFileFilter(), FileFilterUtils.suffixFileFilter(".xxx")),
                        null);
                FileAlterationObserver observer = new FileAlterationObserver(fileMonitorPath);

                //设置文件变化监听器
                observerFilter.addListener(new MyListenerIo());
                observer.addListener(new MyListenerIo());
                //创建文件变化监听器
                FileAlterationMonitor monitor = new FileAlterationMonitor(interval, observerFilter, observer);
                // 开始监控
                monitor.start();
                break;
            case TYPE_JDK:
                WatchService watchService = FileSystems.getDefault().newWatchService();
                Paths.get(fileMonitorPath).register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.OVERFLOW);
                while (true) {
                    WatchKey key = watchService.take();
                    List<WatchEvent<?>> watchEvents = key.pollEvents();
                    for (WatchEvent<?> event : watchEvents) {
                        if (StandardWatchEventKinds.ENTRY_CREATE == event.kind()) {
                            System.out.println("created " + fileMonitorPath + "/" + event.context());
                        } else if (StandardWatchEventKinds.ENTRY_MODIFY == event.kind()) {
                            System.out.println("modified " + fileMonitorPath + "/" + event.context());
                        } else if (StandardWatchEventKinds.ENTRY_DELETE == event.kind()) {
                            System.out.println("deleted " + fileMonitorPath + "/" + event.context());
                        } else if (StandardWatchEventKinds.OVERFLOW == event.kind()) {
                            System.out.println("overflowed " + fileMonitorPath + "/" + event.context());
                        }
                    }
                    boolean reset = key.reset();
                    System.out.println("本次监控于" + DateUtil.formatLocalDateTime(LocalDateTime.now()) + "完成情况：" + (reset ? "是" : "否"));
                }
            case TYPE_JNOTIFY:
                boolean res;
                int watchIdCreate = JNotify.addWatch(fileMonitorPath, JNotify.FILE_CREATED, true, new MyListenerJNotify());
                res = JNotify.removeWatch(watchIdCreate);
                if (!res) {
                    System.out.println(false);
                }
                int watchIdDelete = JNotify.addWatch(fileMonitorPath, JNotify.FILE_DELETED, true, new MyListenerJNotify());
                res = JNotify.removeWatch(watchIdDelete);
                if (!res) {
                    System.out.println(false);
                }
                int watchIdModified = JNotify.addWatch(fileMonitorPath, JNotify.FILE_MODIFIED, true, new MyListenerJNotify());
                res = JNotify.removeWatch(watchIdModified);
                if (!res) {
                    System.out.println(false);
                }
                int watchIdRenamed = JNotify.addWatch(fileMonitorPath, JNotify.FILE_RENAMED, true, new MyListenerJNotify());

                res = JNotify.removeWatch(watchIdRenamed);
                if (!res) {
                    System.out.println(false);
                }
                break;
            default:
                break;
        }
    }
}
