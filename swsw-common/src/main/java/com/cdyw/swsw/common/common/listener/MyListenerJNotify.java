package com.cdyw.swsw.common.common.listener;

import lombok.extern.slf4j.Slf4j;
import net.contentobjects.jnotify.JNotifyListener;

/**
 * @author jovi
 */
@Slf4j
public class MyListenerJNotify implements JNotifyListener {

    @Override
    public void fileRenamed(int wd, String rootPath, String oldName, String newName) {
        log.info("fileRenamed " + rootPath + " : " + oldName + " -> " + newName);
    }

    @Override
    public void fileModified(int wd, String rootPath, String name) {
        log.info("fileModified " + rootPath + " : " + name);
    }

    @Override
    public void fileDeleted(int wd, String rootPath, String name) {
        log.info("fileDeleted " + rootPath + " : " + name);
    }

    @Override
    public void fileCreated(int wd, String rootPath, String name) {
        log.info("fileCreated " + rootPath + " : " + name);
    }

}
