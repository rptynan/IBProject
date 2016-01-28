/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.quebec.core.test;

import uk.ac.cam.quebec.trends.Trend;

/**
 *
 * @author James
 */
public class TestTrend extends Trend{

    public TestTrend(String name, String location, int priority) {
        super(name, location, priority);
    }

    @Override
    public void incrementProcessCount() {
      
    }
    
}
