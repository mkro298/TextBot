package org.cis1200;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

/** Tests for TweetParser */
public class TweetParserTest {

    // A helper function to create a singleton list from a word
    private static List<String> singleton(String word) {
        List<String> l = new LinkedList<String>();
        l.add(word);
        return l;
    }

    // A helper function for creating lists of strings
    private static List<String> listOfArray(String[] words) {
        List<String> l = new LinkedList<String>();
        for (String s : words) {
            l.add(s);
        }
        return l;
    }

    // Cleaning and filtering tests -------------------------------------------
    @Test
    public void removeURLsTest() {
        assertEquals("abc . def.", TweetParser.removeURLs("abc http://www.cis.upenn.edu. def."));
        assertEquals("abc", TweetParser.removeURLs("abc"));
        assertEquals("abc ", TweetParser.removeURLs("abc http://www.cis.upenn.edu"));
        assertEquals("abc .", TweetParser.removeURLs("abc http://www.cis.upenn.edu."));
        assertEquals(" abc ", TweetParser.removeURLs("http:// abc http:ala34?#?"));
        assertEquals(" abc  def", TweetParser.removeURLs("http:// abc http:ala34?#? def"));
        assertEquals(" abc  def", TweetParser.removeURLs("https:// abc https``\":ala34?#? def"));
        assertEquals("abchttp", TweetParser.removeURLs("abchttp"));
    }

    @Test
    public void testCleanWord() {
        assertEquals("abc", TweetParser.cleanWord("abc"));
        assertEquals("abc", TweetParser.cleanWord("ABC"));
        assertNull(TweetParser.cleanWord("@abc"));
        assertEquals("ab'c", TweetParser.cleanWord("ab'c"));
    }

    /* **** ****** **** WRITE YOUR TESTS BELOW THIS LINE **** ****** **** */

    /* **** ****** ***** **** EXTRACT COLUMN TESTS **** **** ****** ***** */

    /* Here's an example test case. Be sure to add your own as well */
    @Test
    public void testExtractColumnGetsCorrectColumn() {
        assertEquals(
                " This is a tweet.",
                TweetParser.extractColumn(
                        "wrongColumn, wrong column, wrong column!, This is a tweet.", 3
                )
        );
    }

    @Test
    public void testExtractColumnNullCSVLineandInvalidColumns() {
        assertNull(TweetParser.extractColumn(null, 0));
        assertNull(TweetParser.extractColumn("This is a tweet", -3));
        assertNull(TweetParser.extractColumn("This is also a tweet", 20));
    }

    @Test
    public void testExtractColumnSingleValue() {
        assertEquals("", TweetParser.extractColumn("", 0));
    }

    /* **** ****** ***** ***** CSV DATA TO TWEETS ***** **** ****** ***** */

    /* Here's an example test case. Be sure to add your own as well */
    @Test
    public void testCsvDataToTweetsSimpleCSV() {
        StringReader sr = new StringReader(
                "0, The end should come here.\n" +
                        "1, This comes from data with no duplicate words!"
        );
        BufferedReader br = new BufferedReader(sr);
        List<String> tweets = TweetParser.csvDataToTweets(br, 1);
        List<String> expected = new LinkedList<String>();
        expected.add(" The end should come here.");
        expected.add(" This comes from data with no duplicate words!");
        assertEquals(expected, tweets);
    }
    @Test
    public void testCsvDataToTweetsEmptyReader() {
        BufferedReader br = new BufferedReader(new StringReader(""));
        List<String> result = TweetParser.csvDataToTweets(br, 1);
        assert result.isEmpty();
    }

    @Test
    public void testCsvDataToTweetsInvalidColumnNumber() {
        String csvData = "1,John,Doe\n2,Jane,Smith";
        BufferedReader br = new BufferedReader(new StringReader(csvData));
        List<String> result = TweetParser.csvDataToTweets(br, 5);
        assert (result.isEmpty());
    }

    @Test
    public void testCsvDataToTweetsWithNullData() {
        String csvData = "1,John,\n2,Jane,";
        BufferedReader br = new BufferedReader(new StringReader(csvData));
        List<String> result = TweetParser.csvDataToTweets(br, 3);
        assert (result.isEmpty());
    }

    @Test
    public void testCsvDataToTweetsMixedDataWithEmptyTweets() {
        String csvData = "1,John,Doe\n2,,Smith";
        BufferedReader br = new BufferedReader(new StringReader(csvData));
        List<String> result = TweetParser.csvDataToTweets(br, 2);
        assertEquals(List.of("Doe", "Smith"), result);
    }

    @Test
    public void testCsvDataToTweetsEmptyTweetInTheMiddle() {
        String csvData = "1,John,Doe\n2,,\n3,Jane,Smith";
        BufferedReader br = new BufferedReader(new StringReader(csvData));
        List<String> result = TweetParser.csvDataToTweets(br, 2);
        assertEquals(List.of("Doe", "Smith"), result);
    }

