/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.core;

import java.util.Collection;
import uk.ac.cam.quebec.trends.Trend;
import uk.ac.cam.quebec.twitterproc.TwitterProcessor;
import uk.ac.cam.quebec.wikiproc.WikiProcessor;

/**
 *
 * @author James
 */
public class WikiTask implements TaskInterface{
        private final Trend trend;
    public WikiTask(Trend _trend)
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
       WikiProcessor wp = new WikiProcessor();
       wp.process(trend);
       return null;
    }
    
}
