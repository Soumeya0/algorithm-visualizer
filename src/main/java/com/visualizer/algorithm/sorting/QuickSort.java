package com.visualizer.algorithm.sorting;

import com.visualizer.algorithm.model.SortStep;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class QuickSort {
    
    public List<SortStep> sort(int[] inputArray) {
        List<SortStep> steps = new ArrayList<>();
        int[] arr = inputArray.clone();
        int comparisons = 0;
        int swaps = 0;
        
        // Initial state
        steps.add(new SortStep(arr, -1, -1, false,
            "Starting Quick Sort", comparisons, swaps));
        
        quickSort(arr, 0, arr.length - 1, steps, comparisons, swaps);
        
        // Final state
        steps.add(new SortStep(arr, -1, -1, false,
            "Array is sorted!", comparisons, swaps));
        
        return steps;
    }
    
    private void quickSort(int[] arr, int low, int high, 
                          List<SortStep> steps, int comparisons, int swaps) {
        if (low < high) {
            int pivotIndex = partition(arr, low, high, steps, comparisons, swaps);
            
            quickSort(arr, low, pivotIndex - 1, steps, comparisons, swaps);
            quickSort(arr, pivotIndex + 1, high, steps, comparisons, swaps);
        }
    }
    
    private int partition(int[] arr, int low, int high,
                         List<SortStep> steps, int comparisons, int swaps) {
        int pivot = arr[high];
        
        // Record pivot selection
        steps.add(new SortStep(arr, high, -1, false,
            String.format("Selected pivot: %d", pivot),
            comparisons, swaps));
        
        int i = low - 1;
        
        for (int j = low; j < high; j++) {
            // COMPARE step
            comparisons++;
            steps.add(new SortStep(arr, j, high, false,
                String.format("Comparing %d with pivot %d", arr[j], pivot),
                comparisons, swaps));
            
            if (arr[j] <= pivot) {
                i++;
                
                // SWAP step
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
                swaps++;
                
                steps.add(new SortStep(arr, i, j, true,
                    String.format("Swapped %d and %d", arr[i], arr[j]),
                    comparisons, swaps));
            }
        }
        
        // Place pivot in correct position
        int temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;
        swaps++;
        
        steps.add(new SortStep(arr, i + 1, high, true,
            String.format("Placed pivot %d at position %d", arr[i + 1], i + 1),
            comparisons, swaps));
        
        return i + 1;
    }
}