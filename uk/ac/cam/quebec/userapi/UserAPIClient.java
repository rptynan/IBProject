/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.userapi;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author James
 */
public class UserAPIClient extends Thread{
    
    private final Socket clientSocket;
    private final int clientID;
    private boolean running = false;
    private final UserAPIServer parent;
    private final UserAPIClientInner inner;
    private final PrintWriter out;
    private final BlockingQueue<String> MessageQueue;
    public UserAPIClient(Socket client,int _clientID,UserAPIServer _parent) throws IOException
    {
        clientSocket = client;
        clientID = _clientID;
        parent = _parent;
        inner = new UserAPIClientInner(this,client);
        inner.setDaemon(true);
        inner.setName("Client number: "+clientID+" inner");
        out = new PrintWriter(clientSocket.getOutputStream());
        MessageQueue = new ArrayBlockingQueue<>(6);
    }
    public boolean Running()
    {
        return running;
    }
    @Override
    public void run()
    {
        running = true;
        inner.start();
        try {
            buildMessage("hi");
        } catch (IOException ex) {
            Logger.getLogger(UserAPIClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        while(running)
        {   if(true){
            break;}
            try {
                buildMessage(MessageQueue.take());
            } catch (InterruptedException ex) {
                System.err.println(ex);
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }
        close();
    }
    private void buildMessage(String message) throws IOException
    {   String content = "<html>"+"\r\n";
        //content += "<Title>Test title</Title>"+"\r\n";
        content += "<body>"+"\r\n";
        content += "<p>"+message+"</p>\r\n";
        content += "</body>"+"\r\n";
        content += "</html>";
        String s = getDateString();
        String tmp = "HTTP/1.0 200 OK ";
        //tmp +="Date: Fri, 31 Dec 1999 23:59:59 GMT"+"\r\n";
        //tmp += "Date: "+s+"\r\n";
        //  tmp +="Server: Apache/0.8.4"+"\r\n";
        //  tmp +="Server: Crude java server V 0.0.1"+"\r\n";
        tmp +="Content-Type: text/html"+"\r\n";
        //tmp +="Content-Length: "+content.length()+"\r\n";
        //tmp +="Expires: Sat, 01 Jan 2100 00:59:59 GMT"+"\r\n";
        //tmp +="Expires: "+s+"\r\n";
        //tmp +="Last-modified: Fri, 09 Aug 1996 14:21:40 GMT"+"\r\n";
        //tmp += "Last-modified: "+s+"\r\n";
          tmp += content+"\r\n";
          out.println(tmp);
          out.flush();
          clientSocket.getOutputStream().flush();
                  
        /*out.write("HTTP/1.0 200 OK");
          out.newLine();
          out.write("Date: Fri, 31 Dec 1999 23:59:59 GMT");
          out.newLine();
          out.write("Server: Apache/0.8.4");
          out.newLine();
          out.write("Content-Type: text/html");
          out.newLine();
          out.write("Content-Length: "+content.length());
          out.newLine();
          out.write("Expires: Sat, 01 Jan 2100 00:59:59 GMT");
          out.newLine();
          out.write("Last-modified: Fri, 09 Aug 1996 14:21:40 GMT");
          out.newLine();
          out.write(content);
          out.newLine();
          out.flush();*/
    }
    private String buildHeader(String content)
    {   String s = getDateString();
        String tmp = "HTTP/1.0 200 OK ";
        tmp += "Date: "+s+"\r\n";
        tmp +="Server: Crude java server V 0.0.1"+"\r\n";
        tmp +="Content-Type: text/html"+"\r\n";
        tmp +="Content-Length: "+content.length()+"\r\n";
        tmp +="Expires: "+s+"\r\n";
        tmp += "Last-modified: "+s+"\r\n";
        return tmp;
        
    }
    private void buildJson(Object o)
    {String tmp = "HTTP/1.0 200 OK"+"\r\n";
     tmp += "Content-Type: application/vnd.api+json"+"\r\n";
        
    }
    public void close()
    {
        if(running)
        {
            running = false;
            parent.removeClient(this);
        }
        if(out != null)
        {
            out.close();
        }
        if(clientSocket.isConnected())
        {
            try {
                clientSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(UserAPIClient.class.getName()).log(Level.SEVERE, null, ex);//something died when trying to close socket
            }
        }
    }
    public void innerError(Throwable ex)
    {   
        System.err.println(ex);
    }
    public void processMessage(String message) {
        System.out.println(message);
    }
    private static String getDateString()
    {
        if(true)
        {
            return "Fri, 31 Dec 1999 23:59:59 GMT";
        }
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zS");
    return format.format(cal);
    }
}
