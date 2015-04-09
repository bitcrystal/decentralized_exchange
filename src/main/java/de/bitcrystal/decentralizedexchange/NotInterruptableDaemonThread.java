/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bitcrystal.decentralizedexchange;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ABC
 */
public class NotInterruptableDaemonThread extends Thread {

    private boolean interrupt = false;

    NotInterruptableDaemonThread() {
        super();
        setDaemon(true);
    }

    NotInterruptableDaemonThread(Runnable runnable) {
        super(runnable);
        setDaemon(true);
    }

    public NotInterruptableDaemonThread(String name) {
        super(name);
        setDaemon(true);
    }

    public NotInterruptableDaemonThread(Runnable target, String name) {
        super(target, name);
        setDaemon(true);
    }

    public NotInterruptableDaemonThread(ThreadGroup group, Runnable target) {
        super(group, target);
        setDaemon(true);
    }

    public NotInterruptableDaemonThread(ThreadGroup group, String name) {
        super(group, name);
        setDaemon(true);
    }

    public NotInterruptableDaemonThread(ThreadGroup group, Runnable target, String name) {
        super(group, target, name);
        setDaemon(true);
    }

    public NotInterruptableDaemonThread(ThreadGroup group, Runnable target, String name, long stackSize) {
        super(group, target, name, stackSize);
        setDaemon(true);
    }

    public void setInterrupted(boolean set) {
        interrupt = set;
    }

    public boolean canInterrupted() {
        return interrupt;
    }

    @Override
    public void interrupt() {
        if (interrupt) {
            super.interrupt();
        }
    }
}
