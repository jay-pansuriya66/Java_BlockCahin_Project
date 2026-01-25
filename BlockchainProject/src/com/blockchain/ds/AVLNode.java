package com.blockchain.ds;

import com.blockchain.model.Block;

public class AVLNode {
    public Block block;
    public AVLNode left;
    public AVLNode right;
    public int height;

    public AVLNode(Block block) {
        this.block = block;
        this.height = 1;
    }
}
