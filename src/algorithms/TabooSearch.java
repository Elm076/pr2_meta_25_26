package algorithms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import config.Data;
import dataStructures.LinkedList;
import dataStructures.PairGeneric;

public class TabooSearch {
    private ArrayList<Integer> solution;
    private Integer solutionValue;
    private final Long seed;
    private final Integer numIterations;
    private Random random;
    private Data data;
    private LinkedList STM;
    private Integer tabooPossession;

    public TabooSearch(ArrayList<Integer> origSolution, Integer solutionValue, Long seed, Data data, Integer numIterations, Integer tabooPossession){
        this.solution = origSolution;
        this.solutionValue = solutionValue;
        this.seed = seed;
        this.numIterations = numIterations;
        this.tabooPossession = tabooPossession;
        this.random = new Random(this.seed);
        this.data = data;
    }

    public PairGeneric<ArrayList<Integer>, Integer> run(){
        //INIT
        ArrayList<Integer> currentSolution = new ArrayList<>(solution);
        Integer currentSolutionValue = solutionValue;
        ArrayList<Integer> globalSolution = new ArrayList<>(solution);
        Integer globalSolutionValue = solutionValue;
        boolean carryOn = false;
        int noImprovementCounter = 0;

        // DLB mask to avoid exploring again the same solution
        ArrayList<Integer> DLBmask = new ArrayList<>(globalSolution.size());
        HashSet<Integer> DLBmaskValidPositions = new HashSet<>(globalSolution.size());
        for (int i = 0; i < globalSolution.size(); i++){
            DLBmask.add(0);
            DLBmaskValidPositions.add(i);
        }

        // We initialize the STM and LTM memories
        STM = new LinkedList();

        for (int counterIte = 0; counterIte < numIterations && !DLBmaskValidPositions.isEmpty(); counterIte++){

            // EXPLORE THE NEIGHBOURHOOD (Filtered by DLB)
            int best_i = -1;
            int best_j = -1;
            int bestNeighborValue = Integer.MAX_VALUE;

            // These variables save the "best of the worst" in case the DLB runs out
            int bestOfWorst_i = -1;
            int bestOfWorst_j = -1;
            int bestOfWorstValue = Integer.MAX_VALUE;

            // We use a copy of the valid positions so we can iterate over them safely.
            ArrayList<Integer> positionsToExplore = new ArrayList<>(DLBmaskValidPositions);


            for (int i : positionsToExplore) {
                for (int j = i + 1; j < currentSolution.size(); j++){
                    int delta = calculateDeltaForSwap(i, j, currentSolution);
                    int neighborValue = currentSolutionValue + delta;

                    // We always keep track of the "best of the worst"
                    if (neighborValue < bestOfWorstValue) {
                        bestOfWorstValue = neighborValue;
                        bestOfWorst_i = i;
                        bestOfWorst_j = j;
                    }

                    boolean isTabu = STM.find(i, j);

                    // Aspiration Criterion: if it improves the global solution, we ignore if it's tabu
                    if (neighborValue < globalSolutionValue){
                        if (neighborValue < bestNeighborValue) {
                            bestNeighborValue = neighborValue;
                            best_i = i;
                            best_j = j;
                        }
                    }
                    // If it's not tabu, we consider it as a candidate for the best neighbor
                    else if (!isTabu) {
                        if (neighborValue < bestNeighborValue) {
                            bestNeighborValue = neighborValue;
                            best_i = i;
                            best_j = j;
                        }
                    }
                }
            }

            // The decision for the DLB is made here, after exploring everything for all valid 'i'.

            // DO THE MOVEMENT
            if (best_i != -1) {
                applyMove(best_i, best_j, currentSolution);
                currentSolutionValue = bestNeighborValue;
                // Update memory
                decrementSTM();
                STM.append(best_i, best_j, tabooPossession);


                // We reactivate the bits of the units moved in the DLB
                if (DLBmask.get(best_i) == 1) DLBmaskValidPositions.add(best_i);
                if (DLBmask.get(best_j) == 1) DLBmaskValidPositions.add(best_j);
                DLBmask.set(best_i, 0);
                DLBmask.set(best_j, 0);

                if (currentSolutionValue < globalSolutionValue) {
                    globalSolution = new ArrayList<>(currentSolution);
                    globalSolutionValue = currentSolutionValue;
                    solutionValue = globalSolutionValue;
                    noImprovementCounter = 0;
                } else {
                    noImprovementCounter++;
                }
            }
            // If we don't find any valid moves (all were taboo or there were no bits in DLB)
            // we activate the diversification mechanism.
            else {

                // DLB exhausted or all moves were tabu without aspiration
                System.out.println("DLB exhausted or no valid moves. Diversifying to the 'best of the worst'.");
                if (bestOfWorst_i != -1) {
                    applyMove(bestOfWorst_i, bestOfWorst_j, currentSolution);
                    currentSolutionValue = bestOfWorstValue;

                    decrementSTM();
                    STM.append(bestOfWorst_i, bestOfWorst_j, tabooPossession);

                    if (currentSolutionValue < globalSolutionValue) {
                        globalSolution = new ArrayList<>(currentSolution);
                        globalSolutionValue = currentSolutionValue;
                        solutionValue = globalSolutionValue;
                        noImprovementCounter = 0;
                    } else {
                        noImprovementCounter++;
                    }

                    // RESET DLB
                    for(int k=0; k < DLBmask.size(); k++) {
                        DLBmask.set(k, 0);
                        DLBmaskValidPositions.add(k);
                    }
                }
            }
        }
        return new PairGeneric<ArrayList<Integer>,Integer>(globalSolution,globalSolutionValue);
    }

