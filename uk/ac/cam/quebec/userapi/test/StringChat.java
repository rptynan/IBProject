/*
 * Part IB tickwork written by James Brashko (jb705)
 * 
 */
package uk.ac.cam.quebec.userapi.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A very basic chat client, converted to use queues for its input and output
 * needs
 * @author James
 */
public class StringChat extends Thread{
    private final Socket sock;
    private final BlockingQueue<String> inputMessages; //
    private final List<String> sentMessages;
    private final BlockingQueue<String> outputMessages; //= new ArrayBlockingQueue<>();
    private final List<String> recievedMessages;
    private StringChatInner output;
    private boolean running;
    public StringChat(String server, int port)
    {
        inputMessages = new ArrayBlockingQueue<>(20);
        outputMessages = new ArrayBlockingQueue<>(20);
        sentMessages = new ArrayList<>();
        recievedMessages = new ArrayList<>();
        /*sock is declared final because we should not have to replace the socket object once the program is running, declaring it final prevents us accidentaly
         replacing it instead of modifying it. Unfortunately it also prevented me from splitting it into a null declaration and assignment.
         */
        sock = getSocket(server, port);
        if (sock==null)
        {
            return;
        }
    }
    @Override
    public void run()
    { running = true;
    BufferedWriter out = null;
    output = null;
    BufferedReader r = null;
        try {
            
            //Two classes in one file, so I de-anonimised and extracted it
                output = new StringChatInner(sock,this);
                output.setDaemon(true); //The JVM will kill the Daemon threads when the last non-Daemon thread dies. 
                output.setName(this.getName()+" inner");
                output.start();
                out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
                r = new BufferedReader(new InputStreamReader(System.in));
                while (running) {
                    out.write(inputMessages.take());
                    out.flush();
                }
                out.close();
                r.close();
                sock.close();
        } catch (IOException ex) {
            System.err.println(ex);
        } catch (InterruptedException ex) {
           System.err.println(this.getName()+" thead interrupted");
        }
        finally
        {         
            if(!sock.isClosed())
            { 
                try {
                    sock.close();
                } catch (IOException ex) {
                    //because netbeans does not like try/finally
                } finally
                {
                    
                }
            }
        }
    }
    public synchronized void close()
    {
        running = false;
        State s = this.getState();
        Thread t = Thread.currentThread();
        boolean c = t.equals(s);
        System.out.println("Closing "+this.getName());
        if((s.equals(Thread.State.WAITING)))
        {   System.out.println("Interrupting "+this.getName());
            this.interrupt();
        }
    }
    public void sendMessage(String message)
    {   sentMessages.add(message);
        inputMessages.add(message);
    }
    public void recievedMessage(String message)
    {   if(message!=null)
    {
        recievedMessages.add(message);
        outputMessages.add(message);
    }
     else//this should never happen, but sometimes it does :(
    {
        System.err.println("null recieved from server");
    }
    }
    /**
     * This should notify the parent thread if an exception happens in the 
     * inner thread 
     * @param e the exception that is thrown
     */
    public void InnerError(Exception e)
    {
        System.err.println(e);
    }
    public BlockingQueue<String> getOutputMessages()
    {
        return outputMessages;
    }
    /**
     * Makes a Socket to the host with the port provided
     * or returns null
     * @param hostName name of the host
     * @param port port on the host to connect to
     * @return null or a connected socket
     */
    public static Socket getSocket(String hostName, int port)
    {
        try {
            return new Socket(hostName,port);
        }
        catch (SocketException ex)
        {
            System.out.println("Cannot connect to "+hostName+" on port "+port);
        }
       catch (UnknownHostException ex) {
               System.err.println("Unknown host");
            System.err.println(ex);
        } catch (IOException ex) {
            System.err.println(ex);
        }
        return null;
    }
    /**
     * Old start point for the StringChat class
     * @param args [String host name, int port number]
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        String server = null;
        int port = 0;
        try {

            if (args.length == 2) {
                server = args[0];
                port = Integer.parseInt(args[1]);
            } else {
                System.out.println("This application requires two arguments: <machine> <port>");
                return;
            }
        } catch (NumberFormatException ex) {
            System.out.println("This application requires two arguments: <machine> <port>");
            return;
        }
        StringChat stuffs = new StringChat(server,port);
        stuffs.sendMessage("Test");
        stuffs.start();
        BlockingQueue<String> m = stuffs.getOutputMessages();
        while(stuffs.isAlive()||(!m.isEmpty()))
        {
            System.out.println(m.take());
        }
    }
}
