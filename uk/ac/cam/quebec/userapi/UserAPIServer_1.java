/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.userapi;

import uk.ac.cam.quebec.core.test.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import uk.ac.cam.quebec.dbwrapper.Database;
import uk.ac.cam.quebec.trends.Trend;
import uk.ac.cam.quebec.trends.TrendsQueue;

/**
 *
 * @author James
 */
public class UserAPIServer extends APIServerAbstract{
    private final ServerSocket serverSocket;
    private boolean running = false;
    private final List<Socket> clientList = new ArrayList<>();
    private Thread ServerThread;
    private final List<UserAPIClient> APIclientList = new ArrayList<>();
    private int clientNumber = 0;
    private final Database db;
    private final TrendsQueue callback;
    public UserAPIServer(int port)
    {
        serverSocket = makeSocket(port);
        db=null;
        callback =null;
    }

    public UserAPIServer(int port, Database DB, TrendsQueue _callback) {
        serverSocket = makeSocket(port);
        db = DB;
        callback = _callback;
        
    }
    @Override
    public void run()
    {running = true;
    ServerThread = Thread.currentThread();
    Socket client = null;
    UserAPIClient APIclient = null;
        while(running)
        {
        try {
            client = null;
            APIclient = null;
            client = serverSocket.accept();
            clientList.add(client);
            APIclient = new UserAPIClient(client,clientNumber,this);
            APIclientList.add(APIclient);
            APIclient.setDaemon(true);
            APIclient.setName("Client number: "+clientNumber);
            APIclient.start();
            clientNumber ++;
        } catch (IOException ex) {
            System.err.println(ex);
        }
        }
    }
    private static ServerSocket makeSocket(int port)
    {
        try {
            ServerSocket server = new ServerSocket(port);
            return server;
        } catch (IOException ex) {
            System.err.println(ex);
            return null;
        }
        
    }
    public void addUserTrend(String str)
    {
        if(callback==null)
        {
            System.out.println("adding trend: "+str);
        }
        else
        {   Trend t = new TestTrend(str,"",1);
            callback.putTrend(t);
        }
    }
    public void removeClient(UserAPIClient c)
    {
        
    }
    public void close()
    {
        running = false;
        APIclientList.stream().forEach(UserAPIClient::close);
        ServerThread.interrupt();
    }


    @Override
    public APIServerAbstract create(Database DB, int port, TrendsQueue callback) {
        UserAPIServer tmp = new UserAPIServer(port, DB,callback);
        return tmp;
    }

    @Override
    public void begin() {
        this.start();
    }
}
