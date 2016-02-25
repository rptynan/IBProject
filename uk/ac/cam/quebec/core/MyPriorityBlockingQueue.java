/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.core;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * This is dumb but I am bored
 * With more time I could make a more involved set of resource locks
 * @author James
 * @param <T>
 */
public class MyPriorityBlockingQueue<T> extends PriorityBlockingQueue<T>{
    private final Semaphore contentsCount;
    //private final S workType;
    public MyPriorityBlockingQueue(Semaphore _contentsCount)
    {   super();
        contentsCount = _contentsCount;
        //workType = _workType;
    }
    /*
    public S getQueueType()
    {
        return workType;
    }*/
    @Override
    public boolean add(T item)
    {   
        boolean b = super.add(item);
        if(b)
        {
        contentsCount.release();
        }
        return b;
    }
    @Override
    public boolean offer(T item)
    {
        boolean b = super.offer(item);
        if(b)
        {
        contentsCount.release();
        }
        return b;
    }
    @Override
    public boolean offer(T item,long timeout,TimeUnit unit)
    {
        boolean b = super.offer(item,timeout,unit);
        if(b)
        {
        contentsCount.release();
        }
        return b;
    }
    @Override
    public void put(T item)
    {
        super.put(item);
        contentsCount.release();
    }
}
