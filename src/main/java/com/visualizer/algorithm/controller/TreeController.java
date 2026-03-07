package com.visualizer.algorithm.controller;

import com.visualizer.algorithm.model.TreeStructure;
import com.visualizer.algorithm.service.TreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tree")
@CrossOrigin(origins = "*")
public class TreeController {

    @Autowired
    private TreeService treeService;

    @PostMapping("/bst/insert/{value}")
    public TreeStructure insertBST(@PathVariable int value) {
        return treeService.insertBST(value);
    }

    @PostMapping("/bst/search/{value}")
    public TreeStructure searchBST(@PathVariable int value) {
        return treeService.searchBST(value);
    }

    @PostMapping("/bst/delete/{value}")
    public TreeStructure deleteBST(@PathVariable int value) {
        return treeService.deleteBST(value);
    }

    @GetMapping("/traversal/inorder")
    public TreeStructure inorder() {
        return treeService.inorder();
    }

    @GetMapping("/traversal/preorder")
    public TreeStructure preorder() {
        return treeService.preorder();
    }

    @GetMapping("/traversal/postorder")
    public TreeStructure postorder() {
        return treeService.postorder();
    }

    @GetMapping("/traversal/bfs")
    public TreeStructure bfs() {
        return treeService.bfs();
    }

    @PostMapping("/avl/insert/{value}")
    public TreeStructure insertAVL(@PathVariable int value) {
        return treeService.insertAVL(value);
    }

    @PostMapping("/heap/insert/{value}")
    public TreeStructure insertHeap(@PathVariable int value) {
        return treeService.insertHeap(value);
    }

    @PostMapping("/reset")
    public TreeStructure reset() {
        return treeService.reset();
    }
}