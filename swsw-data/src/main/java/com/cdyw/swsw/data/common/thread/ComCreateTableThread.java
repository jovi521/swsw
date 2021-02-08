package com.cdyw.swsw.data.common.thread;

/**
 * @author jovi
 */
public class ComCreateTableThread implements Runnable {

    @Override
    public void run() {
        System.out.println(">>>>>>>>>>>>>>" + Thread.currentThread().getId() + " : " + Thread.currentThread().getName());
    }
}
