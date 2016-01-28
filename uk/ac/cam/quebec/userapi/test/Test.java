/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.userapi.test;

import uk.ac.cam.quebec.userapi.UserAPIServer;

/**
 *
 * @author James
 */
public class Test {
    public static void main(String[] args)
    {try
    {
        UserAPIServer test = new UserAPIServer(90);
        test.setDaemon(true);
        test.setName("UserAPIServer");
        test.start();
        String s = null;
        System.in.read();
        
    }
    catch (Exception ex)
    {
        System.err.println(ex);
    }
    }
}
