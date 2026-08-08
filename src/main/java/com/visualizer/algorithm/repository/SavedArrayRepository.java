package com.visualizer.algorithm.repository;

import com.visualizer.algorithm.model.SavedArray;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavedArrayRepository extends JpaRepository<SavedArray, Long> {
    List<SavedArray> findByUserId(Long userId);
}
