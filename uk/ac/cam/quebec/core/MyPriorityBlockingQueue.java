/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.core;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * This is dumb but I am bored
 * With more time I could make a more involved set of resource locks
 * @author James
 * @param <T>
 */
public class MyPriorityBlockingQueue<T> extends PriorityBlockingQueue<T>{
    Semaphore contentsCount;
    public MyPriorityBlockingQueue(Semaphore _contentsCount)
    {   super();
        contentsCount = _contentsCount;
    }
    @Override
    public boolean add(T item)
    {   contentsCount.release();
        return super.add(item);
    }
}
