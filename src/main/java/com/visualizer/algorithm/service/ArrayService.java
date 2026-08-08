package com.visualizer.algorithm.service;

import com.visualizer.algorithm.dto.SavedArrayRequest;
import com.visualizer.algorithm.dto.SavedArrayResponse;
import com.visualizer.algorithm.model.SavedArray;
import com.visualizer.algorithm.model.User;
import com.visualizer.algorithm.repository.SavedArrayRepository;
import com.visualizer.algorithm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArrayService {

    @Autowired
    private SavedArrayRepository savedArrayRepository;

    @Autowired
    private UserRepository userRepository;

    public List<SavedArrayResponse> getUserArrays(String email) {
        User user = getUserByEmail(email);

        return savedArrayRepository.findByUserId(user.getId()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public SavedArrayResponse saveArray(String email, SavedArrayRequest request) {
        if (request.getValues() == null || request.getValues().isEmpty()) {
            throw new IllegalArgumentException("Array values cannot be empty");
        }
        if (request.getLabel() == null || request.getLabel().isBlank()) {
            throw new IllegalArgumentException("Label is required");
        }

        User user = getUserByEmail(email);

        String csv = request.getValues().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        SavedArray array = new SavedArray();
        array.setUser(user);
        array.setLabel(request.getLabel());
        array.setValuesCsv(csv);

        savedArrayRepository.save(array);
        return toResponse(array);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private SavedArrayResponse toResponse(SavedArray array) {
        List<Integer> values = Arrays.stream(array.getValuesCsv().split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        return new SavedArrayResponse(array.getId(), array.getLabel(), values, array.getCreatedAt());
    }
}
