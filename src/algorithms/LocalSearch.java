package algorithms;

import config.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class LocalSearch {
    private ArrayList<Integer> solution;
    private Integer solutionValue;
    private final Long seed;
    private final Integer numIterations;
    private Random random;
    private Data data;

    public LocalSearch(ArrayList<Integer> origSolution, Integer solutionValue, Long seed, Data data, Integer numIterations){
        this.solution = origSolution;
        this.solutionValue = solutionValue;
        this.seed = seed;
        this.numIterations = numIterations;
        this.random = new Random(this.seed);
        this.data = data;
    }

    public ArrayList<Integer> calculateSolution(){
        int counterIte = 0;

        ArrayList<Integer> currentSolution = solution;

        boolean carryOn = false;

        // DLB mask to avoid exploring again the same solution
        ArrayList<Integer> DLBmask = new ArrayList<>(currentSolution.size());
        HashSet<Integer> DLBmaskValidPositionsCounter = new HashSet<>(currentSolution.size());
        for (int i = 0; i < currentSolution.size(); i++){
            DLBmask.add(0);
            DLBmaskValidPositionsCounter.add(i);
        }

        while (!DLBmaskValidPositionsCounter.isEmpty()){
            for (int i = 0; i < solution.size(); i++){
                if (DLBmask.get(i) == 0){
                    carryOn = false;
                }
                for (int j = i + 1; j < solution.size(); j++){
                    if (checkMove(i,j)){
                        applyMove(i,j);
                        counterIte++;
                        DLBmask.set(i,0);
                        DLBmask.set(j,0);
                        DLBmaskValidPositionsCounter.add(i);
                        DLBmaskValidPositionsCounter.add(j);
                        carryOn = true;
                    }
                }
                if (!carryOn){
                    DLBmask.set(i,1);
                    DLBmaskValidPositionsCounter.remove(i);
                }
                if (counterIte >= numIterations){
                    break;
                }
                if (DLBmaskValidPositionsCounter.isEmpty()){
                    break;
                }

            }
        }
        return solution;
    }
    public ArrayList<Integer> getSolution(){
        return solution;
    }

    public int getSolutionValue(){
        return solutionValue;
    }

    private boolean checkMove(int i, int j){
        int newSolutionValue = solutionValue;
        //int greedySolutionValue = 0; // To check if the factorization is correct
        // We calculate the difference in cost by swapping i and j

        for (int k = 0; k < solution.size(); k++){
            if (k != i && k != j){
                /*
                //To check if the factorization is correct
                applyMove(i, j);
                greedySolutionValue = calculateCost(solution, data);
                applyMove(j,i);
                */

                int locK = solution.get(k);
                int locI = solution.get(i);
                int locJ = solution.get(j);

                newSolutionValue   += 2 * (data.flujos[i][k] * (data. distancias[locJ][locK] - data. distancias[locI][locK]))
                        +
                        2 * (data.flujos[j][k] * (data. distancias[locI][locK] - data. distancias[locJ][locK]));
            }
        }
        if (newSolutionValue < solutionValue){
            /*
            // To check if the factorization is correct
            System.out.println("Greedy value:" + greedySolutionValue + " Factorization value: " + newSolutionValue);
            System.out.println();
             */
            solutionValue = newSolutionValue;
            return true;
        }
        return false;
    }

    private void applyMove(int i,int j){
        int aux = solution.get(i);
        solution.set(i,solution.get(j));
        solution.set(j,aux);
    }

    public int calculateCost(ArrayList<Integer> solucion, Data datos) {
        int n = datos.n;
        int coste = 0;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int locI = solucion.get(i);
                int locJ = solucion.get(j);
                coste += datos.flujos[i][j] * datos.distancias[locI][locJ];
            }
        }

        return coste;
    }

}