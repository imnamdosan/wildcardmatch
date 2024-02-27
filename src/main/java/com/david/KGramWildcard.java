package com.david;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KGramWildcard {
    private static final Logger LOGGER = LoggerFactory.getLogger(KGramWildcard.class);

    private int k;
    private List<String> wordsList;
    private Map<String, Set<Integer>> index;

    public KGramWildcard(String path, int k) {
        this.k = k;
        this.wordsList = loadWords(path);
        this.index = new HashMap<>();
        createIndex();
    }

    private List<String> getKGrams(String word, int k) {
        List<String> kgrams = new ArrayList<>();

        for (int i = 0; i <= word.length() - k; i++) {
            kgrams.add(word.substring(i, i + k));
        }
        
        return kgrams;
    }

    public static List<String> loadWords(String path) {
        List<String> validWords = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split("\\s+");
                for (String word : words) {
                    validWords.add(word);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return validWords;
    }

    private void createIndex() {
        for (int wordId = 0; wordId < wordsList.size(); wordId++) {
            String word = wordsList.get(wordId);
            for (int i = 1; i <= this.k; i++){
                List<String> kgrams = getKGrams(word, i);
                for (String gram : kgrams) {
                    if (!index.containsKey(gram)) {
                        index.put(gram, new HashSet<Integer>());
                    }
                    index.get(gram).add(wordId);
                }
            }
  
        }
    }

    public List<String> findMatches(String pattern) {
        List<Set<Integer>> kgramSets = new ArrayList<>();

        String[] processedList = processPattern(pattern);
        List<String> patternKgrams = new ArrayList<>();
        for (String chunk : processedList) {
            List<String> chunkKgrams = getKGrams(chunk, chunk.length());
            patternKgrams.addAll(chunkKgrams);
        }
        
        for (String gram : patternKgrams) {
            if (index.containsKey(gram)) {
                kgramSets.add(index.get(gram));
            }
        }

        Set<Integer> candidates = intersectSets(kgramSets);
        return postFilter(candidates, pattern);
    }

    private Set<Integer> intersectSets(List<Set<Integer>> sets) {
        if (sets == null || sets.isEmpty()) {
            return null;
        }

        Set<Integer> intersect = new HashSet<>(sets.get(0));
        for (Set<Integer> s : sets) {
            intersect.retainAll(s);
        }
        return intersect.isEmpty() ? null : intersect;
    }

    private static String[] processPattern(String pattern) {
        String[] processed = pattern.split("\\*");
        return processed;
    }

    private List<String> postFilter(Set<Integer> candidates, String pattern) {
        List<String> filtered = new ArrayList<>();
        if (candidates == null || candidates.isEmpty()) {
            return filtered;
        }

        for (Integer candidateId : candidates) {
            String candidate = this.wordsList.get(candidateId);
            if (greedyWildcardMatch(candidate, pattern)) {
                filtered.add(candidate);
            }
        }
        return filtered;
    }

    private boolean greedyWildcardMatch(String s, String p) {
        int i = 0, j = 0;
        int n = p.length(), m = s.length();
        int lastMatch = -1, lastStar = -1;

        while (i < m) {
            if (j < n && (s.charAt(i) == p.charAt(j) || p.charAt(j) == '?')) {
                i++;
                j++;
            } else if (j < n && p.charAt(j) == '*') {
                lastStar = j;
                lastMatch = i;
                j++;
            } else if (lastStar != -1) {
                lastMatch++;
                i = lastMatch;
                j = lastStar + 1;
            } else {
                return false;
            }
        }

        for (int k = j; k < n; k++) {
            if (p.charAt(k) != '*') {
                return false;
            }
        }

        return true;
    }

    public static int getK(String pattern){
        String[] processedList = processPattern(pattern);
        int minChunkLen = Integer.MAX_VALUE;
        for (String chunk : processedList) {
            minChunkLen = Math.min(minChunkLen, chunk.length());
        }
        return minChunkLen;
    }

    public static void main(String[] args) {
        // KGramWildcard c = new KGramWildcard("./dataset/doc3.txt", 2);
        // // c.createIndex();
        // // c.findMatches("gol*");
        // List<String> res = c.findMatches("*cat*dog*");
        // // c.loadIndex("./kgramindex.json");

        // System.out.println(res);
        
        // String path = "./src/dataset/400k_corpus.txt";
        // String pattern = "guarantee*";

        // long startTime0 = System.nanoTime();
        // KGramWildcard kgramIndex = new KGramWildcard(path, 2);
        // long constructionTime = (System.nanoTime() - startTime0) / 1000;
        // LOGGER.info("Construction Time: " + constructionTime + " us");

        // long startTime1 = System.nanoTime();
        // kgramIndex.findMatches(pattern);
        // long kgramTime = (System.nanoTime() - startTime1) / 1000;
        // LOGGER.info("K-Gram Time: " + kgramTime + " us " + '\n' );
    }

    public List<String> getWordsList(){
        return this.wordsList; 
    }
}
