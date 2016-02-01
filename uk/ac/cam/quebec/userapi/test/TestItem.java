/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.userapi.test;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author James
 */
public class TestItem {
    private final List<String> expectedResponses;
    private final String input;
    private int index = 0;
    private boolean valid = true;
    private String name;
    public TestItem(String _input,String _name)
    {
        input = _input;
        name = _name;
        expectedResponses = new ArrayList<>();
    }
    public void buildResponse(String a)
    {
        expectedResponses.add(a);
    }
    public String getInput()
    {
        return input;
    }
    public boolean check(String s) throws TestException
    {   try{
        String expected = expectedResponses.get(index);
        boolean b =(expected.equals(s));
        valid = b&&valid;
        index++;
        if(!b)
        {
            throw new TestException("Invalid response to line "+index+". "+s+" recieved but "+expected+" expected.");
        }
        return valid;
    }
    catch (Exception ex)
    {   valid = false;
        String message = "An exception occoured while processing line: "+index+" in test "+name;
        throw new TestException(message,ex);
    }
    }
    public boolean testFinished()
    {
        return (index==expectedResponses.size());
    }
    public boolean testPassed()
    {
        return testFinished()&&valid;
    }
    public String getName()
    {
        return name;
    }
    @Override
    public String toString()
    {
        return getName();
    }
}
