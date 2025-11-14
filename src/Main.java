import algorithms.Stationary;
import algorithms.generational;
import config.Data;
import config.Params;
import dataStructures.PairGeneric;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Main {

    // Internal class to save the configuration of each test
    record TestConfig(String algorithm, String crossover, int M, int E, int kBest, int kWorst) {
        @Override
        public String toString() {
            return String.format("Alg: %s, Cruce: %s, M: %d, E: %d, kBest: %d, kWorst: %d",
                    algorithm, crossover, M, E, kBest, kWorst);
        }
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("\n" + "Error: You must provide the path to the configuration file.");
            return;
        }

        // --- To write in the output file ---
        try (PrintWriter writer = new PrintWriter(new FileWriter("assets/ExecutionsExit.txt"))) {

            Params configuration = new Params(args[0]);

            // --- We define here all the configs posible ---
            List<TestConfig> testSuite = new ArrayList<>();
            int M = 100; // Parámetro constante para M

            // Generationals
            testSuite.add(new TestConfig("Gen", "OX2", M, 1, 2, 3));
            testSuite.add(new TestConfig("Gen", "OX2", M, 1, 3, 3));
            testSuite.add(new TestConfig("Gen", "OX2", M, 2, 2, 3));
            testSuite.add(new TestConfig("Gen", "OX2", M, 2, 3, 3));
            testSuite.add(new TestConfig("Gen", "MOC", M, 1, 2, 3));
            testSuite.add(new TestConfig("Gen", "MOC", M, 1, 3, 3));
            testSuite.add(new TestConfig("Gen", "MOC", M, 2, 2, 3));
            testSuite.add(new TestConfig("Gen", "MOC", M, 2, 3, 3));

            // Estationaries
            testSuite.add(new TestConfig("Est", "OX2", M, 1, 2, 2));
            testSuite.add(new TestConfig("Est", "MOC", M, 1, 2, 2));


            // Load the data files
            ArrayList<Data> files = new ArrayList<>();
            for (String filePath : configuration.getFiles()) {
                files.add(new Data(filePath));
            }

            // We execute the algorithms for each file and seed
            for (Data dataFile : files) {
                writer.println("==========================================================");
                writer.println("PROCESSING FILE: " + dataFile.getFilename());
                writer.println("==========================================================");

                for (Long actualSeed : configuration.getSeeds()) {
                    writer.println("\n----- SEED: " + actualSeed + " -----");

                    for (TestConfig test : testSuite) {
                        writer.println("\n---> EXECUTING: " + test.toString());

                        PairGeneric<ArrayList<Integer>, Double> result = null;

                        boolean useOX2 = test.crossover().equals("OX2");

                        // INIT TIME
                        Instant startTime = Instant.now();

                        if (test.algorithm().equals("Gen")) {
                            generational genAlgorithm = new generational(
                                    dataFile, actualSeed, configuration.getPopSize(),
                                    configuration.getRandomInitPercent(), configuration.getGreedyListSize(),
                                    test.E(), test.kBest(), test.kWorst(),
                                    configuration.getCrossPercent(), configuration.getMutationPercent(),
                                    configuration.getNumIterations(), configuration.getSecondsExec(), useOX2
                            );
                            result = genAlgorithm.run();

                        } else if (test.algorithm().equals("Est")) {
                            // For the stationary, the crossover probability is always 100%
                            float stationaryCrossProb = 1.0f;

                            Stationary statAlgorithm = new Stationary(
                                    dataFile, actualSeed, configuration.getPopSize(),
                                    configuration.getRandomInitPercent(), configuration.getGreedyListSize(),
                                    test.E(), test.kBest(), test.kWorst(),
                                    stationaryCrossProb, configuration.getMutationPercent(),
                                    configuration.getNumIterations(), configuration.getSecondsExec(), useOX2
                            );
                            result = statAlgorithm.run();
                        }
                        // Calculating the ending time
                        Instant endTime = Instant.now();
                        Duration executionTime = Duration.between(startTime, endTime);
                        double secondsElapsed = executionTime.toMillis() / 1000.0;

                        if (result != null) {
                            String formattedTime = String.format("%.3f", secondsElapsed);
                            writer.println("RESULT: " + result.getSecond() + " (TIME: " + formattedTime + "s)");
                        } else {
                            writer.println("\nIt's impossible to execute the algorithm for this configuration.");
                        }
                    }
                }
            }
            writer.println("\nEND OF EXECUTIONS.");

        } catch (IOException e) {
            System.err.println("\nERROR WRITING IN THE FILE " + e.getMessage());
            e.printStackTrace();
        }
    }
}