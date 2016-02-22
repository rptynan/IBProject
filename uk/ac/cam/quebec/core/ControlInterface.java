/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.core;

import uk.ac.cam.quebec.twitterwrapper.TwitException;

/**
 *
 * @author James
 */
public interface ControlInterface {
    public void beginClose();
    public String getServerInfo();
    public boolean isRunning();
    public void initialiseUAPI();
    public void repopulateTrends() throws TwitException;
    public long timeUntilRepopulate();
    public void forceRepopulate();
    public void clearAllTasks();
}
