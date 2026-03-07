package com.visualizer.algorithm.sorting;

import com.visualizer.algorithm.model.SortStep;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class BubbleSort {
    
    public List<SortStep> sort(int[] inputArray) {
        List<SortStep> steps = new ArrayList<>();
        int[] arr = inputArray.clone(); // Work on a copy
        int n = arr.length;
        int comparisons = 0;
        int swaps = 0;
        
        // Initial state
        steps.add(new SortStep(arr, -1, -1, false, 
            "Starting array", comparisons, swaps));
        
        // Bubble Sort algorithm
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                // COMPARE step
                comparisons++;
                steps.add(new SortStep(arr, j, j + 1, false, 
                    String.format("Comparing %d and %d", arr[j], arr[j + 1]), 
                    comparisons, swaps));
                
                if (arr[j] > arr[j + 1]) {
                    // SWAP step
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    swaps++;
                    
                    steps.add(new SortStep(arr, j, j + 1, true, 
                        String.format("Swapped %d and %d", arr[j + 1], arr[j]), 
                        comparisons, swaps));
                }
            }
            // Mark sorted portion
            steps.add(new SortStep(arr, n - i - 1, -1, false, 
                String.format("Element %d is now in place", arr[n - i - 1]), 
                comparisons, swaps));
        }
        
        // Final state
        steps.add(new SortStep(arr, -1, -1, false, 
            "Array is sorted!", comparisons, swaps));
        
        return steps;
    }
}