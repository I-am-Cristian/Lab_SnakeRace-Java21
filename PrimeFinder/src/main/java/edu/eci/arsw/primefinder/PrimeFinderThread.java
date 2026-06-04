package edu.eci.arsw.primefinder;

import java.util.LinkedList;
import java.util.List;

public class PrimeFinderThread extends Thread{
    
    int a,b;
    private List<Integer> primes;
    private Control control;
    
    public PrimeFinderThread(int a, int b, Control control) {
        super();
        this.primes = new LinkedList<>();
        this.a = a;
        this.b = b;
        this.control = control;
    }
    
    @Override
    public void run(){
        for (int i = a; i < b; i++){	
            control.checkPause();
            
            if (isPrime(i)){
                synchronized(primes) {
                    primes.add(i);
                }
            System.out.println(Thread.currentThread().getName() + ": " + i);
            }
        }
        System.out.println(Thread.currentThread().getName() + " ha terminado su rango!");
    }
    
    boolean isPrime(int n) {
        boolean ans;
        if (n > 2) { 
            ans = n % 2 != 0;
            for(int i = 3; ans && i * i <= n; i += 2 ) {
                ans = n % i != 0;
            }
        } else {
            ans = n == 2;
        }
        return ans;
    }
    
    public int getAmountOfPrimes() {
        synchronized(primes) {
            return primes.size();
        }
    }
    
    public List<Integer> getPrimes() {
        return primes;
    }
}