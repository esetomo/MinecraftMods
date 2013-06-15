package com.mes51.minecraft.mods.lightglass.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Random;

/**
 * Package: com.mes51.minecraft.mods.lightglass.blocks
 * Date: 13/06/15
 * Time: 18:58
 */
public class BlockLightGlass extends Block {
    public static final String BLOCK_NAME = "com.mes51.minecraft.mods.lightglass.blocks:lightGlass";

    private static Block instance = null;

    static
    {
        instance = new BlockLightGlass(502);
    }

    public static void register()
    {
        GameRegistry.registerBlock(instance, BLOCK_NAME);
        LanguageRegistry.addName(instance, "Light Glass");
        GameRegistry.addShapelessRecipe(
                new ItemStack(instance, 1),
                new ItemStack(Block.glass, 1),
                new ItemStack(Item.lightStoneDust, 1),
                new ItemStack(Item.lightStoneDust, 1),
                new ItemStack(Item.lightStoneDust, 1),
                new ItemStack(Item.lightStoneDust, 1)
        );
    }

    public BlockLightGlass(int blockId)
    {
        super(blockId, Material.glass);
        setUnlocalizedName(BLOCK_NAME);
        setHardness(0.3F);
        setStepSound(soundGlassFootstep);
        setLightValue(1.0F);
        setCreativeTab(CreativeTabs.tabBlock);
    }

    public int quantityDropped(Random par1Random)
    {
        return 0;
    }

    public int getRenderBlockPass()
    {
        return 0;
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean renderAsNormalBlock()
    {
        return false;
    }

    protected boolean canSilkHarvest()
    {
        return true;
    }
}
