package com.mes51.minecraft.mods.javelin.items;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import ic2.api.item.Items;

/**
 * Package: com.mes51.minecraft.mods.javelin.items
 * Date: 2013/06/06
 * Time: 14:43
 */
public class ItemJavelinMissile extends Item {
    public static final String ITEM_JAVELIN_MISSILE_NAME = "com.mes51.minecraft.mods.javelin.items:javelinMissile";
    public static final Item instance = new ItemJavelinMissile(4002 - 256);

    public static void register()
    {
        GameRegistry.registerItem(instance, ITEM_JAVELIN_MISSILE_NAME);
        LanguageRegistry.addName(instance, "JavelinMissile");
    }

    public static void registerRecipe()
    {
        if (Loader.isModLoaded("IC2"))
        {
            GameRegistry.addRecipe(
                    new ItemStack(instance, 1),
                    new Object[] {
                            "X",
                            "Y",
                            "Z",
                            'X', new ItemStack(Item.ingotIron),
                            'Y', new ItemStack(ItemShapedCharge.instance, 1),
                            'Z', Items.getItem("electronicCircuit")
                    }
            );
        }
        else
        {
            GameRegistry.addRecipe(
                    new ItemStack(instance, 1),
                    new Object[] {
                            " X ",
                            " Y ",
                            "ZWZ",
                            'X', new ItemStack(Item.ingotIron),
                            'Y', new ItemStack(ItemShapedCharge.instance, 1),
                            'Z', new ItemStack(Item.redstone, 1),
                            'W', new ItemStack(Item.lightStoneDust, 1)
                    }
            );
        }
    }

    public ItemJavelinMissile(int par1) {
        super(par1);
        setCreativeTab(CreativeTabs.tabCombat);
        setUnlocalizedName(ITEM_JAVELIN_MISSILE_NAME);
    }
}
