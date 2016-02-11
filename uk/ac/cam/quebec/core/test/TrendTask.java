/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.core.test;

import uk.ac.cam.quebec.core.TaskInterface;
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
    public void process() {
        TwitterProcessor.process(trend);
    }

    @Override
    public int priority() {
      return trend.getPriority();
    }

    @Override
    public int compareTo(TaskInterface o) {
        return this.priority()-o.priority();
    }
    
}
