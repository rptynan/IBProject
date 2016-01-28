/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.userapi.test;

import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author James
 */
public class TestClient extends Thread{
    private final Socket sock;
    public TestClient(int port) throws IOException
    {
        sock = new Socket("127.0.0.1",port);
    }
    @Override
    public void run()
    {
        
    }
}
