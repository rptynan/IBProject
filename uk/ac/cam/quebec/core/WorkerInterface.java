/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.core;

import java.util.Collection;

/**
 *
 * @author James
 */
public interface WorkerInterface {

    public boolean addTask(Task t);
    public boolean addTasks(Collection<Task> t);
    public void reallocateWorker(Worker aThis);
    
}
