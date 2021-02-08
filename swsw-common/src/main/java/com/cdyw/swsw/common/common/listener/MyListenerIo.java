package com.cdyw.swsw.common.common.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;

/**
 * @author jovi
 */
@Slf4j
public class MyListenerIo implements FileAlterationListener {

    @Override
    public void onStart(FileAlterationObserver fileAlterationObserver) {
//        log.info("started " + fileAlterationObserver.getDirectory().getPath());
    }

    @Override
    public void onDirectoryCreate(File file) {
        log.info("directoryCreated " + file.getPath());
    }

    @Override
    public void onDirectoryChange(File file) {
        log.info("directoryChanged " + file.getPath());
    }

    @Override
    public void onDirectoryDelete(File file) {
        log.info("directoryDeleted " + file.getPath());
    }

    @Override
    public void onFileCreate(File file) {
        log.info("fileCreated " + file.getPath());
    }

    @Override
    public void onFileChange(File file) {
        log.info("fileChanged " + file.getPath());
    }

    @Override
    public void onFileDelete(File file) {
        log.info("fileDeleted " + file.getPath());
    }

    @Override
    public void onStop(FileAlterationObserver fileAlterationObserver) {
//        log.info("stopped " + fileAlterationObserver.getDirectory().getPath());
    }
}
