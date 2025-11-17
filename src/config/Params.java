package config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Params {
    private ArrayList<String> files;
    private ArrayList<String> algorithms;
    private ArrayList<Long> seeds;
    private Integer popSize;
    private Float randomInitPercent;
    private ArrayList<Integer> Elites;
    private ArrayList<Integer> kBests;
    private Integer kWorst;
    private Integer greedyListSize;
    private Float crossPercent;
    private Float mutationPercent;
    private Integer numIterations;
    private Integer secondsExec;
    private Integer extraParam;

    public Params(String pathConfigFile){
        this.files = new ArrayList<>();
        this.algorithms = new ArrayList<>();
        this.seeds = new ArrayList<>();
        this.Elites = new ArrayList<>();
        this.kBests = new ArrayList<>();
        String line;
        FileReader f = null;
        try{
            f = new FileReader(pathConfigFile);
            BufferedReader b = new BufferedReader(f);
            while((line = b.readLine()) != null){
                String[] split = line.split(":");
                switch (split[0]){
                    case "Files" :
                        String[] files = split[1].split(" ");
                        for (int i = 0; i < files.length; i++){
                            this.files.add(files[i]);
                        }
                        break;
                    case "Algorithms" :
                        String[] algorithms = split[1].split(" ");
                        for (int i = 0; i < algorithms.length; i++){
                            this.algorithms.add(algorithms[i]);
                        }
                        break;
                    case "Seeds" :
                        String[] seeds = split[1].split(" ");
                        for (int i = 0; i < seeds.length; i++){
                            this.seeds.add(Long.parseLong(seeds[i]));
                        }
                        break;
                    case "PopulationSize" :
                        this.popSize = Integer.parseInt(split[1]);
                        break;
                    case "%RandomInit" :
                        this.randomInitPercent = Float.parseFloat(split[1]);
                        break;
                    case "Elite" :
                        String[] elites = split[1].split(" ");
                        for (int i = 0; i < elites.length; i++){
                            this.Elites.add(Integer.parseInt(elites[i]));
                        }
                        break;
                    case "KBest" :
                        String[] KBests = split[1].split(" ");
                        for (int i = 0; i < KBests.length; i++){
                            this.kBests.add(Integer.parseInt(KBests[i]));
                        }
                        break;
                    case "KWorst" :
                        this.kWorst = Integer.parseInt(split[1]);
                        break;
                    case "GreedyListSize" :
                        this.greedyListSize = Integer.parseInt(split[1]);
                        break;
                    case "%Cross" :
                        this.crossPercent = Float.parseFloat(split[1]);
                        break;
                    case "%Mutation" :
                        this.mutationPercent = Float.parseFloat(split[1]);
                        break;
                    case "NumIterations" :
                        this.numIterations = Integer.parseInt(split[1]);
                        break;
                    case "Seconds" :
                        this.secondsExec = Integer.parseInt(split[1]);
                        break;
                    case "ExtraParam" :
                        this.extraParam = Integer.parseInt(split[1]);
                        break;
                }
            }
        }
        catch (IOException e){
            System.out.println(e);
        }
    }

    public ArrayList<String> getFiles() {
        return files;
    }

    public ArrayList<String> getAlgorithms() {
        return algorithms;
    }

    public ArrayList<Long> getSeeds() {
        return seeds;
    }

    public Integer getPopSize() {
        return popSize;
    }

    public Float getRandomInitPercent() {
        return randomInitPercent;
    }

    public ArrayList<Integer> getElites() {
        return Elites;
    }

    public ArrayList<Integer> getkBests() {
        return kBests;
    }

    public Integer getkWorst() {
        return kWorst;
    }

    public Integer getGreedyListSize() {
        return greedyListSize;
    }

    public Float getCrossPercent() {
        return crossPercent;
    }

    public Float getMutationPercent() {
        return mutationPercent;
    }

    public Integer getSecondsExec() {
        return secondsExec;
    }

    public Integer getNumIterations() {
        return numIterations;
    }

    public Integer getExtraParam() { return  extraParam; }



}
