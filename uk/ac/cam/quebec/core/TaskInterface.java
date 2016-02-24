/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.core;

import java.util.Collection;

/**
 * This interface is used by the various tasks to request runtime on the core thread pool
 * @author James
 */
public interface TaskInterface extends Comparable<TaskInterface>{
    /*
    This method should execute the tasks that you want to complete
    */
    public Collection<Task> process();
    /*
    This method should return the task's getPriority
    */
    public int getPriority();
    /*
    This method should return which module the task is associated with
    */
    public TaskType getType();
    
}
