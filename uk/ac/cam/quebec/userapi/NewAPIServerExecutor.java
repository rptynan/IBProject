/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.userapi;

import java.util.concurrent.Executor;

/**
 *
 * @author James
 */
public class NewAPIServerExecutor implements Executor {
    private final NewAPIServer parent;
    public NewAPIServerExecutor(NewAPIServer _parent)
    {
        parent = _parent;
    }
    @Override
    public void execute(Runnable command) {
       
    }
    
}
