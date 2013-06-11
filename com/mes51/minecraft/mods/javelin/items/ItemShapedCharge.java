package com.mes51.minecraft.mods.javelin.items;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import ic2.api.recipe.Recipes;
import net.minecraft.item.ItemStack;

/**
 * Package: com.mes51.minecraft.mods.javelin.items
 * Date: 2013/06/06
 * Time: 14:58
 */
public class ItemShapedCharge extends Item {
    public static final String ITEM_SHAPED_CHAGE_NAME = "com.mes51.minecraft.mods.javelin.items:shapedCharge";
    public static final Item instance = new ItemShapedCharge(4001 - 256);

    public static void register()
    {
        GameRegistry.registerItem(instance, ITEM_SHAPED_CHAGE_NAME);
        LanguageRegistry.addName(instance, "Shaped Charge");
    }

    public static void registerRecpie()
    {
        if (Loader.isModLoaded("IC2"))
        {
            Recipes.compressor.addRecipe(new ItemStack(Item.gunpowder, 9), new ItemStack(instance, 1));
        }
        else
        {
            GameRegistry.addRecipe(
                    new ItemStack(instance, 1),
                    new Object[] {
                            "XXX",
                            "XXX",
                            "XXX",
                            'X', new ItemStack(Item.gunpowder, 1)
                    }
            );
        }
    }

    public ItemShapedCharge(int par1) {
        super(par1);
        setCreativeTab(CreativeTabs.tabMaterials);
        setUnlocalizedName(ITEM_SHAPED_CHAGE_NAME);
    }
}
