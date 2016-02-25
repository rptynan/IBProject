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
public abstract class GenericTask implements TaskInterface{
    private final TaskType type;
    protected GenericTask(TaskType _type)
    {
        type = _type;
    }
    @Override
    public abstract Collection<TaskInterface> process();
    
    @Override
    public abstract int getPriority();
        @Override
    public int compareTo(TaskInterface o) {
        return this.getPriority()-o.getPriority();
    }
    public abstract String getStatus();
    
    @Override
    public String toString()
    {
        return getStatus();
    }
    @Override
    public TaskType getType() {
       return type;
    }
}
