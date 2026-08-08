package com.visualizer.algorithm.service;

import com.visualizer.algorithm.dto.LeaderboardEntry;
import com.visualizer.algorithm.dto.RunResultRequest;
import com.visualizer.algorithm.dto.RunResultResponse;
import com.visualizer.algorithm.model.RunResult;
import com.visualizer.algorithm.model.SavedArray;
import com.visualizer.algorithm.model.User;
import com.visualizer.algorithm.repository.RunResultRepository;
import com.visualizer.algorithm.repository.SavedArrayRepository;
import com.visualizer.algorithm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RunResultService {

    @Autowired
    private RunResultRepository runResultRepository;

    @Autowired
    private SavedArrayRepository savedArrayRepository;

    @Autowired
    private UserRepository userRepository;

    private static final List<String> ALLOWED_ALGORITHMS = List.of("bubble", "merge", "quick");
    private static final int LEADERBOARD_LIMIT = 10;

    public RunResultResponse logRun(String email, RunResultRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        SavedArray array = savedArrayRepository.findById(request.getArrayId())
                .orElseThrow(() -> new IllegalArgumentException("Referenced array does not exist"));

        // Derive the real array size from the stored array — never trust a client-sent size.
        int arraySize = array.getValuesCsv().split(",").length;

        validateAlgorithm(request.getAlgorithm());
        validateCounts(request.getComparisons(), request.getSwaps(), arraySize);

        RunResult run = new RunResult();
        run.setUser(user);
        run.setSavedArray(array);
        run.setAlgorithm(request.getAlgorithm());
        run.setComparisons(request.getComparisons());
        run.setSwaps(request.getSwaps());
        run.setArraySize(arraySize);

        runResultRepository.save(run);

        return new RunResultResponse(run.getId(), run.getAlgorithm(), run.getComparisons(),
                run.getSwaps(), run.getArraySize(), run.getCreatedAt());
    }

    public List<LeaderboardEntry> getLeaderboard(String algorithm, int size) {
        validateAlgorithm(algorithm);

        return runResultRepository.findByAlgorithmAndArraySizeOrderByComparisonsAsc(algorithm, size).stream()
                .limit(LEADERBOARD_LIMIT)
                .map(r -> new LeaderboardEntry(
                        r.getUser().getEmail(),
                        r.getAlgorithm(),
                        r.getComparisons(),
                        r.getSwaps(),
                        r.getArraySize(),
                        r.getCreatedAt()))
                .collect(Collectors.toList());
    }

    private void validateAlgorithm(String algorithm) {
        if (algorithm == null || !ALLOWED_ALGORITHMS.contains(algorithm.toLowerCase())) {
            throw new IllegalArgumentException("Algorithm must be one of: " + ALLOWED_ALGORITHMS);
        }
    }

    private void validateCounts(int comparisons, int swaps, int arraySize) {
        if (arraySize < 2) {
            throw new IllegalArgumentException("Array size must be at least 2 to sort");
        }
        if (comparisons < 0 || swaps < 0) {
            throw new IllegalArgumentException("Comparisons and swaps cannot be negative");
        }

        // Upper bound: the maximum number of pairwise comparisons or swaps possible
        // for any comparison-based sort on n elements is n*(n-1)/2.
        int maxPossible = arraySize * (arraySize - 1) / 2;

        if (comparisons > maxPossible) {
            throw new IllegalArgumentException(
                    String.format("Comparisons (%d) exceed the maximum possible (%d) for array size %d",
                            comparisons, maxPossible, arraySize));
        }
        if (swaps > maxPossible) {
            throw new IllegalArgumentException(
                    String.format("Swaps (%d) exceed the maximum possible (%d) for array size %d",
                            swaps, maxPossible, arraySize));
        }
    }
}
