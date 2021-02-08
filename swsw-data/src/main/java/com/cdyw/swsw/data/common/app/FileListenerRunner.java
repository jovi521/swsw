package com.cdyw.swsw.data.common.app;

import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.util.Set;

/**
 * 文件监听启动类
 *
 * @author liudong
 * @modified jovi
 */
// @Component
public class FileListenerRunner implements ApplicationRunner {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final FileListenerFactory fileListenerFactory;

    @Autowired
    public FileListenerRunner(FileListenerFactory fileListenerFactory) {
        this.fileListenerFactory = fileListenerFactory;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (args != null) {
            String[] sourceArgs = args.getSourceArgs();
            for (String s : sourceArgs) {
                System.out.println("sourceArgs : " + s);
            }
            Set<String> optionNames = args.getOptionNames();
            for (String o : optionNames) {
                System.out.println("optionNames : " + o);
            }
        }
        // 创建监听者
        FileAlterationMonitor cldas1kmMonitor = fileListenerFactory.getCldas1kmMonitor();
        FileAlterationMonitor grapes3kmMonitor = fileListenerFactory.getGrapes3kmMonitor();
        FileAlterationMonitor fusion12hMonitor = fileListenerFactory.getFusion12hMonitor();
        FileAlterationMonitor radarPhaArrMonitor = fileListenerFactory.getRadarPhaArrMonitor();
        FileAlterationMonitor radarWeaMonitor = fileListenerFactory.getRadarWeaMonitor();
        FileAlterationMonitor radarWinProMonitor = fileListenerFactory.getRadarWinProMonitor();
        FileAlterationMonitor ecmwfHrMonitor = fileListenerFactory.getEcmwfHrMonitor();
        FileAlterationMonitor swcWarrMonitor = fileListenerFactory.getSwcWarrMonitor();
        FileAlterationMonitor swcWarmMonitor = fileListenerFactory.getSwcWarmMonitor();
        FileAlterationMonitor windfieldInversionMonitor = fileListenerFactory.getWindfieldInversionMonitor();
        try {
            // do not stop this thread
            cldas1kmMonitor.start();
            grapes3kmMonitor.start();
            fusion12hMonitor.start();
            radarPhaArrMonitor.start();
            radarWeaMonitor.start();
            radarWinProMonitor.start();
            ecmwfHrMonitor.start();
            swcWarrMonitor.start();
            swcWarmMonitor.start();
            windfieldInversionMonitor.start();
        } catch (Exception e) {
            grapes3kmMonitor.stop();
            fusion12hMonitor.stop();
            radarPhaArrMonitor.stop();
            radarWeaMonitor.stop();
            radarWinProMonitor.stop();
            ecmwfHrMonitor.stop();
            swcWarrMonitor.stop();
            swcWarmMonitor.stop();
            cldas1kmMonitor.stop();
            windfieldInversionMonitor.stop();
            logger.error(e.getMessage());
        }
    }

}
