package com.david;

import static org.junit.Assert.*;

import org.junit.*;
import java.util.*;

/**
 * Unit test for simple App.
 */


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KGramTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(KGramTest.class);

    @Test
    public void testCatDog() {
        String path = "./src/dataset/doc3.txt";
        String pattern = "*cat*dog*";
        KGramWildcard kgramIndex = new KGramWildcard(path, 2);
        Set<String> expected = new HashSet<>(Greedy.findMatches(kgramIndex.getWordsList(), pattern));
        Set<String> result = new HashSet<>(kgramIndex.findMatches(pattern));
        assertEquals(expected, result);
    }

    @Test
    public void testSingleChar() {
        String path = "./src/dataset/doc1.txt";
        String pattern = "*i*";
        KGramWildcard kgramIndex = new KGramWildcard(path, 1);
        Set<String> expected = new HashSet<>(Greedy.findMatches(kgramIndex.getWordsList(), pattern));
        Set<String> result = new HashSet<>(kgramIndex.findMatches(pattern));
        assertEquals(expected, result);
    }

    @Test
    public void testFalsePositive() {
        String path = "./src/dataset/doc2.txt";
        String pattern = "gol*";
        KGramWildcard kgramIndex = new KGramWildcard(path, 2);
        Set<String> expected = new HashSet<>(Greedy.findMatches(kgramIndex.getWordsList(), pattern));
        Set<String> result = new HashSet<>(kgramIndex.findMatches(pattern));
        assertEquals(expected, result);
    }

    @Test
    public void testCorpus() {
        String path = "./src/dataset/400k_corpus.txt";
        String pattern = "*he*";
        KGramWildcard kgramIndex = new KGramWildcard(path, 2);
        Set<String> expected = new HashSet<>(Greedy.findMatches(kgramIndex.getWordsList(), pattern));
        Set<String> result = new HashSet<>(kgramIndex.findMatches(pattern));
        assertEquals(expected, result);
    }

    @Test
    public void testSpeed() {
        String path = "./src/dataset/400k_corpus.txt";
        String pattern = "g*";

        long startTime0 = System.nanoTime();
        KGramWildcard kgramIndex = new KGramWildcard(path, 2);
        long constructionTime = (System.nanoTime() - startTime0) / 1000000;

        long startTime1 = System.nanoTime();
        Greedy.findMatches(kgramIndex.getWordsList(), pattern);
        long greedyTime = (System.nanoTime() - startTime1) / 1000000;

        long startTime2 = System.nanoTime();
        kgramIndex.findMatches(pattern);
        long kgramTime = (System.nanoTime() - startTime2) / 1000000;
        
        LOGGER.info("Construction Time: " + constructionTime + " ms");
        LOGGER.info("Greedy Time: " + greedyTime + " ms");
        LOGGER.info("K-Gram Time: " + kgramTime + " ms");
    }

    @Test
    public void testKValues() {
        String path = "./src/dataset/400k_corpus.txt";
        String pattern = "guarantee*";

        for (int k = pattern.length(); k > 0; k--) {
            long startTime0 = System.nanoTime();
            KGramWildcard kgramIndex = new KGramWildcard(path, k);
            long constructionTime = (System.nanoTime() - startTime0) / 1000000;

            long startTime1 = System.nanoTime();
            kgramIndex.findMatches(pattern);
            long kgramTime = (System.nanoTime() - startTime1) / 1000000;

            LOGGER.info("=== K = " + k + " ===");
            LOGGER.info("Construction Time: " + constructionTime + " ms");
            LOGGER.info("K-Gram Time: " + kgramTime + " ms " + '\n' );
        }

        long startTime2 = System.nanoTime();
        Greedy.findMatches(KGramWildcard.loadWords(path), pattern);
        long greedyTime = (System.nanoTime() - startTime2) / 1000000;

        LOGGER.info("Greedy Time: " + greedyTime + " ms");
    }

}
