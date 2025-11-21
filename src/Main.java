import algorithms.Stationary;
import algorithms.generational;
import config.Data;
import config.Params;
import dataStructures.PairGeneric;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Main {

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

        public String toFileNamePart() {
            return String.format("%s_%s_M%d_E%d_kB%d_kW%d",
                    algorithm, crossover, M, E, kBest, kWorst);
        }
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("\nError: Debes proporcionar la ruta al archivo de configuración.");
            return;
        }

        String configFilePath = args[0];
        Params configuration = new Params(configFilePath);

        // --- 1. Crear directorio logs ---
        File logDir = new File("logs");
        if (!logDir.exists()) {
            logDir.mkdirs();
        }

        // --- 2. Lectura dinámica de la Suite ---
        List<TestConfig> testSuite = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(configFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("TestConfig:")) {
                    try {
                        String[] parts = line.substring("TestConfig:".length()).split(",");
                        if (parts.length >= 6) {
                            testSuite.add(new TestConfig(
                                    parts[0].trim(), parts[1].trim(),
                                    Integer.parseInt(parts[2].trim()),
                                    Integer.parseInt(parts[3].trim()),
                                    Integer.parseInt(parts[4].trim()),
                                    Integer.parseInt(parts[5].trim())
                            ));
                        }
                    } catch (Exception e) {
                        System.err.println("Error parseando config: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error leyendo el archivo de configuración.");
        }

        if (testSuite.isEmpty()) {
            System.out.println("ADVERTENCIA: No se encontraron configuraciones 'TestConfig:'.");
            // No hacemos return aquí por si quieres ejecutar algo por defecto,
            // pero idealmente deberías revisar tu txt.
        }

        // --- 4. Carga de archivos de datos (CORREGIDO) ---
        ArrayList<Data> files = new ArrayList<>();
        System.out.println("--- Cargando archivos de datos ---");

        for (String filePath : configuration.getFiles()) {
            File f = new File(filePath);

            // Verificación previa para depuración
            if (!f.exists()) {
                System.err.println("ERROR CRÍTICO: No se encuentra el archivo: " + filePath);
                System.err.println(" -> Buscando en ruta absoluta: " + f.getAbsolutePath());
                continue; // Saltamos este archivo si no existe
            }

            try {
                // Aquí es donde saltaba la excepción Unhandled. Ahora está dentro de try-catch.
                files.add(new Data(filePath));
                System.out.println("Cargado correctamente: " + filePath);
            } catch (Exception e) { // Capturamos IOException o FileNotFoundException
                System.err.println("Excepción al leer datos de " + filePath + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        if (files.isEmpty()) {
            System.err.println("No se pudieron cargar archivos de datos. Terminando programa.");
            return;
        }

        // --- 5. Ejecución ---
        for (Data dataFile : files) {
            String simpleFileName = new File(dataFile.getFilename()).getName();

            for (Long actualSeed : configuration.getSeeds()) {
                for (TestConfig test : testSuite) {

                    String logFileName = String.format("logs/Log_%s_Seed%d_%s.txt",
                            simpleFileName, actualSeed, test.toFileNamePart());

                    try (PrintWriter writer = new PrintWriter(new FileWriter(logFileName))) {
                        writer.println("============= EXECUTION REPORT =============");
                        writer.println("File: " + dataFile.getFilename());
                        writer.println("Seed: " + actualSeed);
                        writer.println("Configuration: " + test.toString());
                        writer.println("============================================");

                        System.out.println("Ejecutando log: " + logFileName);

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
                            writer.println("\nSTATUS: SUCCESS");
                            writer.println("COST: " + result.getSecond());
                            writer.println("TIME: " + formattedTime + "s");
                            writer.println("SOLUTION: " + result.getFirst());
                        } else {
                            writer.println("\nSTATUS: FAILED");
                        }

                    } catch (IOException e) {
                        System.err.println("Error escribiendo log: " + e.getMessage());
                    }
                }
            }
        }
        System.out.println("\nFIN DE TODAS LAS EJECUCIONES.");
    }
}