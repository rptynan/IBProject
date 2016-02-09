/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.userapi;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.cam.quebec.dbwrapper.Database;
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
    @Override
    public APIServerAbstract create(Database DB, int port, TrendsQueue callback) throws IOException  {
      NewAPIServer ret = new NewAPIServer(DB,port,callback);
      return ret;
    }

    @Override
    public void run() {
      server.start();
    }

}
