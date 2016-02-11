/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.core.test;

import uk.ac.cam.quebec.core.TaskInterface;
import uk.ac.cam.quebec.core.TaskType;

/**
 *
 * @author James
 */
public class TestTask implements TaskInterface{
    private final Object object;
    public TestTask(Object _o)
    {
        object = _o;
    }

    @Override
    public void process() {
        System.err.println("Test task for object: "+object.toString()+" has been called");
    }

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public int compareTo(TaskInterface o) {
        return this.priority()-o.priority();
    }
    
}
