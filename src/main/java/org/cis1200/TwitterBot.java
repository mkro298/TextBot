package org.cis1200;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class TwitterBot {

    
    static final String PATH_TO_TWEETS = "files/dog_feelings_tweets.csv";
    static final int TWEET_COLUMN = 2;
    static final String PATH_TO_OUTPUT_TWEETS = "files/generated_tweets.txt";

    MarkovChain mc;
    NumberGenerator ng;

   
    public TwitterBot(BufferedReader br, int tweetColumn) {
        this(br, tweetColumn, new RandomNumberGenerator());
    }

    
    public TwitterBot(BufferedReader br, int tweetColumn, NumberGenerator ng) {
        mc = new MarkovChain(ng);
        this.ng = ng;
        // Complete this method.
        List<List<String>> data = TweetParser.csvDataToTrainingData(br, tweetColumn);

        for (int i = 0; i < data.size(); i++) {
            Iterator<String> strIter = data.get(i).iterator();
            if (strIter.hasNext()) {
                mc.train(strIter);
            }
        }

    }

    
    public void writeStringsToFile(
            List<String> stringsToWrite, String filePath,
            boolean append
    ) {
        File file = Paths.get(filePath).toFile();
        BufferedWriter bw = null;
        try {
            FileWriter fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            if (append) {
                bw.append(stringsToWrite.get(0));
            } else {
                bw.write(stringsToWrite.get(0));
            }
            for (int i = 1; i < stringsToWrite.size(); i++) {
                bw.append(stringsToWrite.get(i));
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("IOException");
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                System.out.println("Error closing BufferedWriter");
            }
        }
        
    }

    
    public void writeTweetsToFile(
            int numTweets, int numChars, String filePath,
            boolean append
    ) {
        writeStringsToFile(generateTweets(numTweets, numChars), filePath, append);
    }

    
    public String generateTweet(int numWords) {
        this.mc.reset();
        if (numWords <= 0 || !mc.hasNext()) {
            return new String();
        }
        String sentence = "";
        for (int i = 0; i < numWords; i++) {
            if (mc.hasNext() && i != 0) {
                String n = mc.next();
                if (n.equals("<END>")) {
                    sentence += randomPunctuation();
                    mc.reset();
                    i--;
                } else {
                    sentence += " " + n;
                }
            } else if (mc.hasNext()) {
                sentence += mc.next();
            } else {
                sentence += randomPunctuation();
                mc.reset();
                i--;
            }
        }
        sentence += randomPunctuation();
        return sentence;
    }

    
    public List<String> generateTweets(int numTweets, int numChars) {
        List<String> tweets = new ArrayList<>();
        while (numTweets > 0) {
            tweets.add(generateTweetChars(numChars));
            numTweets--;
        }
        return tweets;
    }

    
    public String generateTweetChars(int numChars) {
        if (numChars < 0) {
            throw new IllegalArgumentException(
                    "tweet length cannot be negative"
            );
        }

        String newTweet = generateTweet(1);
        if (newTweet == null || newTweet.length() == 0) {
            return "";
        }

        String tweet = "";
        int numWords = 1;
        while (true) {
            newTweet = generateTweet(numWords);
            if (newTweet.length() > numChars) {
                return tweet;
            }
            tweet = newTweet;
            numWords++;
        }
    }

   
    public String randomPunctuation() {
        char[] puncs = { ';', '?', '!' };
        int m = ng.next(10);
        if (m < puncs.length) {
            return String.valueOf(puncs[m]);
        }
        return ".";
    }

   
    public int fixPunctuation(char punc) {
        return switch (punc) {
            case ';' -> 0;
            case '?' -> 1;
            case '!' -> 2;
            default -> 3;
        };
    }

   
    public boolean isPunctuation(String s) {
        return s.equals(";") || s.equals("?") || s.equals("!") || s.equals(".");
    }

 
    public static boolean isPunctuated(String s) {
        if (s == null || s.equals("")) {
            return false;
        }
        char[] puncs = TweetParser.getPunctuation();
        for (char c : puncs) {
            if (s.charAt(s.length() - 1) == c) {
                return true;
            }
        }
        return false;
    }

   
    public static void main(String[] args) {
        BufferedReader br = FileLineIterator.fileToReader(PATH_TO_TWEETS);
        TwitterBot t = new TwitterBot(br, TWEET_COLUMN);
        List<String> tweets = t.generateTweets(10, 280); // 280 chars in a tweet
        for (String tweet : tweets) {
            System.out.println(tweet);
        }

    
    }

    
    public void fixDistribution(List<String> tweet) {
        List<String> puncs = java.util.Arrays.asList(".", "?", "!", ";");

        if (tweet == null) {
            throw new IllegalArgumentException(
                    "fixDistribution(): tweet argument must not be null."
            );
        } else if (tweet.size() == 0) {
            throw new IllegalArgumentException(
                    "fixDistribution(): tweet argument must not be empty."
            );
        } else if (!puncs.contains(tweet.get(tweet.size() - 1))) {
            throw new IllegalArgumentException(
                    "fixDistribution(): Passed in tweet must be punctuated."
            );
        }

        mc.fixDistribution(
                tweet.stream().map(x -> puncs.contains(x) ? null : x)
                        .collect(java.util.stream.Collectors.toList()),
                true
        );
        List<Integer> puncIndices = new LinkedList<>();
        for (String curWord : tweet) {
            if (isPunctuation(curWord)) {
                puncIndices.add(fixPunctuation(curWord.charAt(0)));
            }
        }
        ng = new ListNumberGenerator(puncIndices);
    }
}
