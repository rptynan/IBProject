/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.userapi.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.cam.quebec.userapi.UserAPIServer;

/**
 *
 * @author James
 */
public class Test extends Thread{
    private final UserAPIServer UAPI;
    private final int UAPIport;
    private final List<StringChat> testClients = new ArrayList<>();
    private final List<TestItem> tests;
    private static final int clientNumbers = 10;
    public Test(int port)
    {   UAPIport = port;
        UAPI = new UserAPIServer(UAPIport);
        UAPI.setDaemon(true);
        this.setDaemon(true);
        this.setName("UserAPIServer");
        StringChat s;
        for(int i=0; i<clientNumbers;i++)
        {s = new StringChat("127.0.0.1",UAPIport);
        s.setDaemon(true);
        s.setName("Test client number: "+i);
        testClients.add(s);
        }
        tests = buildTests(new ArrayList<TestItem>());
    }
    @Override
    public void run()
    {
        UAPI.start();
        for(StringChat s : testClients)
        {
            s.start();
            testSuite(s);
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
     * @param args No args are required for this
     */
    public static void main(String[] args)
    {try
    {
        Test t = new Test(90);
        t.run();
        String s = null;
        System.in.read();
        
    }
    catch (Exception ex)//not a problem here
    {
        System.err.println(ex);//break here to keep scope for debugging
    }
    }
}
