package com.visualizer.algorithm.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SavedArrayRequest {
    private String label;
    private List<Integer> values;
}
