package com.blockchain.ds;

import com.blockchain.model.Block;
import java.util.ArrayList;
import java.util.List;

public class AVLTree {
    public AVLNode root;

    // Get height of the node
    int height(AVLNode N) {
        if (N == null)
            return 0;
        return N.height;
    }

    // Get max of two integers
    int max(int a, int b) {
        return (a > b) ? a : b;
    }

    // Right rotate subtree rooted with y
    AVLNode rightRotate(AVLNode y) {
        if (y == null || y.left == null)
            return y;

        AVLNode x = y.left;
        AVLNode T2 = x.right;

        // Perform rotation
        x.right = y;
        y.left = T2;

        // Update heights
        y.height = max(height(y.left), height(y.right)) + 1;
        x.height = max(height(x.left), height(x.right)) + 1;

        // Return new root
        return x;
    }

    // Left rotate subtree rooted with x
    AVLNode leftRotate(AVLNode x) {
        if (x == null || x.right == null)
            return x;

        AVLNode y = x.right;
        AVLNode T2 = y.left;

        // Perform rotation
        y.left = x;
        x.right = T2;

        // Update heights
        x.height = max(height(x.left), height(x.right)) + 1;
        y.height = max(height(y.left), height(y.right)) + 1;

        // Return new root
        return y;
    }

    // Get Balance factor of node N
    int getBalance(AVLNode N) {
        if (N == null)
            return 0;
        return height(N.left) - height(N.right);
    }

    // Insert a block into the AVL tree
    public void insert(Block block) {
        root = insertRec(root, block);
    }

    private AVLNode insertRec(AVLNode node, Block block) {
        /* 1. Perform the normal BST insertion */
        if (node == null)
            return new AVLNode(block);

        // Key is block.index
        if (block.getIndex() < node.block.getIndex())
            node.left = insertRec(node.left, block);
        else if (block.getIndex() > node.block.getIndex())
            node.right = insertRec(node.right, block);
        else // Duplicate keys not allowed
            return node;

        /* 2. Update height of this ancestor node */
        node.height = 1 + max(height(node.left), height(node.right));

        /* 3. Get the balance factor of this ancestor node */
        int balance = getBalance(node);

        // If this node becomes unbalanced, then there are 4 cases

        // Left Left Case
        if (balance > 1 && block.getIndex() < node.left.block.getIndex())
            return rightRotate(node);

        // Right Right Case
        if (balance < -1 && block.getIndex() > node.right.block.getIndex())
            return leftRotate(node);

        // Left Right Case
        if (balance > 1 && block.getIndex() > node.left.block.getIndex()) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // Right Left Case
        if (balance < -1 && block.getIndex() < node.right.block.getIndex()) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        /* return the (unchanged) node pointer */
        return node;
    }

    // Method to update/replace a node (for tampering)
    // We will cheat a bit and assume we can find it by index and swap the block
    public boolean updateBlock(int index, Block newBlock) {
        AVLNode node = findNode(root, index);
        if (node != null) {
            node.block = newBlock;
            return true;
        }
        return false;
    }

    public AVLNode findNode(AVLNode node, int index) {
        if (node == null)
            return null;
        if (node.block.getIndex() == index)
            return node;

        if (index < node.block.getIndex())
            return findNode(node.left, index);
        else
            return findNode(node.right, index);
    }

    // In-order traversal to print the tree
    public void inOrder() {
        inOrder(System.out::println);
    }

    public void inOrder(java.util.function.Consumer<String> logger) {
        inOrderRec(root, logger);
        if (logger != null)
            logger.accept(""); // Newline
    }

    private void inOrderRec(AVLNode node, java.util.function.Consumer<String> logger) {
        if (node != null) {
            inOrderRec(node.left, logger);
            if (logger != null)
                logger.accept(node.block.toString());
            inOrderRec(node.right, logger);
        }
    }

    // Get all blocks in order (for validation)
    public List<Block> getAllBlocks() {
        List<Block> blocks = new ArrayList<>();
        collectBlocks(root, blocks);
        return blocks;
    }

    private void collectBlocks(AVLNode node, List<Block> list) {
        if (node != null) {
            collectBlocks(node.left, list);
            list.add(node.block);
            collectBlocks(node.right, list);
        }
    }
}
