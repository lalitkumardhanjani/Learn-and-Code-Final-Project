package org.Recommendation;

import java.util.Set;

public class SentimentAnalyzer {
    private Set<String> positiveWords;
    private Set<String> negativeWords;

    public SentimentAnalyzer(ParseSentimentWords parser) {
        this.positiveWords = parser.getPositiveWords();
        this.negativeWords = parser.getNegativeWords();
    }

    public double calculateSentimentScore(String comments) {
        int positiveCount = 0;
        int negativeCount = 0;

        for (String word : comments.split("\\W+")) {
            if (positiveWords.contains(word.toLowerCase())) {
                positiveCount++;
            } else if (negativeWords.contains(word.toLowerCase())) {
                negativeCount++;
            }
        }

        int totalWords = positiveCount + negativeCount;
        if (totalWords == 0) return 3.0; // Neutral sentiment if no known sentiment words found

        double sentimentScore = (5.0 * positiveCount - 5.0 * negativeCount) / totalWords;
        return Math.max(1.0, Math.min(5.0, sentimentScore)); // Normalize to 1-5 scale
    }
}
