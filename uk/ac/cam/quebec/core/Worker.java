/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.core;

import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.cam.quebec.core.test.TestTask;
import uk.ac.cam.quebec.trends.Trend;

/**
 *This is a dummy class that represents a worker thread and the object it is
 * working on
 * @author James
 */
public class Worker extends Thread implements Comparable{
    private BlockingQueue<Task> o;//Todo: replace with semaphores
    private final TaskType type;
    private boolean running;
    private final WorkerInterface parent;
    public Worker (TaskType _type,GroupProjectCore _parent)
    {
        o = new ArrayBlockingQueue<>(1);
        type = _type;
        parent = _parent;
    }
    public TaskType getWorkerType()
    {
        return type;
    }
    public boolean process (Trend _o)
    {
       TrendTask t = new TrendTask(_o);
       Task task = new Task(t,TaskType.Trend);
       return process(task);
    }
    /**
    Sets a task to be processed
     * @param _task the task to be processed
     * @return If the task was successfully set for processing
    */
    public boolean process(Task _task)
    {   if(_task==null)
    {System.err.println("Null task assigned");
        return false;
    }
        try{
        return o.add(_task);
    }
    catch (IllegalStateException ex)
    {   //this means the Queue is full
        return false;
    }
        catch (Exception ex)
        {
            throw ex;//debugging hook
        }
    }
    public boolean processObject(Object _o)
    {TestTask tst = new TestTask(_o);
    Task task = new Task(tst,TaskType.Core);
       return process(task);
       
    }
    @Override
    public void run()
    {   running = true;
        while (running)
        {
        try {
            Task t = o.take();
            Collection<Task> process = t.getTaskInterface().process();
            boolean addTask = parent.addTasks(process);
            if(!addTask)
            {
                System.err.println("Error adding tasks from task: "+t.toString());
            }
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
