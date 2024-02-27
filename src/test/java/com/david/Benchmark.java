package com.david;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;


public class Benchmark {

    private static void testSpeed(String path, String pattern, PrintWriter csvLog, int repeat, boolean verbose){
        long totalConstructTime = 0, totalGreedyTime = 0, totalKGramTime = 0;

        for (int r = 1; r <= repeat; r++){
            if (verbose) System.out.println("Repeat: " + r);

            long startTime0 = System.nanoTime();
            KGramWildcard kgramIndex = new KGramWildcard(path, 5);
            long currConstructTime = (System.nanoTime() - startTime0) / 1000;
            totalConstructTime += currConstructTime;
    
            long startTime1 = System.nanoTime();
            Set<String> greedyResult = new HashSet<>(Greedy.findMatches(kgramIndex.getWordsList(), pattern));
            long currGreedyTime = (System.nanoTime() - startTime1) / 1000;
            totalGreedyTime += currGreedyTime;
    
            long startTime2 = System.nanoTime();
            Set<String> kgramResult = new HashSet<>(kgramIndex.findMatches(pattern));
            long currKGramTime = (System.nanoTime() - startTime2) / 1000;
            totalKGramTime += currKGramTime;

            // verify: compare kgram query with bruteforce 
            if (!greedyResult.equals(kgramResult)){
                throw new RuntimeException("bug: kgram result is wrong");
            }
            
            if (verbose){
                System.out.println("Construction Time: " + currConstructTime + " us");
                System.out.println("Greedy Time: " + currGreedyTime + " us");
                System.out.println("KGram Time: " + currKGramTime + " us");
                System.out.println("=======================");
            }
        }

        long constructTime = totalConstructTime/repeat;
        long kgramTime = totalKGramTime/repeat;
        long greedyTime = totalGreedyTime/repeat;

        if (verbose){
            System.out.println("===== FINAL RESULTS ====");
            System.out.println("Construction Time: " + constructTime + " us");
            System.out.println("K-Gram Time: " +  kgramTime + " us");
            System.out.println("Greedy Time: " + greedyTime + " us");
        }
        csvLog.format("\"%1$s\",%2$d,%3$d,%4$d\n", pattern, constructTime, kgramTime, greedyTime);
        csvLog.flush();
    }

    // public static void testKValues(String path, String pattern, int repeats, boolean verbose) {
    //     int n = KGramWildcard.getLongestPatternSubstring(pattern);
    //     long[] totalKTimes = new long[n + 1];
    //     long totalGreedyTime = 0, totalConstructTime = 0;

    //     for (int r = 0; r < repeats; r++){
    //         if (verbose) System.out.println("Repeat: " + r);

    //         for (int k = n; k > 0; k--) {
    //             long startTime0 = System.nanoTime();
    //             KGramWildcard kgramIndex = new KGramWildcard(path, k);
    //             totalConstructTime += (System.nanoTime() - startTime0) / 1000;
    
    //             long startTime1 = System.nanoTime();
    //             Set<String> kgramResult = new HashSet<>(kgramIndex.findMatches(pattern));
    //             long kgramTime = (System.nanoTime() - startTime1) / 1000;
    //             totalKTimes[k] += kgramTime;

                
    //             Set<String> greedyResult = new HashSet<>(Greedy.findMatches(kgramIndex.getWordsList(), pattern));
    //             if (!greedyResult.equals(kgramResult)){
    //                 throw new RuntimeException("bug: kgram result is wrong");
    //             }

    //             if (verbose) System.out.println("K: " + k + " Time: " + kgramTime + " us");
    //         }
    
    //         long startTime2 = System.nanoTime();
    //         Greedy.findMatches(KGramWildcard.loadWords(path), pattern);
    //         totalGreedyTime += (System.nanoTime() - startTime2) / 1000;

    //         if (verbose) System.out.println("=======================");
    //     }

    //     int bestKVal = n;
    //     for (int k = n; k > 0; k--){
    //         if (totalKTimes[k] < totalKTimes[bestKVal]){
    //             bestKVal = k;
    //         }

    //         long kgramTime = totalKTimes[k] / repeats;
    //         System.out.println("=== K: " + k + " ===");
    //         System.out.println("K-Gram Time: " + kgramTime + " us " + '\n' );
    //     }

    //     long greedyTime = totalGreedyTime / repeats;
    //     long constructTime = totalConstructTime / repeats; 

    //     System.out.println("===== FINAL RESULTS =====");
    //     System.out.println("Construction Time: " + constructTime + " us");
    //     System.out.println("Best K Val: " + bestKVal + " Time: " + totalKTimes[bestKVal]/repeats + " us");
    //     System.out.println("Greedy Time: " + greedyTime + " us\n");

    // }


    public static void test(PrintWriter csvLog, int repeat){
        csvLog.format("\"Pattern\",\"Construction Time\",\"K-Gram Time (us)\",\"Greedy Time (us)\"\n");
        String path = "./src/dataset/400k_corpus.txt";
        testSpeed(path, "guarantee*", csvLog, repeat, true);
        // testKValues(path, "guarantee*", repeat, true);
    }   

    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("# Wildcard Pattern Search Benchmark <3 ");
        System.out.println("# By the only and only David Ji");

        PrintWriter writer = null;

        try {
            String fileName = String.format(
                "benchmark-%1$tY%1$tm%1$tdT%1$tH%1$tM%1$tS.csv",
                System.currentTimeMillis());
    
            writer = new PrintWriter(new FileWriter("./benchmarkresults/" + fileName));
            System.out.println("# Results will be written into a CSV file: " + fileName);
            System.out.println();
            test(writer, 5);
            System.out.println();
            System.out.println("Results were written into a CSV file: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                    writer.close();
            }
        }
    }
}
