package org.Recommendation;

import java.util.*;

public class SentimentAnalyzer {
    private static Set<String> positiveSentimentWords;
    private static Set<String> negativeSentimentWords;
    private static Map<Integer, List<String>> commentsByFoodItem;
    private final Map<Integer, Map<String, Integer>> wordFrequenciesByFoodItem;

    public SentimentAnalyzer(ParseSentimentWords sentimentWordsParser) {
        positiveSentimentWords = sentimentWordsParser.getPositiveWords();
        negativeSentimentWords = sentimentWordsParser.getNegativeWords();
        commentsByFoodItem = new HashMap<>();
        wordFrequenciesByFoodItem = new HashMap<>();
    }

    public void analyzeComments(int foodItemId, String comments) {
        if (comments == null || comments.isEmpty()) return;

        wordFrequenciesByFoodItem.putIfAbsent(foodItemId, new HashMap<>());

        for (String word : comments.split("\\W+")) {
            String lowerCaseWord = word.toLowerCase();
            if (positiveSentimentWords.contains(lowerCaseWord) || negativeSentimentWords.contains(lowerCaseWord)) {
                Map<String, Integer> frequencies = wordFrequenciesByFoodItem.get(foodItemId);
                frequencies.put(lowerCaseWord, frequencies.getOrDefault(lowerCaseWord, 0) + 1);
            }
        }

        commentsByFoodItem.putIfAbsent(foodItemId, new ArrayList<>());
        commentsByFoodItem.get(foodItemId).add(comments);
    }

    public double calculateAverageRating(int foodItemId, List<Integer> ratings) {
        if (ratings == null || ratings.isEmpty()) return 0.0;
        double sumOfRatings = 0;
        for (int rating : ratings) {
            sumOfRatings += rating;
        }
        return sumOfRatings / ratings.size();
    }

    public static String getTopComment(int foodItemId) {
        List<String> comments = commentsByFoodItem.getOrDefault(foodItemId, Collections.emptyList());
        return comments.isEmpty() ? null : comments.get(0);
    }

    public static double calculateSentimentScore(String comments) {
        if (comments == null || comments.isEmpty()) return 3.0;

        int positiveWordCount = 0;
        int negativeWordCount = 0;

        for (String word : comments.split("\\W+")) {
            String lowerCaseWord = word.toLowerCase();
            if (positiveSentimentWords.contains(lowerCaseWord)) {
                positiveWordCount++;
            } else if (negativeSentimentWords.contains(lowerCaseWord)) {
                negativeWordCount++;
            }
        }

        int totalSentimentWords = positiveWordCount + negativeWordCount;
        if (totalSentimentWords == 0) return 3.0;

        double sentimentScore = (5.0 * positiveWordCount - 5.0 * negativeWordCount) / totalSentimentWords;
        return Math.max(1.0, Math.min(5.0, sentimentScore));
    }
}
