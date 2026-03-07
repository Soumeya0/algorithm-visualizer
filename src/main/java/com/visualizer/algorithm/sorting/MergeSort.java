package com.visualizer.algorithm.sorting;

import com.visualizer.algorithm.model.SortStep;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class MergeSort {
    
    public List<SortStep> sort(int[] inputArray) {
        List<SortStep> steps = new ArrayList<>();
        int[] arr = inputArray.clone();
        int comparisons = 0;
        int swaps = 0;
        
        // Initial state
        steps.add(new SortStep(arr, -1, -1, false, 
            "Starting Merge Sort", comparisons, swaps));
        
        // Create temporary array for merging
        int[] temp = new int[arr.length];
        mergeSort(arr, temp, 0, arr.length - 1, steps, comparisons, swaps);
        
        // Final state
        steps.add(new SortStep(arr, -1, -1, false, 
            "Array is sorted!", comparisons, swaps));
        
        return steps;
    }
    
    private void mergeSort(int[] arr, int[] temp, int left, int right, 
                          List<SortStep> steps, int comparisons, int swaps) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            
            // Record division step
            steps.add(new SortStep(arr, left, right, false,
                String.format("Dividing at index %d", mid), 
                comparisons, swaps));
            
            // Sort left half
            mergeSort(arr, temp, left, mid, steps, comparisons, swaps);
            
            // Sort right half
            mergeSort(arr, temp, mid + 1, right, steps, comparisons, swaps);
            
            // Merge the sorted halves
            merge(arr, temp, left, mid, right, steps, comparisons, swaps);
        }
    }
    
    private void merge(int[] arr, int[] temp, int left, int mid, int right,
                      List<SortStep> steps, int comparisons, int swaps) {
        // Copy to temp
        for (int i = left; i <= right; i++) {
            temp[i] = arr[i];
        }
        
        int i = left;
        int j = mid + 1;
        int k = left;
        
        while (i <= mid && j <= right) {
            // COMPARE step
            comparisons++;
            steps.add(new SortStep(arr, i, j, false,
                String.format("Comparing %d and %d", temp[i], temp[j]),
                comparisons, swaps));
            
            if (temp[i] <= temp[j]) {
                arr[k] = temp[i];
                swaps++;
                steps.add(new SortStep(arr, k, -1, true,
                    String.format("Placing %d", temp[i]),
                    comparisons, swaps));
                i++;
            } else {
                arr[k] = temp[j];
                swaps++;
                steps.add(new SortStep(arr, k, -1, true,
                    String.format("Placing %d", temp[j]),
                    comparisons, swaps));
                j++;
            }
            k++;
        }
        
        // Copy remaining elements
        while (i <= mid) {
            arr[k] = temp[i];
            swaps++;
            steps.add(new SortStep(arr, k, -1, true,
                String.format("Placing %d", temp[i]),
                comparisons, swaps));
            i++;
            k++;
        }
        
        while (j <= right) {
            arr[k] = temp[j];
            swaps++;
            steps.add(new SortStep(arr, k, -1, true,
                String.format("Placing %d", temp[j]),
                comparisons, swaps));
            j++;
            k++;
        }
        
        // Mark merge complete
        steps.add(new SortStep(arr, left, right, false,
            String.format("Merged completed"),
            comparisons, swaps));
    }
}