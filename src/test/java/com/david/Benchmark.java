package com.david;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import org.junit.Test;

public class Benchmark {
    private static void testSpeed(String path, String pattern, PrintWriter csvLog, int repeat, boolean verbose){
        long totalConstructTime = 0, totalGreedyTime = 0, totalKGramTime = 0;

        for (int r = 0; r < repeat; r++){
            long startTime0 = System.nanoTime();
            KGramWildcard kgramIndex = new KGramWildcard(path, 2);
            totalConstructTime += (System.nanoTime() - startTime0) / 1000;
    
            long startTime1 = System.nanoTime();
            Set<String> greedyResult = new HashSet<>(Greedy.findMatches(kgramIndex.getWordsList(), pattern));
            totalGreedyTime += (System.nanoTime() - startTime1) / 1000;
    
            long startTime2 = System.nanoTime();
            Set<String> kgramResult = new HashSet<>(kgramIndex.findMatches(pattern));
            totalKGramTime += (System.nanoTime() - startTime2) / 1000;

            // verify: compare kgram query with bruteforce 
            if (!greedyResult.equals(kgramResult)){
                throw new RuntimeException("bug: kgram result is wrong");
            }
        }

        long constructTime = totalConstructTime/repeat;
        long kgramTime = totalKGramTime/repeat;
        long greedyTime = totalGreedyTime/repeat;

        if (verbose){
            System.out.println("Construction Time: " + constructTime + " us");
            System.out.println("K-Gram Time: " +  kgramTime + " us");
            System.out.println("Greedy Time: " + greedyTime + " us");
        }
        csvLog.format("\"%1$s\",%2$d,%3$d,%4$d\n", pattern, constructTime, kgramTime, greedyTime);
        csvLog.flush();
    }

    @Test
    public void testKValues() {
        String path = "./src/dataset/400k_corpus.txt";
        String pattern = "guarantee*";

        for (int k = pattern.length(); k > 0; k--) {
            long startTime0 = System.nanoTime();
            KGramWildcard kgramIndex = new KGramWildcard(path, k);
            long constructionTime = (System.nanoTime() - startTime0) / 1000;

            long startTime1 = System.nanoTime();
            kgramIndex.findMatches(pattern);
            long kgramTime = (System.nanoTime() - startTime1) / 1000;

            System.out.println("=== K: " + k + " ===");
            System.out.println("Construction Time: " + constructionTime + " us");
            System.out.println("K-Gram Time: " + kgramTime + " us " + '\n' );
            
        }

        long startTime2 = System.nanoTime();
        Greedy.findMatches(KGramWildcard.loadWords(path), pattern);
        long greedyTime = (System.nanoTime() - startTime2) / 1000;

        System.out.println("Greedy Time: " + greedyTime + " us");
    }


    public static void test(PrintWriter csvLog, int repeat){
        csvLog.format("\"Pattern\",\"Construction Time\",\"K-Gram Time (us)\",\"Greedy Time (us)\"\n");
        String path = "./src/dataset/400k_corpus.txt";
        testSpeed(path, "guarantee*", csvLog, repeat, true);
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
