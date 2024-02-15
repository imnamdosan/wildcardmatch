package com.david;
import java.util.ArrayList;
import java.util.List;

public class Greedy {
    public static List<String> findMatches(List<String> words, String pattern) {
        List<String> matches = new ArrayList<>();
        for (String word : words) {
            if (greedyWildcardMatch(word, pattern)) {
                matches.add(word);
            }
        }
        return matches;
    }

    public static boolean greedyWildcardMatch(String s, String p) {
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


}
