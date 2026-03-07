package com.visualizer.algorithm.model;

import java.util.List;

public class TreeStructure {
    private TreeNode root;
    private String message;
    private int height;
    private int nodeCount;
    private List<Integer> traversalPath;
    private String traversalType;

    public TreeStructure(TreeNode root, String message, int height, int nodeCount) {
        this.root = root;
        this.message = message;
        this.height = height;
        this.nodeCount = nodeCount;
    }

    // Getters and setters
    public TreeNode getRoot() { return root; }
    public void setRoot(TreeNode root) { this.root = root; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }

    public int getNodeCount() { return nodeCount; }
    public void setNodeCount(int nodeCount) { this.nodeCount = nodeCount; }

    public List<Integer> getTraversalPath() { return traversalPath; }
    public void setTraversalPath(List<Integer> traversalPath) { this.traversalPath = traversalPath; }

    public String getTraversalType() { return traversalType; }
    public void setTraversalType(String traversalType) { this.traversalType = traversalType; }
}