/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.core;

import java.util.ArrayList;
import java.util.Collection;
import uk.ac.cam.quebec.trends.Trend;
import uk.ac.cam.quebec.twitterproc.TwitterProcessor;

/**
 *
 * @author James
 */
public class TrendTask implements TaskInterface{
    private final Trend trend;
    public TrendTask(Trend _trend)
    {
        trend = _trend;
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
   if(true)
   {
        TwitterProcessor.process(trend);
       return null;
   }
       ArrayList<Task> ret = null;
       if(TwitterProcessor.doProcess(trend))
       {
       ret = new ArrayList<>();
       WikiTask t = new WikiTask(trend);
       Task tsk = new Task(t,TaskType.Wiki);
       ret.add(tsk);
       TweetTask tweet = new TweetTask(trend);
       tsk = new Task(tweet,TaskType.Tweet);
       ret.add(tsk);
       }
       return ret;
    }
    
}
