package org.Recommendation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ParseSentimentWords {
    private static final String POSITIVE_WORDS_FILE_PATH = "C:\\Users\\lalit.k.dhanjani\\IdeaProjects\\CafeteriaRecommendation\\src\\main\\java\\Database\\HelpingRecommendWords\\Positive.txt";
    private static final String NEGATIVE_WORDS_FILE_PATH = "C:\\Users\\lalit.k.dhanjani\\IdeaProjects\\CafeteriaRecommendation\\src\\main\\java\\Database\\HelpingRecommendWords\\Negative.txt";

    private Set<String> positiveWords;
    private Set<String> negativeWords;

    public ParseSentimentWords() throws IOException {
        this.positiveWords = loadWordsFromFile(POSITIVE_WORDS_FILE_PATH);
        this.negativeWords = loadWordsFromFile(NEGATIVE_WORDS_FILE_PATH);
    }

    private Set<String> loadWordsFromFile(String filePath) throws IOException {
        Set<String> words = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                addWordsToSet(line, words);
            }
        }
        return words;
    }

    private void addWordsToSet(String line, Set<String> words) {
        String[] csvWords = line.split(",");
        for (String word : csvWords) {
            words.add(word.trim().toLowerCase());
        }
    }

    public Set<String> getPositiveWords() {
        return positiveWords;
    }

    public Set<String> getNegativeWords() {
        return negativeWords;
    }
}
