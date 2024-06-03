package org.cis1200;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.io.BufferedReader;


public class TweetParser {

    
    private static final String BAD_WORD_REGEX = ".*[\\W&&[^']].*";
    private static final String URL_REGEX = "\\bhttp\\S*";
    private static final String URL_REGEX_END_SPACE = "\\bhttp\\S*\\.\\s";
    private static final String URL_REGEX_END_STRING = "\\bhttp\\S*\\.$";

   
    private static final char[] PUNCTUATION = new char[] { '.', '?', '!', ';' };

    
    public static char[] getPunctuation() {
        return PUNCTUATION.clone();
    }

    static String replacePunctuation(String tweet) {
        for (char c : PUNCTUATION) {
            tweet = tweet.replace(c, '.');
        }
        return tweet;
    }

    
    static List<String> tweetSplit(String tweet) {
        List<String> sentences = new LinkedList<>();
        for (String sentence : replacePunctuation(tweet).split("\\.")) {
            sentence = sentence.trim();
            if (!sentence.equals("")) {
                sentences.add(sentence);
            }
        }
        return sentences;
    }

    
    static String extractColumn(String csvLine, int csvColumn) {
        if (csvLine == null || csvColumn < 0) {
            return null;
        }
        String[] str = csvLine.split(",");
        if (csvColumn >= str.length) {
            return null;
        }
        return str[csvColumn]; // Complete this method.
    }

    
    static List<String> csvDataToTweets(BufferedReader br, int tweetColumn) {
        List<String> list = new LinkedList<>();
        FileLineIterator fi = new FileLineIterator(br);
        while (fi.hasNext()) {
            String i = fi.next();
            String toAdd = extractColumn(i, tweetColumn);
            if (toAdd != null) {
                list.add(toAdd);
            }
        }
        return list;
    }


    static String cleanWord(String word) {
        String cleaned = word.trim().toLowerCase();
        if (cleaned.matches(BAD_WORD_REGEX) || cleaned.isEmpty()) {
            return null;
        }
        return cleaned;
    }

   
    static List<String> parseAndCleanSentence(String sentence) {
        List<String> words = new LinkedList<>();
        if (sentence == null) {
            return words;
        }
        String[] str = replacePunctuation(sentence).split(" ");
        for (int i = 0; i < str.length; i++) {
            String curr = str[i];
            curr = cleanWord(curr);
            if (curr != null) {
                words.add(curr);
            }
        }
        return words; // Complete this method.
    }

   
    static String removeURLs(String s) {
        s = s.replaceAll(URL_REGEX_END_STRING, ".");
        s = s.replaceAll(URL_REGEX_END_SPACE, ". ");
        return s.replaceAll(URL_REGEX, "");
    }

    
    static List<List<String>> parseAndCleanTweet(String tweet) {
        List<List<String>> list = new LinkedList<>();
        if (tweet == null) {
            return list;
        }
        String removedU = removeURLs(tweet);
        if (removedU.equals("") || removedU == null) {
            return list;
        }
        List<String> words = tweetSplit(removedU);
        if (words == null) {
            return list;
        }
        for (int i = 0; i < words.size(); i++) {
            String curr = words.get(i);
            if (curr != null && !curr.equals("")) {
                List<String> temo = parseAndCleanSentence(curr);
                if (!temo.isEmpty()) {
                    list.add(parseAndCleanSentence(curr));
                }
            }
        }
        return list; // Complete this method.
    }

   
    public static List<List<String>> csvDataToTrainingData(
            BufferedReader br,
            int tweetColumn
    ) {

        List<String> tweets = csvDataToTweets(br, tweetColumn);
        List<List<String>> cleaned = new ArrayList<>();
        for (int i = 0; i < tweets.size(); i++) {
            String curr = tweets.get(i);
            List<List<String>> temp = parseAndCleanTweet(curr);
            if (!temp.isEmpty()) {
                cleaned.addAll(temp);
            }
        }
        return cleaned; // Complete this method
    }

}
