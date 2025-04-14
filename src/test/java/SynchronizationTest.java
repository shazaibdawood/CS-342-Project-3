import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class SynchronizationTest {
    @RepeatedTest(5)
    void testSynchronizedA(){
        assertEquals(500.0,SynchronizationA.runSynchronized(999),"incorrect Synchronization on ClassA");
    }

    @RepeatedTest(5)
    void testSynchronizedB(){
        try {
            double[] accounts = SynchronizationB.runSynchronized(100);
            for(double account: accounts){
                assertEquals(100.0,account, "incorrect synchronization for B");
            }
        } catch(Exception e){
            fail();
        }

    }
    @RepeatedTest(5)
    void testSynchronizedC(){
        int numT = 100;
        ArrayList<String> tR = SynchronizationC.runSynchronized(numT);
        ArrayList<String> testResult = (ArrayList<String>) tR.clone();

        if(testResult.size() < numT*2) fail("items lost in warehouses");
        if(testResult.size() > numT*2) fail("items duplicated in warehouses");

        for(String elem : tR){
            testResult.remove(elem);
            if(testResult.contains(elem)) fail("duplicated object in warehouse");
        }
    }


}


