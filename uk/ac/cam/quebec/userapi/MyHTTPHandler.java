/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.userapi;

import com.sun.jndi.toolkit.url.Uri;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

/**
 *
 * @author James
 */
public class MyHTTPHandler  implements HttpHandler{
    private final NewAPIServer parent;
    public MyHTTPHandler(NewAPIServer _parent)
    {
        parent = _parent;
    }
            
    @Override
    public void handle(HttpExchange he) throws IOException {
        URI request = he.getRequestURI();
        String path = request.getPath();
        String query = request.getQuery();
        RequestType type = RequestType.getRequestType(path);
        handleRequest(he,type);
        
    }
    private void handleRequest(HttpExchange he,RequestType type) throws IOException
    {   switch(type)
    {
        default:
        sendMessage(type.toString(),he);
    }
        
    }
    private static void sendTweet(Tweet t, HttpExchange he)
    {
        
    }
    private static void sendMessage(String message,HttpExchange he) throws IOException
    {
        
        he.sendResponseHeaders(200, message.getBytes().length);
            OutputStream os = he.getResponseBody();
            os.write(message.getBytes());
            os.close();
    }
    
}
