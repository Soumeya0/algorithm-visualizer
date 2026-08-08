package com.visualizer.algorithm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class SavedArrayResponse {
    private Long id;
    private String label;
    private List<Integer> values;
    private LocalDateTime createdAt;
}
