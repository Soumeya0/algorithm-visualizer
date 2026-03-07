package com.visualizer.algorithm.model;

public class TreeNode {
    private int value;
    private TreeNode left;
    private TreeNode right;
    private boolean highlighted;
    private int height; // For AVL trees

    public TreeNode(int value) {
        this.value = value;
        this.left = null;
        this.right = null;
        this.highlighted = false;
        this.height = 1;
    }

    // Getters and setters
    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }

    public TreeNode getLeft() { return left; }
    public void setLeft(TreeNode left) { this.left = left; }

    public TreeNode getRight() { return right; }
    public void setRight(TreeNode right) { this.right = right; }

    public boolean isHighlighted() { return highlighted; }
    public void setHighlighted(boolean highlighted) { this.highlighted = highlighted; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }
}