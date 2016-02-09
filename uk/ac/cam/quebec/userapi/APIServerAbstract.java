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
    public abstract APIServerAbstract create(Database DB,int port, TrendsQueue callback) throws IOException;
    @Override
    public abstract void run();

    public void begin()
    {
        this.start();
    }
}
