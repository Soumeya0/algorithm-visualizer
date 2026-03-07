package com.visualizer.algorithm.service;

import com.visualizer.algorithm.model.TreeNode;
import com.visualizer.algorithm.model.TreeStructure;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TreeService {
    
    private TreeNode root;
    private List<Integer> traversalPath;
    
    public TreeService() {
        this.root = null;
        this.traversalPath = new ArrayList<>();
    }
    
    // BST Insertion
    public TreeStructure insertBST(int value) {
        traversalPath.clear();
        root = insertBSTRec(root, value, traversalPath);
        return getTreeStructure("Inserted " + value + " into BST");
    }
    
    private TreeNode insertBSTRec(TreeNode node, int value, List<Integer> path) {
        if (node == null) {
            return new TreeNode(value);
        }
        
        path.add(node.getValue());
        
        if (value < node.getValue()) {
            node.setLeft(insertBSTRec(node.getLeft(), value, path));
        } else if (value > node.getValue()) {
            node.setRight(insertBSTRec(node.getRight(), value, path));
        }
        
        return node;
    }
    
    // BST Search
    public TreeStructure searchBST(int value) {
        traversalPath.clear();
        boolean found = searchBSTRec(root, value, traversalPath);
        String message = found ? "Found " + value : value + " not found";
        TreeStructure tree = getTreeStructure(message);
        tree.setTraversalPath(traversalPath);
        tree.setTraversalType("Search");
        return tree;
    }
    
    private boolean searchBSTRec(TreeNode node, int value, List<Integer> path) {
        if (node == null) return false;
        
        path.add(node.getValue());
        
        if (value == node.getValue()) {
            node.setHighlighted(true);
            return true;
        }
        
        if (value < node.getValue()) {
            return searchBSTRec(node.getLeft(), value, path);
        } else {
            return searchBSTRec(node.getRight(), value, path);
        }
    }
    
    // BST Deletion
    public TreeStructure deleteBST(int value) {
        traversalPath.clear();
        root = deleteBSTRec(root, value, traversalPath);
        return getTreeStructure("Deleted " + value + " from BST");
    }
    
    private TreeNode deleteBSTRec(TreeNode node, int value, List<Integer> path) {
        if (node == null) return null;
        
        path.add(node.getValue());
        
        if (value < node.getValue()) {
            node.setLeft(deleteBSTRec(node.getLeft(), value, path));
        } else if (value > node.getValue()) {
            node.setRight(deleteBSTRec(node.getRight(), value, path));
        } else {
            // Node with only one child or no child
            if (node.getLeft() == null) return node.getRight();
            if (node.getRight() == null) return node.getLeft();
            
            // Node with two children: get inorder successor
            TreeNode successor = findMin(node.getRight());
            node.setValue(successor.getValue());
            node.setRight(deleteBSTRec(node.getRight(), successor.getValue(), new ArrayList<>()));
        }
        
        return node;
    }
    
    private TreeNode findMin(TreeNode node) {
        while (node.getLeft() != null) {
            node = node.getLeft();
        }
        return node;
    }
    
    // Traversals
    public TreeStructure inorder() {
        traversalPath.clear();
        inorderRec(root, traversalPath);
        TreeStructure tree = getTreeStructure("In-order Traversal");
        tree.setTraversalPath(traversalPath);
        tree.setTraversalType("In-order");
        return tree;
    }
    
    private void inorderRec(TreeNode node, List<Integer> path) {
        if (node != null) {
            inorderRec(node.getLeft(), path);
            path.add(node.getValue());
            inorderRec(node.getRight(), path);
        }
    }
    
    public TreeStructure preorder() {
        traversalPath.clear();
        preorderRec(root, traversalPath);
        TreeStructure tree = getTreeStructure("Pre-order Traversal");
        tree.setTraversalPath(traversalPath);
        tree.setTraversalType("Pre-order");
        return tree;
    }
    
    private void preorderRec(TreeNode node, List<Integer> path) {
        if (node != null) {
            path.add(node.getValue());
            preorderRec(node.getLeft(), path);
            preorderRec(node.getRight(), path);
        }
    }
    
    public TreeStructure postorder() {
        traversalPath.clear();
        postorderRec(root, traversalPath);
        TreeStructure tree = getTreeStructure("Post-order Traversal");
        tree.setTraversalPath(traversalPath);
        tree.setTraversalType("Post-order");
        return tree;
    }
    
    private void postorderRec(TreeNode node, List<Integer> path) {
        if (node != null) {
            postorderRec(node.getLeft(), path);
            postorderRec(node.getRight(), path);
            path.add(node.getValue());
        }
    }
    
    // BFS / Level Order
    public TreeStructure bfs() {
        traversalPath.clear();
        if (root == null) return getTreeStructure("Tree is empty");
        
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            traversalPath.add(node.getValue());
            
            if (node.getLeft() != null) queue.add(node.getLeft());
            if (node.getRight() != null) queue.add(node.getRight());
        }
        
        TreeStructure tree = getTreeStructure("BFS / Level Order Traversal");
        tree.setTraversalPath(traversalPath);
        tree.setTraversalType("BFS");
        return tree;
    }
    
    // AVL Insertion
    public TreeStructure insertAVL(int value) {
        traversalPath.clear();
        root = insertAVLRec(root, value, traversalPath);
        return getTreeStructure("Inserted " + value + " into AVL tree with rotations");
    }
    
    private TreeNode insertAVLRec(TreeNode node, int value, List<Integer> path) {
        if (node == null) return new TreeNode(value);
        
        path.add(node.getValue());
        
        if (value < node.getValue()) {
            node.setLeft(insertAVLRec(node.getLeft(), value, path));
        } else if (value > node.getValue()) {
            node.setRight(insertAVLRec(node.getRight(), value, path));
        } else {
            return node; // Duplicate values not allowed
        }
        
        // Update height
        node.setHeight(1 + Math.max(getHeight(node.getLeft()), getHeight(node.getRight())));
        
        // Get balance factor
        int balance = getBalance(node);
        
        // Left Left Case
        if (balance > 1 && value < node.getLeft().getValue()) {
            return rotateRight(node);
        }
        
        // Right Right Case
        if (balance < -1 && value > node.getRight().getValue()) {
            return rotateLeft(node);
        }
        
        // Left Right Case
        if (balance > 1 && value > node.getLeft().getValue()) {
            node.setLeft(rotateLeft(node.getLeft()));
            return rotateRight(node);
        }
        
        // Right Left Case
        if (balance < -1 && value < node.getRight().getValue()) {
            node.setRight(rotateRight(node.getRight()));
            return rotateLeft(node);
        }
        
        return node;
    }
    
    private int getHeight(TreeNode node) {
        return node == null ? 0 : node.getHeight();
    }
    
    private int getBalance(TreeNode node) {
        return node == null ? 0 : getHeight(node.getLeft()) - getHeight(node.getRight());
    }
    
    private TreeNode rotateRight(TreeNode y) {
        TreeNode x = y.getLeft();
        TreeNode T2 = x.getRight();
        
        x.setRight(y);
        y.setLeft(T2);
        
        y.setHeight(1 + Math.max(getHeight(y.getLeft()), getHeight(y.getRight())));
        x.setHeight(1 + Math.max(getHeight(x.getLeft()), getHeight(x.getRight())));
        
        return x;
    }
    
    private TreeNode rotateLeft(TreeNode x) {
        TreeNode y = x.getRight();
        TreeNode T2 = y.getLeft();
        
        y.setLeft(x);
        x.setRight(T2);
        
        x.setHeight(1 + Math.max(getHeight(x.getLeft()), getHeight(x.getRight())));
        y.setHeight(1 + Math.max(getHeight(y.getLeft()), getHeight(y.getRight())));
        
        return y;
    }
    
    // Heap Insert (Max Heap)
    public TreeStructure insertHeap(int value) {
        traversalPath.clear();
        if (root == null) {
            root = new TreeNode(value);
            return getTreeStructure("Inserted " + value + " as root of heap");
        }
        
        // Simple implementation - insert and bubble up
        root = insertHeapRec(root, value);
        return getTreeStructure("Inserted " + value + " into heap");
    }
    
    private TreeNode insertHeapRec(TreeNode node, int value) {
        if (node == null) return new TreeNode(value);
        
        // For max heap, ensure parent is larger
        if (value > node.getValue()) {
            int temp = node.getValue();
            node.setValue(value);
            value = temp;
        }
        
        // Insert into left or right (simple approach for visualization)
        if (node.getLeft() == null) {
            node.setLeft(new TreeNode(value));
        } else if (node.getRight() == null) {
            node.setRight(new TreeNode(value));
        } else {
            // Recursively insert into left or right (simplified)
            if (getHeight(node.getLeft()) <= getHeight(node.getRight())) {
                node.setLeft(insertHeapRec(node.getLeft(), value));
            } else {
                node.setRight(insertHeapRec(node.getRight(), value));
            }
        }
        
        return node;
    }
    
    // Reset tree
    public TreeStructure reset() {
        root = null;
        traversalPath.clear();
        return getTreeStructure("Tree reset");
    }
    
    // Get tree height
    private int calculateHeight(TreeNode node) {
        if (node == null) return 0;
        return 1 + Math.max(calculateHeight(node.getLeft()), calculateHeight(node.getRight()));
    }
    
    // Count nodes
    private int countNodes(TreeNode node) {
        if (node == null) return 0;
        return 1 + countNodes(node.getLeft()) + countNodes(node.getRight());
    }
    
    // Helper to create tree structure response
    private TreeStructure getTreeStructure(String message) {
        int height = calculateHeight(root);
        int nodeCount = countNodes(root);
        TreeStructure tree = new TreeStructure(root, message, height, nodeCount);
        return tree;
    }
}