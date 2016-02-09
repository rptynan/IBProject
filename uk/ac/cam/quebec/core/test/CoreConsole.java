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
public class CoreConsole {
 private BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
 private final GroupProjectCore core;
    public static void main(String args[]) throws IOException, TwitException
 {          Database DB = Database.getInstance();
            GroupProjectCore core = new GroupProjectCore(args,DB);
            core.setDaemon(true);
            core.setName("CoreThread");            
 }
 public void processCommand(String command)
 {
     if(command.equalsIgnoreCase("exit"))
     {
         core.beginClose();
     }
     else if(command.equalsIgnoreCase("status"))
     {
         System.out.println(core.getServerInfo());
     }
         }
 public CoreConsole(GroupProjectCore _core)
 {
     core = _core;
 }
 public void run() 
 {  core.start();
     try {
         String s;
         while ((s = r.readLine()).length() > 0) {
             processCommand(s);
         }
     } catch (IOException ex) {
         Logger.getLogger(CoreConsole.class.getName()).log(Level.SEVERE, null, ex);
     }
 }
}
