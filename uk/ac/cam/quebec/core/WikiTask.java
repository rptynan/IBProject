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
public class WikiTask extends GenericTask{
        private final Trend trend;
    public WikiTask(Trend _trend)
    {   super(TaskType.Wiki);
        trend = _trend;
    }
    @Override
    public int getPriority() {
      return trend.getPriority();
    }
    @Override
    public Collection<Task> process() {
       WikiProcessor wp = new WikiProcessor();
       wp.process(trend);
       return null;
    }
        @Override
    public String getStatus()
    {
        return "Wiki processing task for trend: "+trend.getParsedName();
    }
}
