/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.core.test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.cam.quebec.core.GroupProjectCore;
import uk.ac.cam.quebec.core.TaskType;
import uk.ac.cam.quebec.trends.Trend;
import uk.ac.cam.quebec.twitterproc.TwitterProcessor;

/**
 *This is a dummy class that represents a worker thread and the object it is
 * working on
 * @author James
 */
public class Worker extends Thread implements Comparable{
    private BlockingQueue<Trend> o;
    private final TaskType type;
    private boolean running;
    private final GroupProjectCore parent;
    public Worker (TaskType _type,GroupProjectCore _parent)
    {
        o = new ArrayBlockingQueue<>(5);
        type = _type;
        parent = _parent;
    }
    public TaskType getWorkerType()
    {
        return type;
    }
    public void process (Trend _o)
    {
        o.add(_o);
    }
    public void process(Object _o)
    {
        
    }
    @Override
    public void run()
    {   running = true;
        while (running)
        {
        try {
            TwitterProcessor.process(o.take());
        } catch (InterruptedException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        }
        parent.reallocateWorker(this);
        }
    }
    @Override
    public int compareTo(Object o) {
       return 0;
    }
}
