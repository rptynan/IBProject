/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.userapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author James
 */
public class UserAPIClientInner extends Thread{
    private final UserAPIClient parent;
    private final Socket clientSocket;
    private final InputStream o;
    private final BufferedReader reader;
    public UserAPIClientInner(UserAPIClient _parent,Socket _clientSocket) throws IOException
    {
        parent = _parent;
        clientSocket = _clientSocket;
        o = clientSocket.getInputStream();
        reader = new BufferedReader(new InputStreamReader(o));
    }
    @Override
    public void run()
    {
        while((parent.Running())&&(clientSocket.isConnected())&&(!clientSocket.isClosed()))
        {
            try {    
                processMessage(reader.readLine());
            } catch (Exception ex) {
                try {
                    parent.innerError(ex);//lets handle the errors somewhere else
                } catch (Exception ex1) {
                Logger.getLogger(UserAPIClientInner.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }
        parent.close();
    }
    private void processMessage(String message)
    {
     parent.processMessage(message);
    }
    
}
