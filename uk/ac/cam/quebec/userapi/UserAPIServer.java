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
 * This class creates the Server that listens for new client API connections
 * @author James
 */
public class UserAPIServer extends APIServerAbstract {

    private final ServerSocket serverSocket;
    private boolean running = false;
    private final List<Socket> clientList = new ArrayList<>();
    private final List<UserAPIClient> APIclientList = new ArrayList<>();
    private int clientNumber = 0;
    private final Database db;
    private final TrendsQueue callback;

    /**
     * Create an isolated instance of the server for debugging
     *
     * @param port The port to listen on
     */
    public UserAPIServer(int port) {
        serverSocket = makeSocket(port);
        db = null;
        callback = null;
    }

    /**
     * Create a full instance of the server
     *
     * @param port The port to listen on
     * @param DB The Database wrapper to use
     * @param _callback the callback used to add new Trends
     */
    public UserAPIServer(int port, Database DB, TrendsQueue _callback) {
        serverSocket = makeSocket(port);
        db = DB;
        callback = _callback;

    }

    @Override
    public void run() {
        running = true;
        Socket client;
        UserAPIClient APIclient;
        while (running) {
            try {
                client = null;
                APIclient = null;
                client = serverSocket.accept();
                clientList.add(client);
                APIclient = new UserAPIClient(client, clientNumber, this);
                APIclientList.add(APIclient);
                APIclient.setDaemon(true);
                APIclient.setName("Client handler number: " + clientNumber);
                APIclient.start();
                clientNumber++;
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }
    }
    /**
     * Constructs a ServerSocket object listening on the given port
     * @param port the port to listen on
     * @return A ServerSocket object if creation is successful, null otherwise
     */
    private static ServerSocket makeSocket(int port) {
        try {
            ServerSocket server = new ServerSocket(port);
            //TODO: set further options here
            return server;
        } catch (IOException ex) {
            System.err.println(ex);
            return null;
        }

    }
    
    public void addUserTrend(String str) {
        if (callback == null) {
            System.out.println("Unable to add trend: " + str+". No trend callback added");
        } else {
            Trend t = new TestTrend(str, "", 1);
            callback.putTrend(t);
        }
    }

    public void removeClient(UserAPIClient c) {

    }

    @Override
    public synchronized void close() {
        if (running) {
            running = false;
            APIclientList.stream().forEach(UserAPIClient::close);
            if (this.getState().equals(Thread.State.BLOCKED)) {
                this.interrupt();
            }
        }
    }

    @Override
    public boolean running() {
return running;
    }

    @Override
    public String getStatus() {
        if(running())
        {
            return "User API server is running";
        }
        else
        {
            return "User API server is not running";
        }
    }
}
