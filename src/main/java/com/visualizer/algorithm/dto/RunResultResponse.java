package com.visualizer.algorithm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class RunResultResponse {
    private Long id;
    private String algorithm;
    private int comparisons;
    private int swaps;
    private int arraySize;
    private LocalDateTime createdAt;
}
