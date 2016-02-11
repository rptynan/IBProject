/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.userapi;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;
import java.util.regex.Matcher;

import uk.ac.cam.quebec.trends.Trend;

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
        handleRequest(he,type,query);
        
    }
    private void handleRequest(HttpExchange he,RequestType type, String query) throws IOException
    {   String s = "";
        Matcher m = type.getPattern().matcher(query);
        if(m.find())
        {
         s+= "Found "+ m.groupCount()+" groups.";
        }
        else
        {
            s+= "Query matcher failed.";
        }
        switch(type)
    {
        case TrendsRequest:
          
            s = parent.getTrendsAsString("UK", "Popularity", 15);
            break;
        case ArticlesRequest:
            s = parent.getArticlesAsString(Integer.parseInt(m.group(1)),"Popularity",15);
            break;
        default:
            s+= " The Request type was: "+type.toString();
    }
        sendMessage(s,he);
    }
    private static void sendTweet(Object t, HttpExchange he)
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
