package com.visualizer.algorithm.repository;

import com.visualizer.algorithm.model.RunResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RunResultRepository extends JpaRepository<RunResult, Long> {
    List<RunResult> findByUserId(Long userId);
    List<RunResult> findBySavedArrayId(Long savedArrayId);
    List<RunResult> findByAlgorithmAndArraySizeOrderByComparisonsAsc(String algorithm, int arraySize);
}
