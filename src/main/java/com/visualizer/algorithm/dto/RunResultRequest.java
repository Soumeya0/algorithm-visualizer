package com.visualizer.algorithm.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RunResultRequest {
    private Long arrayId;
    private String algorithm;
    private int comparisons;
    private int swaps;
}
