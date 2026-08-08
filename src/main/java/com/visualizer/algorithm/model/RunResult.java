package com.visualizer.algorithm.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "run_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RunResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "array_id", nullable = false)
    private SavedArray savedArray;

    @Column(nullable = false)
    private String algorithm;

    @Column(nullable = false)
    private int comparisons;

    @Column(nullable = false)
    private int swaps;

    @Column(name = "array_size", nullable = false)
    private int arraySize;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
