import algorithms.Stationary;
import algorithms.generational;
import config.Data;
import config.Params;
import dataStructures.PairGeneric;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Main {

    // --- CAMBIO: Usamos 'static class' en lugar de 'record' para compatibilidad ---
    static class TestConfig {
        private final String algorithm;
        private final String crossover;
        private final int M, E, kBest, kWorst;

        public TestConfig(String algorithm, String crossover, int M, int E, int kBest, int kWorst) {
            this.algorithm = algorithm;
            this.crossover = crossover;
            this.M = M;
            this.E = E;
            this.kBest = kBest;
            this.kWorst = kWorst;
        }

        // Estos métodos imitan a un 'record' para no romper el resto del código
        public String algorithm() { return algorithm; }
        public String crossover() { return crossover; }
        public int M() { return M; }
        public int E() { return E; }
        public int kBest() { return kBest; }
        public int kWorst() { return kWorst; }

        @Override
        public String toString() {
            return String.format("Alg: %s, Cruce: %s, M: %d, E: %d, kBest: %d, kWorst: %d",
                    algorithm, crossover, M, E, kBest, kWorst);
        }
    }
    // ---------------------------------------------------------------------------

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("\n" + "Error: Debes proporcionar la ruta al archivo de configuración.");
            return;
        }

        String configFilePath = args[0];

        try (PrintWriter writer = new PrintWriter(new FileWriter("assets/ExecutionsExit.txt"))) {

            Params configuration = new Params(configFilePath);

            // --- LECTURA DINÁMICA DEL ARCHIVO TXT ---
            List<TestConfig> testSuite = new ArrayList<>();

            try (BufferedReader br = new BufferedReader(new FileReader(configFilePath))) {
                String line;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    // Solo leemos las líneas que empiezan exactamente con "TestConfig:"
                    if (line.startsWith("TestConfig:")) {
                        try {
                            // Cortamos la etiqueta y separamos por comas
                            String content = line.substring("TestConfig:".length());
                            String[] parts = content.split(",");

                            // Limpiamos espacios en blanco alrededor de cada dato
                            if (parts.length >= 6) {
                                String alg = parts[0].trim();
                                String cross = parts[1].trim();
                                int M = Integer.parseInt(parts[2].trim());
                                int E = Integer.parseInt(parts[3].trim());
                                int kBest = Integer.parseInt(parts[4].trim());
                                int kWorst = Integer.parseInt(parts[5].trim());

                                testSuite.add(new TestConfig(alg, cross, M, E, kBest, kWorst));
                            }
                        } catch (Exception e) {
                            System.err.println("Error leyendo línea de test: " + line + " -> " + e.getMessage());
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("No se pudo leer el archivo de configuración para los tests.");
            }

            // Si no encontró nada en el archivo, avisamos
            if (testSuite.isEmpty()) {
                System.out.println("ADVERTENCIA: No se encontraron líneas 'TestConfig:' en " + configFilePath);
                writer.println("ADVERTENCIA: No se encontraron tests. Revisa parametros.txt");
            }
            // ----------------------------------------

            // Carga de archivos de datos
            ArrayList<Data> files = new ArrayList<>();
            for (String filePath : configuration.getFiles()) {
                files.add(new Data(filePath));
            }

            // Ejecución de algoritmos
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

                        Instant startTime = Instant.now();

                        if (test.algorithm().equals("Gen")) {
                            generational genAlgorithm = new generational(
                                    dataFile, actualSeed, configuration.getPopSize(),
                                    configuration.getRandomInitPercent(), configuration.getGreedyListSize(),
                                    test.E(), test.kBest(), test.kWorst(),
                                    configuration.getCrossPercent(), configuration.getMutationPercent(),
                                    configuration.getNumIterations(), configuration.getSecondsExec(), useOX2, configuration.getExtraParam()
                            );
                            result = genAlgorithm.run();

                        } else if (test.algorithm().equals("Est")) {
                            float stationaryCrossProb = 1.0f;
                            Stationary statAlgorithm = new Stationary(
                                    dataFile, actualSeed, configuration.getPopSize(),
                                    configuration.getRandomInitPercent(), configuration.getGreedyListSize(),
                                    test.E(), test.kBest(), test.kWorst(),
                                    stationaryCrossProb, configuration.getMutationPercent(),
                                    configuration.getNumIterations(), configuration.getSecondsExec(), useOX2, configuration.getExtraParam()
                            );
                            result = statAlgorithm.run();
                        }

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