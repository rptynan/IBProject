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
public class TrendTask extends GenericTask {

    private final Trend trend;

    public TrendTask(Trend _trend) {
        super(TaskType.Trend);
        trend = _trend;
    }

    @Override
    public int getPriority() {
        return trend.getPriority();
    }

    @Override
    public Collection<TaskInterface> process() {
        ArrayList<TaskInterface> ret = null;
        if (TwitterProcessor.doProcess(trend)) {
            ret = new ArrayList<>();
            WikiTask t = new WikiTask(trend);
            Task tsk = new Task(t);
            ret.add(tsk);
            TweetTask tweet = new TweetTask(trend);
            tsk = new Task(tweet);
            ret.add(tsk);
        }
        return ret;
    }

    @Override
    public String getStatus() {
        return "Trend processing task for trend: " + trend.getParsedName();
    }
}
