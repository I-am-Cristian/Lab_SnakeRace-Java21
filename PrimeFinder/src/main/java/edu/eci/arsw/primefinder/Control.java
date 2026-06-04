/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.primefinder;

import java.util.Scanner;

/**
 *
 */
public class Control extends Thread {
    
    private final static int NTHREADS = 3;
    private final static int MAXVALUE = 30000000;
    private final static int TMILISECONDS = 5000;
    private static Control control;
    private Scanner scanner;
    private volatile boolean pause = false;

    private final int NDATA = MAXVALUE / NTHREADS;
    private PrimeFinderThread pft[];
    
    private Control() {
        super();
        this.scanner = new Scanner(System.in);
        this.pft = new PrimeFinderThread[NTHREADS];

        int i;
        for(i = 0; i < NTHREADS - 1; i++) {
            PrimeFinderThread elem = new PrimeFinderThread(i * NDATA, (i + 1) * NDATA, this);
            pft[i] = elem;
        }
        pft[i] = new PrimeFinderThread(i * NDATA, MAXVALUE + 1, this);
    }
    
    public static Control newControl() {
        if (control == null) {
            control = new Control();
        }
        return control;
    }

    @Override
    public void run() {
        for(int i = 0; i < NTHREADS; i++) {
            pft[i].setName("Hilo-" + i);
            pft[i].start();
        }

        try {
            while(true) {
                // Esperar t milisegundos
                Thread.sleep(TMILISECONDS);

                // Pausa hilos
                System.out.println("Pausando hilos");
                pauseThreads();

                // Dar tiempo para que los hilos entren en wait
                Thread.sleep(100);

                // Muestra cuantos primos se han encontrado
                System.out.println("Primos encontrados hasta ahora: " + amountOfPrimesFound());

                // Esperar ENTER para reanudar
                System.out.print("Presione ENTER para reanudar");
                scanner.nextLine();
                
                // Reanudar hilos
                System.out.println("Reanudando hilos");
                resumeThreads();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public int amountOfPrimesFound() {
        int amount = 0;
        for (PrimeFinderThread t : pft) {
            amount += t.getAmountOfPrimes();
        }
        return amount;
    }

    public synchronized void pauseThreads() {
        pause = true;
    }

    public synchronized void resumeThreads() {
        pause = false;
        notifyAll();
    }

    public synchronized void checkPause() {
        while(pause) {
            try {
                wait();
            } catch (Exception e) {
                System.out.println("Error en wait: " + e.getMessage());
            }
        }
    }
}