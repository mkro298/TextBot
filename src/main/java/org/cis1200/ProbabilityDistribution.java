package org.cis1200;

import java.util.*;
import java.util.Map.Entry;


class ProbabilityDistribution<T extends Comparable<T>> {

    // association between keys and number of occurrences
    // store in a TreeMap so that the entries can be accessed in sorted order
    // INVARIANT: keys are never null, values are > 0
    private final TreeMap<T, Integer> records;
    // INVARIANT: total is sum of all values stored in records
    private Integer total = 0;

    public ProbabilityDistribution() {
        this.records = new TreeMap<>();
    }

    
    public int getTotal() {
        return total;
    }

   
    public Set<Entry<T, Integer>> getEntrySet() {
        // Copy constructor so records cannot be modified externally.
        return new TreeSet<>(records.entrySet());
    }

   
    public Map<T, Integer> getRecords() {
        // Copy constructor so records cannot be modified externally.
        return new TreeMap<>(records);
    }

    
    public T pick(NumberGenerator generator) {
        return this.pick(generator.next(total));
    }

    
    public T pick(int index) {
        if (index >= total || index < 0) {
            throw new IllegalArgumentException(
                    "Index has to be less than or " +
                            "equal to the total " + "number of records in the PD"
            );
        }
        int currentIndex = 0;
        // go through the keys in order, summing their occurrences until we
        // reach the weighted key
        for (Map.Entry<T, Integer> entry : records.entrySet()) {
            T key = entry.getKey();
            int currentCount = entry.getValue();
            if (currentIndex + currentCount > index) {
                return key;
            }
            currentIndex += currentCount;
        }
        throw new IllegalStateException(
                "Error in ProbabilityDistribution. Make " +
                        "sure to only add new " + "records through " + "record()"
        );
    }

    
    public void record(T t) {
        records.put(t, records.getOrDefault(t, 0) + 1);
        total++;
    }

   
    public int count(T t) {
        Integer count = records.get(t);
        return count != null ? count : 0;
    }

    
    public Set<T> keySet() {
        return records.keySet();
    }

    
    public int index(T element) {
        int currentIndex = 0;
        for (Map.Entry<T, Integer> entry : records.entrySet()) {
            T key = entry.getKey();
            int currentCount = entry.getValue();
            if (key.equals(element)) {
                return currentIndex;
            }
            currentIndex += currentCount;
        }
        throw new IllegalArgumentException("element not in the distribution");
    }

    
    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        for (Entry<T, Integer> r : records.entrySet()) {
            res.append("Frequency of ");
            res.append(r.getKey());
            res.append(": ");
            res.append(r.getValue());
        }
        return res.toString();
    }
}
