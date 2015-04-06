/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bitcrystal.decentralizedexchange;

/**
 *
 * @author ABC
 */
public class NotInterruptableThread extends Thread {
    private boolean interrupt=false;

    NotInterruptableThread(Runnable runnable) {
        super(runnable);
    }
    
    public void setInterrupted(boolean set)
    {
        interrupt=set;
    }
    
    public boolean canInterrupted()
    {
        return interrupt;
    }
    
    @Override
    public void interrupt() {
        if(interrupt)
        {
            super.interrupt();
        }
    }
}
