/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.userapi.test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author James
 */
public class TestItem {
    private final List<Pattern> expectedResponses;
    private final String input;
    private int index = 0;
    private boolean valid = true;
    private final String name;
    /**
     * Creates a new test item with no expected responses
     * @param _input The input that we are going to send to the server
     * @param _name The name of this test
     */
    public TestItem(String _input,String _name)
    {
        input = _input;
        name = _name;
        expectedResponses = new ArrayList<>();
    }
    /**
     * Creates a new test item
     * @param _input The input that we are going to send to the server
     * @param _name The name of this test
     * @param _responses The list of Patterns that we expect in response
     */
    public TestItem(String _input,String _name, List<Pattern> _responses)
    {
        input = _input;
        name = _name;
        expectedResponses = _responses;
    }
    public void buildResponse(String s)
    {   Pattern p = Pattern.compile(s);
        expectedResponses.add(p);
    }
    public String getInput()
    {
        return input;
    }
    public boolean check(String s) throws TestException
    {   try{
        Pattern expected = expectedResponses.get(index);
        String regex = expected.pattern();
        Matcher m = expected.matcher(s);
        boolean b = m .matches();
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
