package config;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Data {
    private String filename;
    public int n;
    public int[][] flujos;
    public int[][] distancias;

    public Data(String filename) throws FileNotFoundException {
        this.filename = filename;
        leerFichero(filename);
    }

    private void leerFichero(String filename) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(filename));

        n = sc.nextInt(); // número de unidades/localizaciones
        flujos = new int[n][n];
        distancias = new int[n][n];

        // leer matriz de flujos
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                flujos[i][j] = sc.nextInt();
            }
        }

        // leer matriz de distancias
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                distancias[i][j] = sc.nextInt();
            }
        }

        sc.close();
    }

    // Método para mostrar datos (para pruebas)
    public void imprimirDatos() {
        System.out.println("n = " + n);

        System.out.println("Matriz de flujos:");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(flujos[i][j] + " ");
            }
            System.out.println();
        }

        System.out.println("Matriz de distancias:");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(distancias[i][j] + " ");
            }
            System.out.println();
        }
    }

    public String getFilename() {return filename;}
}
