package org.Recommendation;

import java.util.*;

public class SentimentAnalyzer {
    private static Set<String> positiveWords;
    private static Set<String> negativeWords;
    private static Map<Integer, List<String>> foodItemComments; // Stores comments by food item ID
    private final Map<Integer, Map<String, Integer>> foodItemCommentFrequencies; // Stores word frequencies for sentiment analysis

    public SentimentAnalyzer(ParseSentimentWords parser) {
        this.positiveWords = parser.getPositiveWords();
        this.negativeWords = parser.getNegativeWords();
        this.foodItemComments = new HashMap<>();
        this.foodItemCommentFrequencies = new HashMap<>();
    }

    public void analyzeComments(int foodItemId, String comments) {
        if (comments == null || comments.isEmpty()) return; // Handle empty or null comments

        // Initialize map for new food item
        foodItemCommentFrequencies.putIfAbsent(foodItemId, new HashMap<>());

        // Split comments into words and analyze sentiment
        for (String word : comments.split("\\W+")) {
            String lowerCaseWord = word.toLowerCase();
            if (positiveWords.contains(lowerCaseWord) || negativeWords.contains(lowerCaseWord)) {
                Map<String, Integer> frequencies = foodItemCommentFrequencies.get(foodItemId);
                frequencies.put(lowerCaseWord, frequencies.getOrDefault(lowerCaseWord, 0) + 1);
            }
        }

        // Store the comment
        foodItemComments.putIfAbsent(foodItemId, new ArrayList<>());
        foodItemComments.get(foodItemId).add(comments);
    }

    public double calculateAverageRating(int foodItemId, List<Integer> ratings) {
        if (ratings == null || ratings.isEmpty()) return 0.0; // Handle null or empty ratings
        double sum = 0;
        for (int rating : ratings) {
            sum += rating;
        }
        return sum / ratings.size();
    }

    public static List<String> getTopComments(int foodItemId) {
        List<String> comments = foodItemComments.getOrDefault(foodItemId, Collections.emptyList());

        // Sort comments based on frequency of positive and negative words
        comments.sort((c1, c2) -> Double.compare(calculateSentimentScore(c2), calculateSentimentScore(c1))); // Higher sentiment score first

        return comments.size() > 3 ? comments.subList(0, 3) : comments;
    }

    private static double calculateSentimentScore(String comments) {
        if (comments == null || comments.isEmpty()) return 3.0; // Handle null or empty comments

        int positiveCount = 0;
        int negativeCount = 0;

        for (String word : comments.split("\\W+")) {
            String lowerCaseWord = word.toLowerCase();
            if (positiveWords.contains(lowerCaseWord)) {
                positiveCount++;
            } else if (negativeWords.contains(lowerCaseWord)) {
                negativeCount++;
            }
        }

        int totalWords = positiveCount + negativeCount;
        if (totalWords == 0) return 3.0; // Neutral score if no sentiment words

        double sentimentScore = (5.0 * positiveCount - 5.0 * negativeCount) / totalWords;
        return Math.max(1.0, Math.min(5.0, sentimentScore)); // Ensure score is within [1, 5]
    }
}
