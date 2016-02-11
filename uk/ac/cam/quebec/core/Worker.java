/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.core;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.cam.quebec.core.test.TrendTask;
import uk.ac.cam.quebec.trends.Trend;

/**
 *This is a dummy class that represents a worker thread and the object it is
 * working on
 * @author James
 */
public class Worker extends Thread implements Comparable{
    private final BlockingQueue<Task> o;//Todo: replace with semaphores
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
       TrendTask t = new TrendTask(_o);
       process(t);
    }
    public void process(Task _task)
    {
        o.add(_task);
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
            Task t = o.take();
            t.getTaskInterface().process();
        } catch (Exception ex) {
            System.err.println("Error processing task");
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
