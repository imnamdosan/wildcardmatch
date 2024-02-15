package com.david;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.junit.Test;

public class Benchmark {
    private static void testSpeed(String path, String pattern, PrintWriter csvLog, int repeat, boolean verbose){
        long startTime0 = System.nanoTime();
        KGramWildcard kgramIndex = new KGramWildcard(path, 2);
        long constructionTime = (System.nanoTime() - startTime0) / 1000;

        long startTime1 = System.nanoTime();
        Greedy.findMatches(kgramIndex.getWordsList(), pattern);
        long greedyTime = (System.nanoTime() - startTime1) / 1000;

        long startTime2 = System.nanoTime();
        kgramIndex.findMatches(pattern);
        long kgramTime = (System.nanoTime() - startTime2) / 1000;
    
        if (verbose){
            System.out.println("Construction Time: " + constructionTime + " us");
            System.out.println("Greedy Time: " + greedyTime + " us");
            System.out.println("K-Gram Time: " + kgramTime + " us");
        }
        csvLog.format();
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

            System.out.println("=== K = " + k + " ===");
            System.out.println("Construction Time: " + constructionTime + " us");
            System.out.println("K-Gram Time: " + kgramTime + " us " + '\n' );
            
        }

        long startTime2 = System.nanoTime();
        Greedy.findMatches(KGramWildcard.loadWords(path), pattern);
        long greedyTime = (System.nanoTime() - startTime2) / 1000;

        System.out.println("Greedy Time: " + greedyTime + " us");
    }


    public static void test(PrintWriter csvLog, int repeat){
        csvLog.format("\"Algorithm\",\"Sparsity\",\"Bits per int\",\"Compress speed (MiS)\",\"Decompress speed (MiS)\"\n");
        String path = "./src/dataset/400k_corpus.txt";
        testSpeed(path, "g*", csvLog, repeat, true);
    }   

    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("# Wildcard Pattern Search Benchmark <3 ");
        System.out.println("# By the only and only David Ji");

        PrintWriter writer = null;

        try {
                File csvFile = new File(
                        String.format(
                                "benchmark-%1$tY%1$tm%1$tdT%1$tH%1$tM%1$tS.csv",
                                System.currentTimeMillis()));
                writer = new PrintWriter(csvFile);
                System.out.println("# Results will be written into a CSV file: " + csvFile.getName());
                System.out.println();
                test(writer, 5);
                System.out.println();
                System.out.println("Results were written into a CSV file: " + csvFile.getName());
        } finally {
                if (writer != null) {
                        writer.close();
                }
        }



    }
}
