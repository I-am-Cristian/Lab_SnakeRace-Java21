package edu.eci.arsw.primefinder;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== PRIME FINDER ===");
        System.out.println("Buscando números primos hasta " + 30000000);
        System.out.println("Cada 5 segundos se pausará la búsqueda");
        System.out.println("Se mostrará el total de primos encontrados");
        System.out.println("Presione ENTER para reanudar\n");
        
        Control control = Control.newControl();
        control.start();
    }
}