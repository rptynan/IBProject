/*
 * Part IB tickwork written by James Brashko (jb705)
 * 
 */
package uk.ac.cam.quebec.userapi.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 *Extracted and de-anonymised inner class from StringChat
 * Handles receiving messages from the server
 * @author James
 */
public class StringChatInner extends Thread {

    private final Socket sock;

    /**
     *
     * @param socket the socket to read text from
     */
    public StringChatInner(Socket socket) {
        sock = socket;
    }

    @Override
    public void run() {
        InputStream in = null;
        BufferedReader data = null;
        try {
            in = sock.getInputStream();
            data = new BufferedReader(new InputStreamReader(in));
            String s;
            while (!(sock.isClosed()) && sock.isConnected()) {
                s = data.readLine();
                System.out.println(s);
            }
        } catch (IOException ex) {
            System.err.println(ex);
        } finally {
            System.out.println("Socket closing");
            try {
                if (!sock.isClosed()) {
                    sock.close();
                }
                if (in != null) {
                    in.close();
                }
                if (data != null) {
                    data.close();
                }

            } catch (IOException ex) {
                //nothing to do here, socket is dead
            }
        }
    }

}
