/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.userapi;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

import uk.ac.cam.quebec.dbwrapper.Database;
import uk.ac.cam.quebec.dbwrapper.DatabaseException;
import uk.ac.cam.quebec.trends.Trend;
import uk.ac.cam.quebec.trends.TrendsQueue;
import uk.ac.cam.quebec.wikiwrapper.WikiArticle;

/**
 *
 * @author James
 */
public class NewAPIServer extends APIServerAbstract {
    private final Database DB;
    private final InetSocketAddress addr;
    private final TrendsQueue callback;
    private final HttpServer server;

    public NewAPIServer(int _port) throws IOException {
        this(null, _port, null);
    }

    public NewAPIServer(Database _DB, int _port, TrendsQueue _callback)
            throws IOException {
        addr = new InetSocketAddress(_port);
        DB = _DB;
        callback = _callback;
        server = HttpServer.create(addr, 0);
        NewAPIServerExecutor execute = new NewAPIServerExecutor(this);
        MyHTTPHandler handler = new MyHTTPHandler(this);
        server.createContext("/", handler);
        server.setExecutor(null);
    }

    public Object getItemFromDB(int ID) {
        return null;
    }

    public String getArticlesAsString(int id, String sorting, int max) {
        try {
            List<WikiArticle> articleList = DB.getWikiArticles(id);
            // flag
            boolean itemAdded = false;
            String result = "[";
            for (WikiArticle a : articleList) {
                if (max <= 0)
                    break;
                if (itemAdded)
                    result += ",";
                max--;
                result += "{\"title\":\"" + a.getTitle() + "\", \"id\":"
                        + a.getId() + ", \"url\":\"" + a.getURL() + "\"}";
                itemAdded = true;
            }
            result += "]";
            return result;

        } catch (DatabaseException e) {
            return "[]";
        }
    }

    public String getTrendsAsString(String location, String sorting, int max) {
        try {
            List<Trend> trendList = DB.getTrends();
            // flag
            boolean itemAdded = false;
            String result = "[";
            for (Trend t : trendList) {
                if (max <= 0)
                    break;
                if (itemAdded)
                    result += ",";
                max--;
                result += "{\"name\":\"" + t.getName() + "\", \"id\":"
                        + t.getId() + "}";
                itemAdded = true;
            }
            result += "]";
            return result;

        } catch (DatabaseException e) {
            return "[]";
        }

    }

    public boolean addTrend(String trend) {
        if (callback == null) {
            System.err.println("Unable to add trend: " + trend
                    + ". No trend callback added");
            return false;
        } else {
            Trend T = new Trend(trend, "", 1);
            return callback.putTrend(T);
        }
    }

    @Override
    public APIServerAbstract create(Database DB, int port, TrendsQueue callback)
            throws IOException {
        NewAPIServer ret = new NewAPIServer(DB, port, callback);
        return ret;
    }

    @Override
    public void run() {
        server.start();
    }

}
