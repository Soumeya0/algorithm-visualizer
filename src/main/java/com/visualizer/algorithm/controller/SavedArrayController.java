package com.visualizer.algorithm.controller;

import com.visualizer.algorithm.dto.SavedArrayRequest;
import com.visualizer.algorithm.dto.SavedArrayResponse;
import com.visualizer.algorithm.service.ArrayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/arrays")
@CrossOrigin(origins = "*")
public class SavedArrayController {

    @Autowired
    private ArrayService arrayService;

    @GetMapping
    public ResponseEntity<List<SavedArrayResponse>> getMyArrays(Authentication authentication) {
        return ResponseEntity.ok(arrayService.getUserArrays(authentication.getName()));
    }

    @PostMapping
    public ResponseEntity<?> saveArray(@RequestBody SavedArrayRequest request, Authentication authentication) {
        try {
            SavedArrayResponse response = arrayService.saveArray(authentication.getName(), request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
