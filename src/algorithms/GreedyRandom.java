package algorithms;

import config.Data;

import java.util.ArrayList;
import java.util.Random;

public class GreedyRandom {
    private Long seed;
    private Random random;
    private Data datos;
    private Integer extraParam;

    public GreedyRandom(long seed, Integer greedyListSize, Data datos, Integer extraParam) {
        this.seed = seed;
        this.datos = datos;
        this.extraParam = extraParam;
        this.random = new Random(this.seed); // We initialize here the pseudo-randomness using the given seed
        }

        public ArrayList<Integer> execute(){

        int n = datos.n;

        // 1) Suma de flujos por unidad
        ArrayList<Integer> sumaFlujos = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int suma = 0;
            for (int j = 0; j < n; j++) {
                suma += datos.flujos[i][j];
            }
            sumaFlujos.add(suma);
        }

        // 2) Suma de distancias por localización
        ArrayList<Integer> sumaDistancias = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int suma = 0;
            for (int j = 0; j < n; j++) {
                suma += datos.distancias[i][j];
            }
            sumaDistancias.add(suma);
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

            /* IMPORTANTE, solo necesitamos poner esta línea el el bloque de código de abajo cada vez que necesitemos
            una unidad y una localización (se llamaría 2 veces por iteración del for). el valor que nos devuelva el random
            lo utilizamos para acceder como índice a la eedd en vez del "i" que tenemos ahora.
            Vease:   "int unidad = unidades.get(i);
                      int loc = localizaciones.get(i);"

            random.nextInt(extraParam-1); // -1 due to the range starts from 0 and we want exactly "extraParam" range
            */
            ArrayList<Integer> solucion = new ArrayList<>(n);

            for (int i = 0; i < n; i++) {
                solucion.add(0); // inicializamos con ceros
            }
            int unidadActual = -1;
            int localizacionActual = -1;
            int unidad = -1;
            int loc = -1;
            for (int i = 0; i < n; i++) {
                if (i < n - extraParam){
                    unidadActual = random.nextInt(extraParam-1); // -1 due to the range starts from 0 and we want "extraParam" range
                    localizacionActual = random.nextInt(extraParam-1); // -1 due to the range starts from 0 and we want "extraParam" range
                }
                else {
                    unidadActual = random.nextInt(n-i);
                    localizacionActual = random.nextInt(n-i);
                }
                unidad = unidades.get(unidadActual);
                loc = localizaciones.get(localizacionActual);

                unidades.remove(unidadActual);
                localizaciones.remove(localizacionActual);

                solucion.set(unidad, loc); // USAR set en lugar de add
            }

        return solucion;
    }

}
