import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class SynchronizationC {
    public static ArrayList<String> runSynchronized(int numThreads){
        Warehouse a = new Warehouse(numThreads, "A");
        Warehouse b = new Warehouse(numThreads, "B");
        ArrayList<TraderThread> threads = new ArrayList<>();
        for(int i = 0; i<numThreads; i++){
            TraderThread th = new TraderThread(a,b,5);
            threads.add(th);
            th.start();
        }

        boolean finished = false;
        while(!finished) {
            finished = true;
            for (TraderThread thread : threads) {
                if (thread.isAlive()) {
                    finished = false;
                    break;
                }
            }
        }
        ArrayList<String> toRet = a.getInventory();
        toRet.addAll(b.getInventory());
        return toRet;

    }
}


class Warehouse{
    ArrayList<String> inventory;
    String name;

    Warehouse(int numThreads, String n){
        inventory = new ArrayList<>();
        name = n;
        for(int i = 0; i<numThreads; i++){
            inventory.add(n+String.valueOf(i));
        }
    }

    synchronized void addToWarehouse(String s){
        inventory.add(s);
    }
    synchronized String[] removeFromWarehouse(int numRemoved){
        String[] toRet = new String[numRemoved];
        for (int i = 0; i < numRemoved; i++) {
            Collections.shuffle(inventory);
            toRet[i] = inventory.remove(0);
        }
        return toRet;
    }

    ArrayList<String> getInventory(){
        return inventory;
    }
}

class TraderThread extends Thread{
    Warehouse a;
    Warehouse b;
    int numTrades;

    TraderThread(Warehouse a, Warehouse b, int numT){
        this.a = a;
        this.b = b;
        numTrades = numT;
    }

    @Override
    public void run() {
        if(Math.random()<0.5){
            Warehouse temp = b;
            b = a;
            a = temp;
        }
        for (int i = 0; i < numTrades; i++) {
            String[] purchases;
            synchronized (a){
                purchases = a.removeFromWarehouse(a.inventory.size()/2);
            }
            for (String purchase : purchases) {
                b.addToWarehouse(purchase);
            }
        }
    }
}