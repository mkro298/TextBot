package org.cis1200;

import java.util.List;
import java.util.Arrays;
import java.util.NoSuchElementException;


public class ListNumberGenerator implements NumberGenerator {

    // INVARIANT: numbers is nonnull and nonempty
    private final int[] numbers;
    // INVARIANT: smallest is the minimum value of numbers
    private int smallest;
    // INVARIANT: index is a valid position in numbers
    private int index = 0;

  
    private void initializeSmallest() {
        int min = numbers[0];
        for (int x : numbers) {
            if (x < min) {
                min = x;
            }
        }
        this.smallest = min;
    }


    public ListNumberGenerator(List<Integer> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException(
                    "list must be non-null and non-empty"
            );
        }
        this.numbers = new int[list.size()];
        int i = 0;
        for (Integer v : list) {
            if (v == null) {
                throw new IllegalArgumentException("Null element in Integer list.");
            }
            numbers[i] = v;
            i++;
        }
        initializeSmallest();

    }

  
    public ListNumberGenerator(int[] arr) {
        if (arr == null || arr.length == 0) {
            throw new IllegalArgumentException("array must be non-null and non-empty");
        }
        this.numbers = Arrays.copyOf(arr, arr.length);
        initializeSmallest();

    }

    public int next() {
        if (index == numbers.length) {
            index = 0;
        }
        int num = numbers[index];
        index++;
        return num;
    }


    public int next(int bound) {
        if (bound <= smallest) {
            throw new NoSuchElementException(
                    "The list contains no elements "
                            + "greater than or equal to the argued bound"
            );
        }
        int nextInt = next();
        while (nextInt >= bound) {
            nextInt = next();
        }
        return nextInt;
    }

}
