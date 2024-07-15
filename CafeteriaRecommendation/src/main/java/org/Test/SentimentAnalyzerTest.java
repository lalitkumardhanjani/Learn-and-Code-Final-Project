package org.Test;

import org.Recommendation.*;

import org.junit.gen5.api.BeforeEach;
import org.junit.gen5.api.Test;
import org.junit.gen5.api.Assertions;

import java.util.Arrays;
import java.util.List;

public class SentimentAnalyzerTest {

    private static SentimentAnalyzer sentimentAnalyzer;

    @BeforeEach
    public void setUp() {
        ParseSentimentWords mockParser = new ParseSentimentWords();
        sentimentAnalyzer = new SentimentAnalyzer(mockParser);
    }

    @Test
    public void testAnalyzeComments() {
        sentimentAnalyzer.analyzeComments(1, "Great food, loved the taste!");
        Assertions.assertEquals("Great food, loved the taste!", SentimentAnalyzer.getTopComment(1));
    }

    @Test
    public void testCalculateAverageRating() {
        List<Integer> ratings = Arrays.asList(4, 5, 3);
        double averageRating = sentimentAnalyzer.calculateAverageRating(1, ratings);
        Assertions.assertEquals(4.0, averageRating, String.valueOf(0.01));
    }

    @Test
    public void testCalculateSentimentScore() {
        String comments = "Not good, very disappointing";
        double sentimentScore = SentimentAnalyzer.calculateSentimentScore(comments);
        Assertions.assertEquals(1.0, sentimentScore, String.valueOf(0.01));
    }

}
