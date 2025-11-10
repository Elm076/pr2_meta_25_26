package algorithms;

import config.Data;

import java.util.ArrayList;
import java.util.Random;

public class Greedy {
    private Data data;
    private int greedyListSize;
    private Random random;

    public Greedy(Long seed, Integer greedyListSize, Data data) {
        this.data = data;
        // this.greedyListSize = greedyListSize; // Podrías guardarlo
        // this.random = new Random(seed); // Podrías inicializar un random
    }

    public ArrayList<Integer> getSolution(){

        int n = data.n;

        // 1) Suma de flujos por unidad
        ArrayList<Integer> sumaFlujos = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int sum = 0;
            for (int j = 0; j < n; j++) {
                sum += data.flujos[i][j];
            }
            sumaFlujos.add(sum);
        }

        // 2) Suma de distancias por localización
        ArrayList<Integer> sumaDistancias = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int sum = 0;
            for (int j = 0; j < n; j++) {
                sum += data.distancias[i][j];
            }
            sumaDistancias.add(sum);
        }

        // 3) Arrays de índices
        ArrayList<Integer> unidades = new ArrayList<>();
        ArrayList<Integer> localizaciones = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            unidades.add(i);
            localizaciones.add(i);
        }

        // Ordenar unidades (flujos descendente)
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                if (sumaFlujos.get(unidades.get(j)) > sumaFlujos.get(unidades.get(i))) {
                    int temp = unidades.get(i);
                    unidades.set(i, unidades.get(j));
                    unidades.set(j, temp);
                }
            }
        }

        // Ordenar localizaciones (distancias ascendente)
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                if (sumaDistancias.get(localizaciones.get(j)) < sumaDistancias.get(localizaciones.get(i))) {
                    int temp = localizaciones.get(i);
                    localizaciones.set(i, localizaciones.get(j));
                    localizaciones.set(j, temp);
                }
            }
        }

        // 4) Construcción de solución
        ArrayList<Integer> solucion = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            solucion.add(0); // inicializamos con ceros
        }
        for (int i = 0; i < n; i++) {
            int unidad = unidades.get(i);
            int loc = localizaciones.get(i);
            solucion.set(unidad, loc); // USAR set en lugar de add
        }

        return solucion;
    }
}