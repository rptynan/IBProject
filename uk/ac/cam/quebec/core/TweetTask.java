/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.core;

import java.util.Collection;
import uk.ac.cam.quebec.trends.Trend;

/**
 *
 * @author James
 */
public class TweetTask implements TaskInterface{
    private final Trend trend;
    public TweetTask(Trend t)
    {
        trend = t;
    }
   @Override
    public int priority() {
      return trend.getPriority();
    }

    @Override
    public int compareTo(TaskInterface o) {
        return this.priority()-o.priority();
    }

    @Override
    public Collection<Task> process() {
        System.out.println("Tweet task called for trend: "+trend.getParsedName());
       //TwitterProcessor.process(trend);
       return null;
       /*WikiTask t = new WikiTask(trend);
       Task tsk = new Task(t,TaskType.Wiki);
       ArrayList<Task> ret = new ArrayList<>();
       return ret;
        */
    } 
}
