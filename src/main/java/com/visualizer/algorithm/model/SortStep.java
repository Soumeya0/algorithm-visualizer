package com.visualizer.algorithm.model;
import java.util.Arrays;

public class SortStep {
    private int[] array;
    private int comparingIndex1;
    private int comparingIndex2;
    private boolean swapped;
    private String message;
    private int comparisons;
    private int swaps;

    // Constructor
    public SortStep(int[] array, int comparingIndex1, int comparingIndex2, 
                   boolean swapped, String message, int comparisons, int swaps) {
        this.array = Arrays.copyOf(array, array.length); // Create a copy to preserve state
        this.comparingIndex1 = comparingIndex1;
        this.comparingIndex2 = comparingIndex2;
        this.swapped = swapped;
        this.message = message;
        this.comparisons = comparisons;
        this.swaps = swaps;
    }

    // Getters (important for JSON serialization)
    public int[] getArray() { 
        return array; 
    }
    
    public int getComparingIndex1() { 
        return comparingIndex1; 
    }
    
    public int getComparingIndex2() { 
        return comparingIndex2; 
    }
    
    public boolean isSwapped() { 
        return swapped; 
    }
    
    public String getMessage() { 
        return message; 
    }
    
    public int getComparisons() { 
        return comparisons; 
    }
    
    public int getSwaps() { 
        return swaps; 
    }
}