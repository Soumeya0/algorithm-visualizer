package com.visualizer.algorithm.controller;

import com.visualizer.algorithm.model.SortStep;
import com.visualizer.algorithm.sorting.BubbleSort;
import com.visualizer.algorithm.sorting.MergeSort;
import com.visualizer.algorithm.sorting.QuickSort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sort")
@CrossOrigin(origins = "*")
public class SortController {

    @Autowired
    private BubbleSort bubbleSort;
    
    @Autowired
    private MergeSort mergeSort;
    
    @Autowired
    private QuickSort quickSort;

    @PostMapping("/bubble")
    public List<SortStep> visualizeBubbleSort(@RequestBody int[] array) {
        return bubbleSort.sort(array);
    }
    
    @PostMapping("/merge")
    public List<SortStep> visualizeMergeSort(@RequestBody int[] array) {
        return mergeSort.sort(array);
    }
    
    @PostMapping("/quick")
    public List<SortStep> visualizeQuickSort(@RequestBody int[] array) {
        return quickSort.sort(array);
    }

    @GetMapping("/test")
    public String test() {
        return "Algorithm Visualizer API is working!";
    }
}