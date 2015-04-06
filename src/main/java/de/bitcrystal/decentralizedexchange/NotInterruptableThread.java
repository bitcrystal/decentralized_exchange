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
public class NotInterruptableThread extends Thread {

    private boolean interrupt = false;

    NotInterruptableThread() {
        super();
    }

    NotInterruptableThread(Runnable runnable) {
        super(runnable);
    }

    public NotInterruptableThread(String name) {
        super(name);
    }

    public NotInterruptableThread(Runnable target, String name) {
        super(target, name);
    }

    public NotInterruptableThread(ThreadGroup group, Runnable target) {
        super(group, target);
    }

    public NotInterruptableThread(ThreadGroup group, String name) {
        super(group, name);
    }

    public NotInterruptableThread(ThreadGroup group, Runnable target, String name) {
        super(group, target, name);
    }

    public NotInterruptableThread(ThreadGroup group, Runnable target, String name, long stackSize) {
        super(group, target, name, stackSize);
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
