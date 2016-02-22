/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import uk.ac.cam.quebec.twitterwrapper.TwitException;

/**
 *
 * @author James
 */
public class TrendRefreshTask extends GenericTask {

    private final int delay;
    private final ControlInterface parent;
    private Thread thisThread = null;
    private static final int priority = 1000;//lowest getPriority core task
    private long sleeptime = 0;
    private long sleepstart = 0;

    public TrendRefreshTask(int _delay, ControlInterface _parent) {
        delay = _delay;
        parent = _parent;
    }

    @Override
    public Collection<Task> process() {

        ArrayList<Task> ret = new ArrayList<>();
        Task t = new Task(this, TaskType.Core);
        thisThread = Thread.currentThread();
        try {
            parent.repopulateTrends();
        } catch (TwitException ex) {
            System.err.println(ex);
            Throwable cause = ex.getCause();
            boolean b0 = cause != null;

        }
        try {
            sleepstart = System.currentTimeMillis();
            sleeptime = TimeUnit.MINUTES.toMillis(delay);
            Thread.sleep(sleeptime);

        } catch (InterruptedException ex) {

        }
        ret.add(t);
        return ret;
    }

    public long remainingTime() {
        return sleeptime + sleepstart - System.currentTimeMillis();

    }

    public void forceRefresh() {
        if (thisThread.getState().equals(Thread.State.TIMED_WAITING)) {
            thisThread.interrupt();
        }
    }
    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public String getStatus() {
        if (sleepstart == 0) {
            return "Refresh task is not running";
        } else {
            long time = remainingTime();
            String s = String.format("%d min, %d sec until repopulation", TimeUnit.MILLISECONDS.toMinutes(time),
                    TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
            return s;
        }
    }

}
