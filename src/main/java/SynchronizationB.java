import java.util.ArrayList;

public class SynchronizationB {
    public static double[] runSynchronized(int numThreads){
        Banker b = new Banker(numThreads);
        ArrayList<BankerThread> threads = new ArrayList<>();


        for(int i = 0; i<numThreads; i++){
            BankerThread th = new BankerThread(b,numThreads,10.0*i,i);
            threads.add(th);
            th.start();
        }

        boolean finished = false;
        while(!finished) {
            finished = true;
            for (BankerThread thread : threads) {
                if (thread.isAlive()) {
                    finished = false;
                    break;
                }
            }
        }
        return b.getAccounts();
    }
}

class Banker{
    double[] accounts;
    Banker(int numAccounts){
        accounts = new double[numAccounts];
        for(int i = 0; i<numAccounts; i++){
            accounts[i] = 100.0;
        }
    }

    double addToAccount(int index, double value){
        synchronized(accounts){
            accounts[index] = accounts[index] + value;
            return accounts[index];
        }
    }
    double withdrawFromAccount(int index, double value) {
        synchronized(accounts) {
            if(accounts[index]-value < 0) throw new NegativeWithdrawlException();
            accounts[index] = accounts[index] - value;
            return accounts[index];
        }
    }
    double[] getAccounts(){
        return accounts;
    }
}

class BankerThread extends Thread{
    Banker banker;
    int numTransactions;
    double transactionSize;
    int index;

    BankerThread(Banker b, int numTrans, double transSize, int ind){
        banker = b;
        numTransactions = numTrans;
        transactionSize = transSize;
        index = ind;
    }
    @Override
    public void run() {
        for(int i = 0; i<numTransactions; i++){
            for(int ind = 0; ind<banker.accounts.length; ind++) {
                banker.addToAccount(ind, transactionSize);
            }
        }
        for(int i = 0; i<numTransactions; i++){
            for(int ind = 0; ind<banker.accounts.length; ind++) {
                banker.withdrawFromAccount(ind, transactionSize);
            }
        }
    }
}

class NegativeWithdrawlException extends RuntimeException{
    @Override
    public String getMessage() {
        return "An account is overdrawn";
    }
}