    public ArrayList<Integer> getSolution(){
        return solution;
    }

    public int getSolutionValue(){
        return solutionValue;
    }

    private int calculateDeltaForSwap(int i, int j, ArrayList<Integer> currentSolution) {
        int delta = 0;
        int locI = currentSolution.get(i);
        int locJ = currentSolution.get(j);

        for (int k = 0; k < currentSolution.size(); k++) {
            if (k != i && k != j) {
                int locK = currentSolution.get(k);
                // We use the simplified symmetric formula
                delta += 2 * (data.flujos[i][k] * (data.distancias[locJ][locK] - data.distancias[locI][locK]));
                delta += 2 * (data.flujos[j][k] * (data.distancias[locI][locK] - data.distancias[locJ][locK]));
            }
        }
        return delta;
    }

    private void applyMove(int i,int j, ArrayList<Integer> actualSoluttion){
        int aux = actualSoluttion.get(i);
        actualSoluttion.set(i,actualSoluttion.get(j));
        actualSoluttion.set(j,aux);
    }

    private void decrementSTM() {
        LinkedList.Node head = STM.getHead();
        if (head == null) {
            return; // Nothing to do if the list is empty
        }

        // Traverse the list, decrement, and collect nodes to be deleted ---

        // We use an auxiliary list to store the nodes to be deleted
        ArrayList<LinkedList.Node> nodesToDelete = new ArrayList<>();
        LinkedList.Node current = head;

        // We use a do-while loop, the standard method to traverse a circular list once
        do {
            int tabooValue = current.getData3();
            current.setData3(tabooValue - 1); // We decrement the value

            // If the value reaches 0, we add the node to our deletion list
            if (tabooValue - 1 == 0) {
                nodesToDelete.add(current);
            }

            current = current.getNext();
        } while (current != head); // The loop stops when we've completed a full circle


        // Delete all the collected nodes from the STM list ---
        for (LinkedList.Node node : nodesToDelete) {
            STM.delete(node.getData1(), node.getData2());
        }
    }

    public void setElite(ArrayList<Integer> elite, Integer eliteValue){
        solution.clear();
        solution = new ArrayList<>(elite);
        solutionValue = eliteValue;
    }
}