    @Test
    public void testCsvDataToTweetsMalformedCSVData() {
        String csvData = "1,John,Doe\n2,Jane,Smith\nInvalidData";
        BufferedReader br = new BufferedReader(new StringReader(csvData));
        List<String> result = TweetParser.csvDataToTweets(br, 2);
        assertEquals(List.of("Doe", "Smith"), result);
    }

    @Test
    public void testCsvDataToTweetsLargeInputData() {
        StringBuilder csvDataBuilder = new StringBuilder();
        for (int i = 1; i <= 1000; i++) {
            csvDataBuilder.append(i).append(",John,Doe\n");
        }
        BufferedReader br = new BufferedReader(new StringReader(csvDataBuilder.toString()));
        List<String> result = TweetParser.csvDataToTweets(br, 2);
        assertEquals(1000, result.size());
    }

    @Test
    public void testCsvDataToTweetsSingleLine() {
        String csvData = "1,John,Doe";
        BufferedReader br = new BufferedReader(new StringReader(csvData));
        List<String> result = TweetParser.csvDataToTweets(br, 1);
        assertEquals(List.of("John"), result);
    }

    @Test
    public void testCsvDataToTweetsSingleLineEmptyTweet() {
        String csvData = "1,,";
        BufferedReader br = new BufferedReader(new StringReader(csvData));
        List<String> result = TweetParser.csvDataToTweets(br, 2);
        assert (result.isEmpty());
    }

    @Test
    public void testCsvDataToTweetsNoTweetsInColumn() {
        String csvData = "1,John,Doe\n2,Jane,Smith";
        BufferedReader br = new BufferedReader(new StringReader(csvData));
        List<String> result = TweetParser.csvDataToTweets(br, 3);
        assert (result.isEmpty());
    }

    /* **** ****** ***** ** PARSE AND CLEAN SENTENCE ** ***** ****** ***** */

    /* Here's an example test case. Be sure to add your own as well */
    @Test
    public void parseAndCleanSentenceNonEmptyFiltered() {
        List<String> sentence = TweetParser.parseAndCleanSentence("abc #@#F");
        List<String> expected = new LinkedList<String>();
        expected.add("abc");
        assertEquals(expected, sentence);
    }

    @Test
    public void testEmptyString() {
        String sentence = "";
        List<String> result = TweetParser.parseAndCleanSentence(sentence);
        assert (result.isEmpty());
    }

    @Test
    public void testNullString() {
        String sentence = null;
        List<String> result = TweetParser.parseAndCleanSentence(sentence);
        assert (result.isEmpty());
    }



    @Test
    public void testSentenceWithMultipleSpaces() {
        String sentence = "   Extra    spaces   should   not   affect   parsing   ";
        List<String> result = TweetParser.parseAndCleanSentence(sentence);
        assertEquals(List.of("extra", "spaces", "should", "not", "affect", "parsing"), result);
    }


    @Test
    public void testMixedCaseWords() {
        String sentence = "MiXeD";
        List<String> result = TweetParser.parseAndCleanSentence(sentence);
        assertEquals(List.of("mixed"), result);
    }

    @Test
    public void testSentenceWithSpecialCharacters() {
        String sentence = "Special characters @#$%^&* should be handled";
        List<String> result = TweetParser.parseAndCleanSentence(sentence);
        assertEquals(List.of("special", "characters", "should", "be", "handled"), result);
    }

    @Test
    public void testSentenceEndingWithBadWord() {
        String sentence = "This is a bad\\word";
        List<String> result = TweetParser.parseAndCleanSentence(sentence);
        assertEquals(List.of("this", "is", "a"), result);
    }

    @Test
    public void testSingleCharacterWords() {
        String sentence = "A B C D E F G";
        List<String> result = TweetParser.parseAndCleanSentence(sentence);
        assertEquals(List.of("a", "b", "c", "d", "e", "f", "g"), result);
    }

    @Test
    public void testConsecutivePunctuation() {
        String sentence = "Consecutive punctuation ... should not cause issues";
        List<String> result = TweetParser.parseAndCleanSentence(sentence);
        assertEquals(List.of("consecutive", "punctuation", "should",
                "not", "cause", "issues"), result);
    }

    @Test
    public void testRepeatedBadWords() {
        String sentence = "* \\ are && ..";
        List<String> result = TweetParser.parseAndCleanSentence(sentence);
        assertEquals(List.of("are"), result);
    }

    @Test
    public void testLeadingAndTrailingSpaces() {
        String sentence = "   Trim leading and trailing spaces   ";
        List<String> result = TweetParser.parseAndCleanSentence(sentence);
        assertEquals(List.of("trim", "leading", "and", "trailing", "spaces"), result);
    }

    @Test
    public void testOnlyBadWords() {
        String sentence = "* \\ &";
        List<String> result = TweetParser.parseAndCleanSentence(sentence);
        assert (result.isEmpty());
    }
    /* **** ****** ***** **** PARSE AND CLEAN TWEET *** ***** ****** ***** */

