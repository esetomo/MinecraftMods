package com.mes51.minecraft.mods.yukariblockreplica;

import com.mes51.minecraft.mods.yukariblockreplica.blocks.BlockYukariReplica;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Package: com.mes51.minecraft.mods.yukariblockreplica
 * Date: 13/05/28
 * Time: 1:52
 */
@Mod(modid = "YukariBlockReplica", name = "YukariBlockReplica", version = "1.0.0")
@NetworkMod(clientSideRequired = true, serverSideRequired = true)
public class YukariBlockReplica {
    @Init
    public void load(FMLInitializationEvent event) {
        LanguageRegistry.addName(BlockYukariReplica.getInstance(), "Yukari Block Replica");
        GameRegistry.registerBlock(BlockYukariReplica.getInstance(), BlockYukariReplica.BLOCK_NAME);
        GameRegistry.addRecipe(
                new ItemStack(BlockYukariReplica.getInstance()),
                new Object[] {
                        "XXX", "XYX", "XXX", 'X', Item.coal, 'Y', Block.obsidian
                });
        GameRegistry.addSmelting(BlockYukariReplica.getInstance().blockID, new ItemStack(Item.diamond), 1);

        GameRegistry.addShapelessRecipe(new ItemStack(Block.obsidian, 1), new ItemStack(Block.glass, 1), new ItemStack(Item.coal, 1, 1));
    }
}
