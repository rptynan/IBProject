/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author James
 */
public class TrendRefreshTask implements TaskInterface{
    private final int delay;
    private final ControlInterface parent;
    private static final int priority = 1000;//lowest priority core task
    private long sleeptime =0 ;
    private long sleepstart = 0;
    public TrendRefreshTask(int _delay, ControlInterface _parent)
    {
        delay = _delay;
        parent = _parent;
    }
    @Override
    public Collection<Task> process() {
        
        ArrayList<Task> ret = new ArrayList<>();
        Task t = new Task(this,TaskType.Core);
        parent.repopulateTrends();
        try {
            sleepstart = System.currentTimeMillis();
            sleeptime = TimeUnit.MINUTES.toMillis(delay);
            Thread.sleep(sleeptime);
            
        } catch (InterruptedException ex) {
            
        }
        ret.add(t);
        return ret;
    }
    public long remainingTime()
    {
        return sleeptime+sleepstart-System.currentTimeMillis();
        
    }
    @Override
    public int priority() {
        return this.priority();
    }

    @Override
    public int compareTo(TaskInterface o) {
        return this.priority()-o.priority();
    }
    
}
