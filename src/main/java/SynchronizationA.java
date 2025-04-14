import java.util.ArrayList;

public class SynchronizationA {

    public static double runSynchronized(int numThreads){
        Counter counter = new Counter(numThreads);
        ArrayList<ThreadA> threads = new ArrayList<>();

        for(int i = 1; i<numThreads/4; i++){
            ThreadA newThread = new ThreadA(counter);
            threads.add(newThread);
            newThread.start();
        }


        boolean finished = false;
        while(!finished) {
            finished = true;
            for (ThreadA thread : threads) {
                if (thread.isAlive()) {
                    finished = false;
                    break;
                }
            }
        }

        int[] result = counter.getValues();
        double sum = 0.0;
        for(int i = 0; i<result.length; i++){
            sum = sum + result[i];
        }
        return (sum / result.length);
    }
}

class Counter{
    int[] values;

    Counter(int numValues){
        values = new int[numValues];
        for(int i = 0; i<numValues; i++){
            values[i] = i+1;
        }
    }

    int addToSmallest(){
        int currSmallest = 1001;
        int currIndex = -1;
        for(int i = 0; i<values.length; i++){
            if (values[i]<currSmallest){
                currSmallest = values[i];
                currIndex = i;
            }
        }
        synchronized (this) {
            values[currIndex]++;
        }

        return values[currIndex];
    }
    int subtractFromLargest(){
        int currLargest = -1;
        int currIndex = -1;
        for(int i = 0; i<values.length; i++){
            if (values[i]>currLargest){
                currLargest = values[i];
                currIndex = i;
            }
        }
        synchronized (this) {
            values[currIndex]--;
        }
        return values[currIndex];
    }
    int[] getValues(){
        return values;
    }
}

class ThreadA extends Thread{
    Counter counter;
    int numThreads;
    ThreadA(Counter c){
        counter = c;
        numThreads = c.values.length;

    }
    @Override
    public void run(){
        for(int i = 1; i<numThreads/2; i++){
                counter.addToSmallest();
                counter.subtractFromLargest();
        }
    }
}
