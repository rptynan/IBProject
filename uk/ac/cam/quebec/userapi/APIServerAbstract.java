/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.userapi;

import java.io.IOException;
import uk.ac.cam.quebec.dbwrapper.Database;
import uk.ac.cam.quebec.trends.TrendsQueue;

/**
 *
 * @author James
 */
public abstract class APIServerAbstract extends Thread{
    @Override
    public abstract void run();
    public abstract boolean running();
    public abstract String getStatus();
    public abstract void close();
}
