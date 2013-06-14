package com.mes51.minecraft.mods.javelin.items;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import ic2.api.recipe.Recipes;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import ic2.api.item.Items;

/**
 * Package: com.mes51.minecraft.mods.javelin.items
 * Date: 13/06/15
 * Time: 0:11
 */
public class ItemMissileOuter extends Item {
    public static final String ITEM_SHAPED_CHAGE_NAME = "com.mes51.minecraft.mods.javelin.items:missileOuter";
    public static final Item instance = new ItemMissileOuter(4003 - 256);

    public static void register()
    {
        GameRegistry.registerItem(instance, ITEM_SHAPED_CHAGE_NAME);
        LanguageRegistry.addName(instance, "Missile Outer");
    }

    public static void registerRecpie()
    {
        ItemStack liner = null;
        if (Loader.isModLoaded("IC2"))
        {
            liner = Items.getItem("copperIngot");
        }
        else
        {
            liner = new ItemStack(Block.glass, 1);
        }
        GameRegistry.addRecipe(
                new ItemStack(instance, 5),
                new Object[] {
                        " X ",
                        "XYX",
                        "XYX",
                        'X', new ItemStack(Item.ingotIron, 1),
                        'Y', liner
                }
        );
    }

    public ItemMissileOuter(int par1) {
        super(par1);
        setCreativeTab(CreativeTabs.tabMaterials);
        setUnlocalizedName(ITEM_SHAPED_CHAGE_NAME);
    }
}
