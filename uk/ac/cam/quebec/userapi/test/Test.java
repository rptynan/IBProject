/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.userapi.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.cam.quebec.userapi.APIServerAbstract;
import uk.ac.cam.quebec.userapi.NewAPIServer;
import uk.ac.cam.quebec.userapi.UserAPIServer;

/**
 * Class that handles the testing of the UserAPI server
 * @author James
 */
public class Test extends Thread{
    private final APIServerAbstract UAPI;
    private final int UAPIport;
    private final List<StringChat> testClients = new ArrayList<>();
    private final List<TestItem> tests;
    private static final int clientNumbers = 0;
    public Test(int port) throws IOException
    {   UAPIport = port;
        
    if(true)
    {
        UAPI = new NewAPIServer(UAPIport);
    }
        else
    {
        UAPI = new UserAPIServer(UAPIport);
    }StringChat s;
        for(int i=0; i<clientNumbers;i++)
        {s = new StringChat("127.0.0.1",UAPIport);
        s.setDaemon(true);
        s.setName("Test client number: "+i);
        testClients.add(s);
        }
        tests = buildTests(new ArrayList<>());
    
        UAPI.setDaemon(true);
        this.setDaemon(true);
        UAPI.setName("UserAPIServer");
    
    }
    @Override
    public void run()
    {
        UAPI.start();
        for(StringChat s : testClients)
        {
            s.start();
            if(testSuite(s))
            {
                System.out.println("Test passed");
            }
            s.close();
        }
        String tmp = "a";
        System.out.println(tmp);
    }
    /**
     * Runs an instance of the test suite on a StringChat instance
     * @param s the StringChat client to run it on
     * @return returns true if the tests run successfully
     */
    public boolean testSuite(StringChat s)
    {   boolean b = true;
        boolean b0;
        boolean b1;
        for(TestItem t : tests)
    {   b0 = true;
        b1 = false;
        s.sendMessage(t.getInput());
        BlockingQueue<String> q = s.getOutputMessages();
        while(!t.testFinished()){
        String a;
        try {
            a = q.take();
            b0 = b0&&t.check(a);
        } catch (InterruptedException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TestException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }}
        b1 = t.testPassed();
    }
        return b;
    }
    /**
     * Builds the list of tests that the tester will run
     * @param l The list to add the Test items to.
     * @return The list that the Test items are added to. Can be ignored.
     */
    public static List<TestItem> buildTests(List<TestItem> l)
    {
        TestItem t = new TestItem("testing","Test1");
        t.buildResponse("HTTP/1.0 200 OK Content-Type: text/html");
        t.buildResponse("<html>");
        
        l.add(t);
        return l;
    }
    /**
     * Starts the test suite for the user API, will hang waiting for console input
     * @param args [int port] If the first argument is a number it will listen on that port
     * otherwise it will listen on the default port.
     */
    public static void main(String[] args)
    {Test t;
    int port =90;
        try
    {               if(args.length>0)
            {
                String s = args[0];
                try{
                port = Integer.parseInt(s);
                }
                catch(NumberFormatException ex)
                {
                    port = 90;
                }
            }
        t = new Test(port);
        t.run();
        System.in.read();
        
    }
    catch (Exception ex)
    {
        System.err.println(ex);//break here to keep scope for debugging
    }
        finally
        {
            //t.close(); Do any needed cleanup here
        }
    }
}
