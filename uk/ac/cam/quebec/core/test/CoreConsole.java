/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.core.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.cam.quebec.core.GroupProjectCore;
import uk.ac.cam.quebec.dbwrapper.Database;
import uk.ac.cam.quebec.twitterwrapper.TwitException;

/**
 *
 * @author James
 */
public class CoreConsole extends Thread{

    private final BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
    private final GroupProjectCore core;
    private final String[] twitterCreds;
    private boolean running = true;
    public static void main(String[] args) {
        try {
          //  
            Database.setCredentials("IBUser", "IBUserTest", "jdbc:mysql://localhost:3306/ibprojectdb");
            Database DB = Database.getInstance();
            GroupProjectCore core = new GroupProjectCore(args, DB);
            core.setDaemon(true);
            core.setName("CoreThread");
            CoreConsole c = new CoreConsole(core,args);
            c.run();
        } catch (Exception ex) {
System.err.println(ex);
        }
    }

    public void processCommand(String command) {
        if (command.equalsIgnoreCase("start"))
        { if(!core.isRunning())
        {
            core.start();
        }}
        else if (command.equalsIgnoreCase("exit")) {
            if(core.isRunning())
            {
            core.beginClose();
            running = false;
            }
        } else if (command.equalsIgnoreCase("status")) {
            System.out.println(core.getServerInfo());
        }
        else if (command.equalsIgnoreCase("test twitter"))
        {
            uk.ac.cam.quebec.twitterwrapper.test.Test.main(twitterCreds);
        }
    }

    public CoreConsole(GroupProjectCore _core, String[] _args) {
        core = _core;
        twitterCreds = _args;
    }

    @Override
    public void run() {
        try {
            String s;
            while ((running)&&((s = r.readLine()).length() > 0)) {
                processCommand(s);
            }
        } catch (IOException ex) {
            Logger.getLogger(CoreConsole.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
