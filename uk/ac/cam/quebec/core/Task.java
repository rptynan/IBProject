/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.core;

/**
 *
 * @author James
 */
public class Task {

    private final TaskInterface taskInterface;
    public Task (TaskInterface _taskInterface)
    {
        taskInterface = _taskInterface;
    }
    public TaskType getTaskType()
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
}
