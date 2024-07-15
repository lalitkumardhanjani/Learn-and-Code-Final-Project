package org.Test;

import org.Recommendation.ParseSentimentWords;
import org.junit.gen5.api.BeforeEach;
import org.junit.gen5.api.Test;
import org.junit.gen5.api.Assertions;

import java.util.Set;

public class ParseSentimentWordsTest {

    private ParseSentimentWords parseSentimentWords;

    @BeforeEach
    public void setUp() {
        parseSentimentWords = new ParseSentimentWords();
    }

    @Test
    public void testPositiveWordsLoading() {
        Set<String> positiveWords = parseSentimentWords.getPositiveWords();
        Assertions.assertFalse(positiveWords.isEmpty());
        // Add more assertions based on your expected positive words
        Assertions.assertTrue(positiveWords.contains("good"));
        Assertions.assertTrue(positiveWords.contains("great"));
    }

    @Test
    public void testNegativeWordsLoading() {
        Set<String> negativeWords = parseSentimentWords.getNegativeWords();
        Assertions.assertFalse(negativeWords.isEmpty());
        // Add more assertions based on your expected negative words
        Assertions.assertTrue(negativeWords.contains("bad"));
        Assertions.assertTrue(negativeWords.contains("disappointing"));
    }
}
