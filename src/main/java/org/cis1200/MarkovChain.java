package org.cis1200;

import java.util.*;


public class MarkovChain implements Iterator<String> {
    /** source of random numbers */
    private NumberGenerator ng;
    /** probability distribution of initial words in a sentence */
    final ProbabilityDistribution<String> startWords;
    /** for each word, probability distribution of next word in a sentence */
    final Map<String, ProbabilityDistribution<String>> chain;
    /** end of sentence marker */
    static final String END_TOKEN = "<END>";

    // add field(s) used in implementing the Iterator functionality
    private String currWord;
    private String startWord;

    public MarkovChain() {
        this(new RandomNumberGenerator());
    }


    public MarkovChain(NumberGenerator ng) {
        if (ng == null) {
            throw new IllegalArgumentException(
                    "NumberGenerator input cannot be null"
            );
        }
        this.chain = new TreeMap<>();
        this.ng = ng;
        this.startWords = new ProbabilityDistribution<>();
        reset();
    }


    void addBigram(String first, String second) {
        // Complete this method.
        if (first == null || second == null) {
            throw new IllegalArgumentException();
        }
        if (chain.containsKey(first)) {
            ProbabilityDistribution<String> temp = chain.get(first);
            temp.record(second);
            chain.put(first, temp);
        } else {
            ProbabilityDistribution<String> map = new ProbabilityDistribution();
            map.record(second);
            chain.put(first, map);
        }
    }

    
    public void train(Iterator<String> sentence) {
        // Complete this method.
        if (sentence == null) {
            throw new IllegalArgumentException();
        }
        String prev = sentence.next();
        startWords.record(prev);
        while (sentence.hasNext()) {
            String curr = sentence.next();
            addBigram(prev, curr);
            prev = curr;
        }
        addBigram(prev, END_TOKEN);
    }

    
    ProbabilityDistribution<String> get(String token) {
        if (token == null) {
            throw new IllegalArgumentException("token cannot be null.");
        }
        return chain.get(token);
    }

    
    public void reset(String start) {
        if (start == null) {
            throw new IllegalArgumentException("start cannot be null");
        }
        // Complete this method.
        startWord = start;
        if (!start.equals(END_TOKEN) && !chain.containsKey(start)) {
            currWord = "$";
        } else if (start.equals(END_TOKEN)) {
            currWord = END_TOKEN;
        } else {
            currWord = "*";
        }
    }

    
    public void reset() {
        if (startWords.getTotal() == 0) {
            reset(END_TOKEN);
        } else {
            reset(startWords.pick(ng));
        }
    }

   
    @Override
    public boolean hasNext() {
        // Complete this method.
        if (currWord.equals(END_TOKEN)) {
            return false;
        }
        return true;
    }

   
    @Override
    public String next() {
        if (currWord.equals("$")) {
            currWord = END_TOKEN;
            return startWord;
        }
        if (currWord.equals("*")) {
            currWord = startWord;
            return currWord;
        }
        try {
            String word = chain.get(currWord).pick(ng);
            currWord = word;
            return word;
        } catch (NullPointerException e) {
            throw new NoSuchElementException();
        }

    }

   
    public void fixDistribution(List<String> words) {
        if (words == null || words.isEmpty()) {
            throw new IllegalArgumentException("Invalid word list for fixDistribution");
        }
        fixDistribution(words, words.size() == 1);
    }

   
    public void fixDistribution(List<String> words, boolean pickFirst) {
        if (words == null || words.isEmpty()) {
            throw new IllegalArgumentException("Invalid word list for fixDistribution");
        }

        String curWord = words.remove(0);
        if (startWords.count(curWord) < 1) {
            throw new IllegalArgumentException(
                    "first word " + curWord + " " +
                            "not present in " + "startWords"
            );
        }

        List<Integer> probabilityNumbers = new LinkedList<>();
        if (pickFirst) {
            probabilityNumbers.add(startWords.index(curWord));
        }

        while (words.size() > 0) {
            ProbabilityDistribution<String> curDistribution;
            // if we were just at null, reset. otherwise, continue on the chain
            if (curWord == null) {
                curDistribution = startWords;
            } else {
                curDistribution = chain.get(curWord);
            }

            String nextWord = words.remove(0);
            if (nextWord != null) {
                if (curDistribution.count(nextWord) < 1) {
                    throw new IllegalArgumentException(
                            "word " + nextWord +
                                    " not found as a child of" + " word " + curWord
                    );
                }
                probabilityNumbers.add(curDistribution.index(nextWord));
            } else {
                probabilityNumbers.add(0);
            }
            curWord = nextWord;
        }

        ng = new ListNumberGenerator(probabilityNumbers);
    }

    
    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        for (Map.Entry<String, ProbabilityDistribution<String>> c : chain.entrySet()) {
            res.append(c.getKey());
            res.append(": ");
            res.append(c.getValue().toString());
            res.append("\n");
        }
        return res.toString();
    }
}
