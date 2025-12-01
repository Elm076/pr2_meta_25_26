package algorithms;

import config.Data;
import dataStructures.PairGeneric;

import java.util.*;
import java.time.Duration;
import java.time.Instant;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class generational {
    GreedyRandom greedy;
    // private Double[][] distancesMatrix; // Ya no se usa
    private Data data; // Usamos el objeto Data
    private Long seed;
    private Integer populationSize;
    private Float percentRandomInit;
    private Integer greedyListSize;
    private Integer nElite;
    private ArrayList<PairGeneric<ArrayList<Integer>, Double>> elites;
    private Integer kBest;
    private Integer kWorst;
    private Float probCross;
    private Float probMutation;
    private Integer maxIterations;
    private Integer maxSeconds;
    private ArrayList<Integer> initCase;
    private Random random;
    private ArrayList<PairGeneric<ArrayList<Integer>, Double>> prevPopulation;
    private ArrayList<PairGeneric<ArrayList<Integer>, Double>> actualPopulation;
    private Boolean useOX2;
    private Integer numOfEvaluations;
    private Integer extraParam;

    public generational(Data data_, Long seed_,Integer popSize_, Float percentRandomInit_, Integer greedSize_, Integer elite_,
                        Integer kBest_, Integer kWorst_, Float probCross_, Float probMutation_, Integer maxIterations_, Integer maxSeconds_, Boolean OX2_,
                        Integer extraParam){
        data = data_;
        seed = seed_;
        populationSize = popSize_;
        percentRandomInit = percentRandomInit_;
        greedyListSize = greedSize_;
        nElite = elite_;
        kBest = kBest_;
        kWorst = kWorst_;
        probCross = probCross_;
        probMutation = probMutation_;
        maxIterations = maxIterations_;
        maxSeconds = maxSeconds_;
        useOX2 = OX2_;
        this.extraParam = extraParam;
        random = new Random(seed);
        numOfEvaluations = 0;

        greedy = new GreedyRandom(seed, greedyListSize, data, extraParam);

        prevPopulation = new ArrayList<>();
        actualPopulation = new ArrayList<>();
        elites = new ArrayList<>();
    }

    private void generatePopultaion(){
        ArrayList<Integer> population = new ArrayList<>(); // Ahora representa localizaciones
        //Initialize to "empty" values the elites
        PairGeneric<ArrayList<Integer>, Double> entry = new PairGeneric<>(population,0.0);
        for (int i = 0; i < nElite; i++){
            ArrayList<Integer> emptySolution = new ArrayList<>();
            PairGeneric<ArrayList<Integer>, Double> e = new PairGeneric<>(emptySolution, Double.POSITIVE_INFINITY);
            elites.add(e);
        }


        for (int i = 0; i < data.n; i++){
            population.add(i);
        }

        for (int i = 0; i < (populationSize*percentRandomInit); i++){
            ArrayList<Integer> newIndividual = new ArrayList<>(population);
            Collections.shuffle(newIndividual, random);
            PairGeneric<ArrayList<Integer>, Double> newEntry = new PairGeneric<>(newIndividual, Double.POSITIVE_INFINITY);
            prevPopulation.add(newEntry);
        }

        for (int i = 0; i < (populationSize * (1-percentRandomInit)); i++){
            ArrayList<Integer> newIndividualGreedy = greedy.execute();
            PairGeneric<ArrayList<Integer>, Double> newEntryGreedy = new PairGeneric<>(newIndividualGreedy, Double.POSITIVE_INFINITY);
            prevPopulation.add(newEntryGreedy);
        }

    }

    private void evaluatePopulation(){

        for (int i = 0; i < populationSize; i++){

            Double individualValue = Utilitys.EvaluationFunction(prevPopulation.get(i).getFirst(), data);
            prevPopulation.get(i).setSecond(individualValue);
            for (int j = 0; j < nElite; j++){
                // If we find a new Elite, we add it and erase the least-Elite from elites
                if (individualValue < elites.get(j).getSecond()){
                    elites.add(j,new PairGeneric<>(prevPopulation.get(i).getFirst(),individualValue));
                    elites.removeLast();
                    break; //Whenever we find its better than the k-elite, we dont need to evaluate if its better than the rest of the elites
                }
            }
        }
    }

    int kBestTournament(ArrayList<PairGeneric<Integer, Double>> candidates){
        double bestCost = Double.POSITIVE_INFINITY;
        int bestIndex = 0;
        for (int i = 0; i < candidates.size(); i++){
            double cost = candidates.get(i).getSecond();
            if (cost < bestCost){
                bestCost = cost;
                bestIndex = candidates.get(i).getFirst();
            }
        }
        return bestIndex;
    }

    int kWorstTournament(ArrayList<PairGeneric<Integer, Double>> candidates){
        double worstCost = Double.NEGATIVE_INFINITY;
        int worstIndex = 0;
        for (int i = 0; i < candidates.size(); i++){
            double cost = candidates.get(i).getSecond();
            if (cost > worstCost){
                worstCost = cost;
                worstIndex = candidates.get(i).getFirst();
            }
        }
        return worstIndex;
    }


    public static PairGeneric<ArrayList<Integer>, ArrayList<Integer>> mocCrossover(
            ArrayList<Integer> p1, ArrayList<Integer> p2, Random random) {

        int n = p1.size();
        // Init children with all -1
        ArrayList<Integer> c1 = new ArrayList<>(Collections.nCopies(n, -1));
        ArrayList<Integer> c2 = new ArrayList<>(Collections.nCopies(n, -1));

        // set the cross point between 1 and size-1
        int cp = random.nextInt(n - 1) + 1;

        // Copy the father's right-side into c1 and mother's into c2
        for (int i = cp; i < n; i++) {
            c1.set(i, p1.get(i));
            c2.set(i, p2.get(i));
        }

        // Fill the "left part" [0...cp-1] of c1 with the elements of p2
        // in order, skipping the ones that already exist in the copied part of c1
        int insertPos = 0;
        for (int i = 0; i < n; i++) {
            Integer content = p2.get(i);
            // Si c1 NO contiene el elemento (en la parte derecha) y no hemos llenado la izquierda
            if (!c1.contains(content)) {
                c1.set(insertPos, content);
                insertPos++;
                if (insertPos == cp) {
                    break; // Hemos completado hasta cp-1
                }
            }
        }

        // Fill the "left part" [0...cp-1] of c2 with the elements of p1
        // in order, excluding those already copied to c2
        insertPos = 0;
        for (int i = 0; i < n; i++) {
            Integer content = p1.get(i);
            if (!c2.contains(content)) {
                c2.set(insertPos, content);
                insertPos++;
                if (insertPos == cp) {
                    break;
                }
            }
        }

        return new PairGeneric<>(c1, c2);
    }

    public static PairGeneric<ArrayList<Integer>, ArrayList<Integer>> ox2Crossover(
            ArrayList<Integer> p1, ArrayList<Integer> p2, Random random) {

        int n = p1.size();

        // --- STEP 1: Select a random set of positions ---
        // Generate a list of all possible indices (0, 1, ..., n-1)
        List<Integer> allIndices = IntStream.range(0, n).boxed().collect(Collectors.toList());
        Collections.shuffle(allIndices, random);

        // Choose a random number of positions to select (e.g., between 2 and half the size)
        int numPositions = random.nextInt(n / 2 - 2) + 2;
        List<Integer> selectedPositions = allIndices.subList(0, numPositions);

        // Generate the two children symmetrically
        ArrayList<Integer> child1 = generateOX2Child(p1, p2, selectedPositions);
        ArrayList<Integer> child2 = generateOX2Child(p2, p1, selectedPositions);

        return new PairGeneric<>(child1, child2);
    }


    private static ArrayList<Integer> generateOX2Child(
            ArrayList<Integer> primaryParent,
            ArrayList<Integer> secondaryParent,
            List<Integer> positions) {

        int n = primaryParent.size();
        ArrayList<Integer> child;

        // --- STEP 2: Extract the ordered elements from the secondary parent ---
        List<Integer> orderedElements = new ArrayList<>();
        for (int pos : positions) {
            orderedElements.add(secondaryParent.get(pos));
        }

        // Use a HashSet for an efficient lookup of which elements to move
        HashSet<Integer> elementSet = new HashSet<>(orderedElements);

        // --- STEP 3: Identify the holes in the primary parent ---
        // Store the indices of the positions that we will empty and refill
        List<Integer> holeIndices = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (elementSet.contains(primaryParent.get(i))) {
                holeIndices.add(i);
            }
        }

        // --- STEP 4: Fill the holes ---
        // Create the child as a copy of the primary parent
        child = new ArrayList<>(primaryParent);
        // Now, fill the holes with the elements in the new order
        for (int i = 0; i < holeIndices.size(); i++) {
            int holeIndex = holeIndices.get(i);
            int elementToInsert = orderedElements.get(i);
            child.set(holeIndex, elementToInsert);
        }

        return child;
    }


    void parentsSelectionAndMutation(){
        ArrayList<PairGeneric<ArrayList<Integer>, Double>> matingPool = new ArrayList<>();
        PairGeneric<ArrayList<Integer>,ArrayList<Integer>> childs = new PairGeneric<>();

        // We generate the matingPool
        for (int i = 0; i < populationSize; i++){
            //remember im referring the candidates by his index in the ArrayList Population and not copying the individuals into a new object
            ArrayList<PairGeneric<Integer, Double>> candidates = new ArrayList<>();

            for (int j = 0; j < kBest; j++){
                int idx = random.nextInt(0, populationSize);
                double cost = prevPopulation.get(idx).getSecond();
                PairGeneric<Integer, Double> candidate =
                        new PairGeneric<>(idx, cost);
                candidates.add(candidate);
            }
            int winner = kBestTournament(candidates);
            matingPool.add(prevPopulation.get(winner));
        }

        //We generate the children (actualPopulation)
        for (int i = 0; i < populationSize/2; i++){
            Boolean crossed = false;
            if (random.nextFloat() <= 0.7) {
                if (useOX2) {
                    childs = ox2Crossover(matingPool.get(i).getFirst(), matingPool.get(i+1).getFirst(), random);
                } else
                    childs = mocCrossover(matingPool.get(i).getFirst(), matingPool.get(i+1).getFirst(), random);
                crossed = true;
            }
            else{
                childs.setFirst(matingPool.get(i).getFirst());
                childs.setSecond(matingPool.get(i+1).getFirst());
            }

            ArrayList<Integer> mutation = new ArrayList<>();
            Boolean mutationChild1 = false;
            Boolean mutationChild2 = false;
            //Apply mutation 2opt if probabilities success
            if (random.nextFloat() <= 0.1) {
                mutation = Utilitys.TwoOpt(childs.getFirst(), random.nextInt(0, data.n), random.nextInt(0, data.n));
                actualPopulation.add(new PairGeneric<>(mutation, 0.0));
                mutationChild1 = true;
            }
            else{
                actualPopulation.add(new PairGeneric<>(childs.getFirst(), 0.0));
            }

            if (random.nextFloat() <= 0.1) {
                mutation = Utilitys.TwoOpt(childs.getSecond(), random.nextInt(0, data.n), random.nextInt(0, data.n));
                actualPopulation.add(new PairGeneric<>(mutation, 0.0));
                mutationChild2 = true;
            }
            else{
                actualPopulation.add(new PairGeneric<>(childs.getSecond(), 0.0));
            }

            /*We update the number of evaluations made
            If we crossed, we will have to evaluate both children
            If we didnt cross, we will have to evaluate only the mutated children (if mutation occurred)
             */
            if (crossed)
                numOfEvaluations += 2;
            else{
                if (mutationChild1)
                    numOfEvaluations++;
                if (mutationChild2)
                    numOfEvaluations++;
            }
        }

        prevPopulation = actualPopulation;
        actualPopulation = new ArrayList<>();
        evaluatePopulation();

        // We need to ensure that the elites survives into the new generation
        for (int i = 0; i < nElite; i++){
            if (!prevPopulation.contains(elites.get(i))){
                //remember im referring the candidates by his index in the ArrayList Population and not by the whole Path
                ArrayList<PairGeneric<Integer, Double>> candidates = new ArrayList<>();

                for (int j = 0; j < kWorst; j++){
                    int idx = random.nextInt(0, populationSize);
                    double cost = prevPopulation.get(idx).getSecond();
                    PairGeneric<Integer, Double> candidate =
                            new PairGeneric<>(idx, cost);
                    candidates.add(candidate);
                }
                int worst = kWorstTournament(candidates);

                prevPopulation.remove(worst);
                prevPopulation.add(elites.get(i));
            }
        }
    }


    public PairGeneric<ArrayList<Integer>, Double> run(){
        Instant initTime = Instant.now();
        Instant checkTime = Instant.now();
        Duration time = Duration.between(initTime,checkTime);

        generatePopultaion();
        evaluatePopulation();
        numOfEvaluations += populationSize;
        while (numOfEvaluations < maxIterations - 2 && time.toSeconds() < maxSeconds){
            //System.out.println("Iteration = " +ite);
            parentsSelectionAndMutation();

            checkTime = Instant.now();
            time = Duration.between(initTime,checkTime);
        }
        evaluatePopulation();

        System.out.println("Value of solution = " + elites.getFirst().getSecond());
        System.out.println();

        return new PairGeneric<>(elites.getFirst().getFirst(), elites.getFirst().getSecond());

    }

    public void setnElite(Integer nElite) {
        this.nElite = nElite;
    }

    public void setkBest(Integer kBest) {
        this.kBest = kBest;
    }
}