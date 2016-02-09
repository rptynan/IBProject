/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.core.test;

import uk.ac.cam.quebec.core.TaskType;
import uk.ac.cam.quebec.trends.Trend;
import uk.ac.cam.quebec.twitterproc.TwitterProcessor;

/**
 *This is a dummy class that represents a worker thread and the object it is
 * working on
 * @author James
 */
public class Worker extends Thread implements Comparable{
    private Trend o;
    private final TaskType type;
    public Worker (TaskType _type)
    {
        o = null;
        type = _type;
    }
    public TaskType getWorkerType()
    {
        return type;
    }
    public void process (Trend _o)
    {
        o = _o;
    }
    public void process(Object _o)
    {
        
    }
    @Override
    public void run()
    {
        TwitterProcessor.process(o);
    }
    @Override
    public int compareTo(Object o) {
       return 0;
    }
}
