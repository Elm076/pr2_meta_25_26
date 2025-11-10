package algorithms;

// Importa la clase Data que lee flujos y distancias
import config.Data;
import dataStructures.Pair;

import java.util.ArrayList;

public class Utilitys {

    /**
     * Operador de mutación. Intercambia las localizaciones de dos unidades.
     * @param list La solución (permutación)
     * @param a Índice de la primera unidad
     * @param b Índice de la segunda unidad
     * @return Una nueva lista con las localizaciones intercambiadas
     */
    public static ArrayList<Integer> TwoOpt(ArrayList<Integer> list, Integer a, Integer b){
        ArrayList<Integer> solution = (ArrayList<Integer>) list.clone();
        int oldA = solution.get(a);
        solution.set(a,solution.get(b));
        solution.set(b,oldA);
        return solution;
    }

    /**
     * NUEVA FUNCIÓN DE EVALUACIÓN (QAP)
     * Calcula el coste total de una asignación.
     * Coste = Sum(flujo[i][j] * distancia[sol[i]][sol[j]])
     * @param solution La permutación actual (ArrayList<Integer>)
     * @param data Objeto que contiene las matrices de flujos y distancias
     * @return El coste total como Double
     */
    public static Double EvaluationFunction(ArrayList<Integer> solution, Data data) {
        int n = data.n;
        // Usamos double para ser consistentes con el resto del código (elites, etc.)
        double cost = 0.0;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // locI es la localización asignada a la unidad i
                int locI = solution.get(i);
                // locJ es la localización asignada a la unidad j
                int locJ = solution.get(j);

                // Sumamos el coste: flujo(i,j) * distancia(locI, locJ)
                // Hacemos cast a double para la multiplicación
                cost += (double)data.flujos[i][j] * (double)data.distancias[locI][locJ];
            }
        }

        return cost;
    }
}