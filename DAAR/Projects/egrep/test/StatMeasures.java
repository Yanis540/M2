package test;


import KMP.KMP;
import Regex.Automate;

import java.util.ArrayList;
import java.util.List;

public class StatMeasures {
    public static class Result {
        /** time in ms */
        public long avg_time; 
        public boolean found; 
        public Result(long avg_time,boolean found){
            this.found= found; 
            this.avg_time= avg_time; 
        }
    }
     // Mesurer le temps de création et d'exécution du KMP
     public static long[] measureKMPConstructionTime(String pattern) {
        List<Long> times = new ArrayList<>();
        for (int i = 0; i < 100; i++) { // Faire 100 itérations pour la moyenne
            long startTime = System.nanoTime();
            @SuppressWarnings("unused")
            KMP kmp = new KMP(pattern); // Création de l'algorithme KMP
            long endTime = System.nanoTime();
            long timeInMicroSeconds = (endTime - startTime)/1000;
            times.add(timeInMicroSeconds); // Stocker le temps d'exécution
        }
        return times.stream().mapToLong(l -> l).toArray();
    }
    public static long[] measureDFAConstructionTime(String pattern) {
        List<Long> times = new ArrayList<>();
        for (int i = 0; i < 100; i++) { // Faire 100 itérations pour la moyenne
            long startTime = System.nanoTime();
            @SuppressWarnings("unused")
            Automate dfa = new Automate(pattern); // Création de l'automate DFA
            long endTime = System.nanoTime();
            long timeInMicroSeconds = (endTime - startTime)/1000;
            times.add(timeInMicroSeconds); // Stocker le temps d'exécution
        }
        return times.stream().mapToLong(l -> l).toArray();
    }
    public static Result measureKMPTotalSearchTime(String pattern,String line,int rep) {
        List<Long> times = new ArrayList<>();
        boolean foundKMP=false;
        for (int i = 0; i < rep; i++) { // Faire 100 itérations pour la moyenne
            long startTime = System.nanoTime();
            KMP kmp = new KMP(pattern);
            foundKMP= kmp.find(line); // Rechercher le motif avec KMP
            long endTime = System.nanoTime();
            long timeInMicroSeconds = (endTime - startTime)/1000;
            times.add(timeInMicroSeconds); // Stocker le temps d'exécution
        }
        long avg_time = average(times.stream().mapToLong(l -> l).toArray()); 
        return (new Result(avg_time,foundKMP));
    }
    public static Result measureDFATotalSearchTime(String regEx,String line,int rep) {
        List<Long> times = new ArrayList<>();
        boolean foundDFA=false;
        for (int i = 0; i < rep; i++) { // Faire 100 itérations pour la moyenne
            long startTime = System.nanoTime();
            Automate dfa = new Automate(regEx); // Création de l'automate DFA
            foundDFA= dfa.find(line); // Rechercher le motif avec DFA
            long endTime = System.nanoTime();
            long timeInMicroSeconds = (endTime - startTime)/1000;
            times.add(timeInMicroSeconds); // Stocker le temps d'exécution
        }
        long avg_time = average(times.stream().mapToLong(l -> l).toArray()); 
        return (new Result(avg_time,foundDFA));
    }
    // Calculer la moyenne des temps
    public static long average(long[] times) {
        return (long) java.util.Arrays.stream(times).average().orElse(0);
    }

    // Trouver le temps minimum
    public static long min(long[] times) {
        return java.util.Arrays.stream(times).min().orElse(0);
    }

    // Trouver le temps maximum
    public static long max(long[] times) {
        return java.util.Arrays.stream(times).max().orElse(0);
    }
}