    /* Here's an example test case. Be sure to add your own as well */
    @Test
    public void testParseAndCleanTweetRemovesURLS1() {
        List<List<String>> sentences = TweetParser
                .parseAndCleanTweet("abc http://www.cis.upenn.edu");
        List<List<String>> expected = new LinkedList<List<String>>();
        expected.add(singleton("abc"));
        assertEquals(expected, sentences);
    }

    @Test
    public void testEmptyTweet() {
        String tweet = "";
        List<List<String>> result = TweetParser.parseAndCleanTweet(tweet);
        assert (result.isEmpty());
    }

    @Test
    public void testTweetWithURLs() {
        String tweet = "Check out this link: https://example.com. " +
                "Another link: http://anotherlink.com.";
        List<List<String>> result = TweetParser.parseAndCleanTweet(tweet);
        assertEquals(
                List.of(
                        List.of("check", "out", "this"),
                        List.of("another")
                ),
                result
        );
    }

    @Test
    public void testTweetWithSpecialCharacters() {
        String tweet = "Special characters @#$%^&* should be handled. Another sentence.";
        List<List<String>> result = TweetParser.parseAndCleanTweet(tweet);
        assertEquals(
                List.of(
                        List.of("special", "characters", "should", "be", "handled"),
                        List.of("another", "sentence")
                ),
                result
        );
    }

    @Test
    public void testTweetWithMultipleSpaces() {
        String tweet = "   Extra    spaces   should   not   affect   parsing.   ";
        List<List<String>> result = TweetParser.parseAndCleanTweet(tweet);
        assertEquals(
                List.of(
                        List.of("extra", "spaces", "should", "not", "affect", "parsing")
                ),
                result
        );
    }


    @Test
    public void testTweetWithMixedCaseBadWords() {
        String tweet = "Do NoT UsE bAd\\ WoRds*. Another sentence.";
        List<List<String>> result = TweetParser.parseAndCleanTweet(tweet);
        assertEquals(
                List.of(
                        List.of("do", "not", "use"),
                        List.of("another", "sentence")
                ),
                result
        );
    }

    @Test
    public void testSingleWordTweet() {
        String tweet = "SingleWord";
        List<List<String>> result = TweetParser.parseAndCleanTweet(tweet);
        assertEquals(
                List.of(
                        List.of("singleword")
                ),
                result
        );
    }

    @Test
    public void testSingleSentenceTweet() {
        String tweet = "Single sentence.";
        List<List<String>> result = TweetParser.parseAndCleanTweet(tweet);
        assertEquals(
                List.of(
                        List.of("single", "sentence")
                ),
                result
        );
    }

    @Test
    public void testTweetWithOnlyBadWords() {
        String tweet = "* * *";
        List<List<String>> result = TweetParser.parseAndCleanTweet(tweet);
        assert (result.isEmpty());
    }

    @Test
    public void testTweetWithLeadingAndTrailingSpaces() {
        String tweet = "   Trim leading and trailing spaces.   ";
        List<List<String>> result = TweetParser.parseAndCleanTweet(tweet);
        assertEquals(
                List.of(
                        List.of("trim", "leading", "and", "trailing", "spaces")
                ),
                result
        );
    }

    @Test
    public void testTweetWithEmptySentences() {
        String tweet = "Sentence 1. Sentence 2.   .   Sentence 3.";
        List<List<String>> result = TweetParser.parseAndCleanTweet(tweet);
        assertEquals(
                List.of(
                        List.of("sentence", "1"),
                        List.of("sentence", "2"),
                        List.of("sentence", "3")
                ),
                result
        );
    }

    @Test
    public void testNullTweet() {
        List<List<String>> result = TweetParser.parseAndCleanTweet(null);
        assert (result.isEmpty());
    }

    /* **** ****** ***** ** CSV DATA TO TRAINING DATA ** ***** ****** **** */

    /* Here's an example test case. Be sure to add your own as well */
    @Test
    public void testCsvDataToTrainingDataSimpleCSV() {
        StringReader sr = new StringReader(
                "0, The end should come here.\n" +
                        "1, This comes from data with no duplicate words!"
        );
        BufferedReader br = new BufferedReader(sr);
        List<List<String>> tweets = TweetParser.csvDataToTrainingData(br, 1);
        List<List<String>> expected = new LinkedList<List<String>>();
        expected.add(listOfArray("the end should come here".split(" ")));
        expected.add(listOfArray("this comes from data with no duplicate words".split(" ")));
        assertEquals(expected, tweets);
    }

    @Test
    public void testMixedDataWithEmptyTweets() {
        String csvData = "1,John,Doe\n2,,Smith";
        BufferedReader br = new BufferedReader(new StringReader(csvData));
        List<List<String>> result = TweetParser.csvDataToTrainingData(br, 2);
        assertEquals(
                List.of(
                        List.of("doe"),
                        List.of("smith")
                ),
                result
        );
    }

    @Test
    public void testValidDataWithNullTweets() {
        String csvData = "1,John,\n2,Jane,Smith";
        BufferedReader br = new BufferedReader(new StringReader(csvData));
        List<List<String>> result = TweetParser.csvDataToTrainingData(br, 3);
        assert (result.isEmpty());
    }


}
