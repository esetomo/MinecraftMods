package com.mes51.minecraft.mods.yukariblockreplica.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

/**
 * Package: com.mes51.minecraft.mods.yukariblockreplica.blocks
 * Date: 13/05/28
 * Time: 1:55
 */
public class BlockYukariReplica extends Block {
    public static final String BLOCK_NAME = "com.mes51.minecraft.mods.yukariblockreplica.blocks:yukariBlockReplica";

    private static BlockYukariReplica instance = new BlockYukariReplica(500);

    public static Block getInstance() {
        return instance;
    }

    public BlockYukariReplica(int blockId) {
        super(blockId, Material.rock);
        setStepSound(Block.soundStoneFootstep);
        setHardness(0.1F);
        setUnlocalizedName(BLOCK_NAME);
    }
}
