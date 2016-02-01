/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.userapi.test;

/**
 *
 * @author James
 */
public class TestException extends Exception{
    private Exception innerException;
    TestException(String _message)
    {   super(_message);

    }
    TestException(String _message, Exception _inner)
    {   super(_message);
        innerException = _inner;
    }
        
}
