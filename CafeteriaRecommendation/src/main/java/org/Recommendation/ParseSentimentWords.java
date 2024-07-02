package org.Recommendation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ParseSentimentWords {
    private static final String POSITIVE_WORDS_FILE_PATH = "src/main/java/org/Recommendation/HelpingRecommendWords/Positive.txt";
    private static final String NEGATIVE_WORDS_FILE_PATH = "src/main/java/org/Recommendation/HelpingRecommendWords/Negative.txt";

    private Set<String> positiveWords;
    private Set<String> negativeWords;

    public ParseSentimentWords() {
        try {
            this.positiveWords = loadWordsFromFile(POSITIVE_WORDS_FILE_PATH);
            this.negativeWords = loadWordsFromFile(NEGATIVE_WORDS_FILE_PATH);
        } catch (IOException e) {
            System.err.println("Error loading sentiment words: " + e.getMessage());
            this.positiveWords = new HashSet<>();
            this.negativeWords = new HashSet<>();
        }
    }

    private Set<String> loadWordsFromFile(String filePath) throws IOException {
        Set<String> words = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                addWordsToSet(line, words);
            }
        } catch (IOException e) {
            System.err.println("Failed to read file at " + filePath + ": " + e.getMessage());
            throw e;
        }
        return words;
    }

    private void addWordsToSet(String line, Set<String> words) {
        if (line == null || line.trim().isEmpty()) return; // Handle empty lines

        String[] csvWords = line.split(",");
        for (String word : csvWords) {
            if (!word.trim().isEmpty()) {
                words.add(word.trim().toLowerCase());
            }
        }
    }

    public Set<String> getPositiveWords() {
        return new HashSet<>(positiveWords); // Return a copy to avoid external modifications
    }

    public Set<String> getNegativeWords() {
        return new HashSet<>(negativeWords); // Return a copy to avoid external modifications
    }
}
