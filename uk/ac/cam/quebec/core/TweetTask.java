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
public class TweetTask extends GenericTask{
    private final Trend trend;
    public TweetTask(Trend t)
    {   super(TaskType.Tweet);
        trend = t;
    }
   @Override
    public int getPriority() {
      return trend.getPriority();
    }

    @Override
    public int compareTo(TaskInterface o) {
        return this.getPriority()-o.getPriority();
    }

    @Override
    public Collection<Task> process() {
        System.out.println("Tweet task called for trend: "+trend.getParsedName());
       
       return null;
    } 

    @Override
    public String getStatus() {
        return "Tweet processing task for trend: "+trend.getParsedName();
    }
}
