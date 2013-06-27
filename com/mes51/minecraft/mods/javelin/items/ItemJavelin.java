package com.mes51.minecraft.mods.javelin.items;

import com.mes51.minecraft.mods.javelin.entities.EntityJavelinMissile;
import com.mes51.minecraft.mods.javelin.entities.filter.FilterJavelinTarget;
import com.mes51.minecraft.mods.javelin.util.Util;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import ic2.api.item.Items;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;

import java.util.List;

/**
 * Package: com.mes51.minecraft.mods.javelin.items
 * Date: 2013/06/03
 * Time: 23:59
 */
public class ItemJavelin extends Item
{
    public static final String ITEM_JAVELIN_NAME = "com.mes51.minecraft.mods.javelin.items:javelin";

    private static final Item instance = new ItemJavelin(4000 - 256);

    public static void register() {
        GameRegistry.registerItem(instance, ITEM_JAVELIN_NAME);
        LanguageRegistry.addName(instance, "Javelin");
    }

    public static void registerRecipe()
    {
        if (Loader.isModLoaded("IC2"))
        {
            GameRegistry.addRecipe(
                    new ItemStack(instance, 1),
                    new Object[] {
                            "XXX",
                            " YZ",
                            "  Z",
                            'X', new ItemStack(Item.ingotIron, 1),
                            'Y', Items.getItem("advancedCircuit"),
                            'Z', Items.getItem("carbonPlate")
                    }
            );
        }
        else
        {
            GameRegistry.addRecipe(
                    new ItemStack(instance, 1),
                    "XXX",
                    "XYX",
                    "  X",
                    'X', new ItemStack(Item.ingotIron, 1),
                    'Y', new ItemStack(Item.redstone, 1)
            );
        }
    }

    public ItemJavelin(int par1)
    {
        super(par1);
        this.maxStackSize = 1;
        this.setCreativeTab(CreativeTabs.tabCombat);
        this.setUnlocalizedName(ITEM_JAVELIN_NAME);
        setMaxDamage(1);
    }

    public void onPlayerStoppedUsing(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer, int par4)
    {
        ArrowLooseEvent event = new ArrowLooseEvent(par3EntityPlayer, par1ItemStack, this.getMaxItemUseDuration(par1ItemStack));
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled())
        {
            return;
        }

        boolean isCreative = par3EntityPlayer.capabilities.isCreativeMode;

        if (isCreative || par1ItemStack.getItemDamage() < par1ItemStack.getMaxDamage())
        {
            Entity target = Util.searchTarget(par3EntityPlayer);
            // targetがいない場合は何もしない
            if (target == null)
            {
                return;
            }

            Entity entity = new EntityJavelinMissile(par2World, par3EntityPlayer, target);

            par2World.playSoundAtEntity(par3EntityPlayer, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + 0.5F);

            if (!isCreative)
            {
                par1ItemStack.damageItem(par1ItemStack.getMaxDamage(), par3EntityPlayer);
            }

            if (!par2World.isRemote)
            {
                par2World.spawnEntityInWorld(entity);
            }
        }
    }

    public ItemStack onEaten(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        if (par1ItemStack.getItemDamage() > 0 && par3EntityPlayer.inventory.hasItem(ItemJavelinMissile.instance.itemID))
        {
            par3EntityPlayer.inventory.consumeInventoryItem(ItemJavelinMissile.instance.itemID);
            par1ItemStack.setItemDamage(0);
        }
        return par1ItemStack;
    }

    public int getMaxItemUseDuration(ItemStack par1ItemStack)
    {
        if (par1ItemStack.getItemDamage() < par1ItemStack.getMaxDamage())
        {
            return 72000;
        }
        else
        {
            return 80;
        }
    }

    public EnumAction getItemUseAction(ItemStack par1ItemStack)
    {
        if (par1ItemStack.getItemDamage() < par1ItemStack.getMaxDamage())
        {
            return EnumAction.bow;
        }
        else
        {
            return EnumAction.block;
        }
    }

    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        ArrowNockEvent event = new ArrowNockEvent(par3EntityPlayer, par1ItemStack);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled())
        {
            return event.result;
        }

        par3EntityPlayer.setItemInUse(par1ItemStack, this.getMaxItemUseDuration(par1ItemStack));
        return par1ItemStack;
    }

    public int getItemEnchantability()
    {
        return 0;
    }
}
