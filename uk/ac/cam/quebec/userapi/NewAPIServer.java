/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.userapi;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import uk.ac.cam.quebec.dbwrapper.Database;
import uk.ac.cam.quebec.trends.Trend;
import uk.ac.cam.quebec.trends.TrendsQueue;

/**
 *
 * @author James
 */
public class NewAPIServer  extends APIServerAbstract {
    private final Database DB;
    private final InetSocketAddress addr;
    private final TrendsQueue callback;
    private final HttpServer  server;
    private boolean running;
    public NewAPIServer(int _port) throws IOException
    {
        this(null,_port,null);
    }
    public NewAPIServer(Database _DB, int _port, TrendsQueue _callback) throws IOException {
            addr = new InetSocketAddress(_port);
            DB = _DB;
            callback = _callback;
            server = HttpServer.create(addr, 0);
            NewAPIServerExecutor execute = new NewAPIServerExecutor(this);
            MyHTTPHandler handler = new MyHTTPHandler(this);
            server.createContext("/", handler);
            server.setExecutor(null);   
    }
    public Object getItemFromDB(int ID)
    {
        return null;
    }
    public boolean addTrend(String trend)
    {if (callback == null) {
            System.err.println("Unable to add trend: " + trend+". No trend callback added");
            return false;
        } else {
        Trend T = new Trend(trend,"",1);
        return callback.putTrend(T);
    }
    }
    @Override
    public void run() {
        running = true;
      server.start();
    }

    @Override
    public String getStatus() {
      if(running())
      {
          return "User API server running";
      }
      else
      {
          return "User API server not running";
      }
    }

    @Override
    public boolean running() {
       return running;
    }

    @Override
    public void close() {
        running = false;
        System.out.println("User API server stopping");
        server.stop(30);
    }
    

}
