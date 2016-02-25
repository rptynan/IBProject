/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.core;

import java.util.Collection;

/**
 * This acts as a wrapper for the task interfaces
 * @author James
 */
public class Task implements TaskInterface{

    private final TaskInterface taskInterface;
    public Task (TaskInterface _taskInterface)
    {
        taskInterface = _taskInterface;
    }
    public TaskType getType()
    {
        return taskInterface.getType();
    }
    public TaskInterface getTaskInterface()
    {
        return taskInterface;
    }
   @Override
    public String toString()
    {
        return taskInterface.getType().name() +" : "+taskInterface.toString();
    }
    public Collection<TaskInterface> process()
    {
        return taskInterface.process();
    }
    public int getPriority()
    {
        return taskInterface.getPriority();
    }
    @Override
    public int compareTo(TaskInterface o) {
        return taskInterface.compareTo(o);
    }
    
}
