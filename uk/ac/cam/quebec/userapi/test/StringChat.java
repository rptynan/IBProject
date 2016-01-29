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

/**
 * A very basic chat client
 * @author James
 */
public class StringChat {

    public static void main(String[] args) {
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
        /*sock is declared final because we should not have to replace the socket object once the program is running, declaring it final prevents us accidentaly
         replacing it instead of modifying it. Unfortunately it also prevented me from splitting it into a null declaration and assignment.
         */
        final Socket sock = getSocket(server, port);
        if (sock==null)
        {
            return;
        }
        try {
            

            Thread output = null;
            
                //Two classes in one file, so I de-anonimised and extracted it
                output = new StringChatInner(sock);
                output.setDaemon(true); //The JVM will kill the Daemon threads when the last non-Daemon thread dies. 
                output.start();
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
                BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
                String s;
                while ((s=r.readLine()).length()>0) {
                    out.write(s);
                    out.flush();
                }
                out.close();
                r.close();
                sock.close();
        } catch (IOException ex) {
            System.err.println(ex);
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
}
