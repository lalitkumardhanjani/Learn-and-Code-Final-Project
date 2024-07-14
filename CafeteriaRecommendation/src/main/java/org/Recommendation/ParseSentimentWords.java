package org.Recommendation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.Constant.Constant;

public class ParseSentimentWords {

    private Set<String> positiveSentimentWords;
    private Set<String> negativeSentimentWords;

    public ParseSentimentWords() {
        try {
            this.positiveSentimentWords = loadWordsFromFile(Constant.POSITIVE_WORDS_FILE_PATH);
            this.negativeSentimentWords = loadWordsFromFile(Constant.NEGATIVE_WORDS_FILE_PATH);
        } catch (IOException e) {
            System.err.println("Error loading sentiment words: " + e.getMessage());
            this.positiveSentimentWords = new HashSet<>();
            this.negativeSentimentWords = new HashSet<>();
        }
    }

    private Set<String> loadWordsFromFile(String filePath) throws IOException {
        Set<String> sentimentWords = new HashSet<>();
        try (BufferedReader fileReader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = fileReader.readLine()) != null) {
                addWordsToSet(line, sentimentWords);
            }
        } catch (IOException e) {
            System.err.println("Failed to read file at " + filePath + ": " + e.getMessage());
            throw e;
        }
        return sentimentWords;
    }

    private void addWordsToSet(String line, Set<String> sentimentWords) {
        if (line == null || line.trim().isEmpty()) return;

        String[] csvWords = line.split(",");
        for (String word : csvWords) {
            if (!word.trim().isEmpty()) {
                sentimentWords.add(word.trim().toLowerCase());
            }
        }
    }

    public Set<String> getPositiveWords() {
        return new HashSet<>(positiveSentimentWords);
    }

    public Set<String> getNegativeWords() {
        return new HashSet<>(negativeSentimentWords);
    }
}
