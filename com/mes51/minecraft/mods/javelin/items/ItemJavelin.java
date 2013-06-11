package com.mes51.minecraft.mods.javelin.items;

import com.mes51.minecraft.mods.javelin.entities.EntityJavelinMissile;
import com.mes51.minecraft.mods.javelin.entities.filter.FilterJavelinTarget;
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

    private static final double TARGET_SEARCH_RANGE = 256.0;
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
    }
    /**
     * called when the player releases the use item button. Args: itemstack, world, entityplayer, itemInUseCount
     */
    public void onPlayerStoppedUsing(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer, int par4)
    {
        int j = this.getMaxItemUseDuration(par1ItemStack) - par4;

        ArrowLooseEvent event = new ArrowLooseEvent(par3EntityPlayer, par1ItemStack, j);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled())
        {
            return;
        }

        boolean flag = par3EntityPlayer.capabilities.isCreativeMode;

        if (flag || par3EntityPlayer.inventory.hasItem(ItemJavelinMissile.instance.itemID))
        {
            Entity target = this.searchTarget(par3EntityPlayer);
            // targetがいない場合は何もしない
            if (target == null)
            {
                return;
            }

            Entity entity = new EntityJavelinMissile(par2World, par3EntityPlayer, target);

            par1ItemStack.damageItem(1, par3EntityPlayer);
            par2World.playSoundAtEntity(par3EntityPlayer, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + 0.5F);

            if (!flag)
            {
                par3EntityPlayer.inventory.consumeInventoryItem(ItemJavelinMissile.instance.itemID);
            }

            if (!par2World.isRemote)
            {
                par2World.spawnEntityInWorld(entity);
            }
        }
    }

    public ItemStack onEaten(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        return par1ItemStack;
    }

    /**
     * How long it takes to use or consume an item
     */
    public int getMaxItemUseDuration(ItemStack par1ItemStack)
    {
        return Integer.MAX_VALUE;
    }

    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    public EnumAction getItemUseAction(ItemStack par1ItemStack)
    {
        return EnumAction.bow;
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        ArrowNockEvent event = new ArrowNockEvent(par3EntityPlayer, par1ItemStack);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled())
        {
            return event.result;
        }

        if (par3EntityPlayer.capabilities.isCreativeMode || par3EntityPlayer.inventory.hasItem(ItemJavelinMissile.instance.itemID))
        {
            par3EntityPlayer.setItemInUse(par1ItemStack, this.getMaxItemUseDuration(par1ItemStack));
        }

        return par1ItemStack;
    }

    /**
     * Return the enchantability factor of the item, most of the time is based on material.
     */
    public int getItemEnchantability()
    {
        return 0;
    }

    // tergetの検索
    // see: https://github.com/bbc-mc/LockOnMod/blob/master/bbc_mc/LockOn/LockOn.java
    private Entity searchTarget(EntityPlayer player)
    {
        Vec3 lookAt = player.getLookVec();
        lookAt = Vec3.createVectorHelper(
                lookAt.xCoord * TARGET_SEARCH_RANGE,
                lookAt.yCoord * TARGET_SEARCH_RANGE,
                lookAt.zCoord * TARGET_SEARCH_RANGE
        );
        Vec3 playerPos = this.getPlayerPosition(player);
        Vec3 limit = playerPos.addVector(lookAt.xCoord, lookAt.yCoord, lookAt.zCoord);
        MovingObjectPosition movingObjectPosition = player.worldObj.rayTraceBlocks_do_do(playerPos, limit, false, true);

        playerPos = this.getPlayerPosition(player);
        if (movingObjectPosition != null)
        {
            limit = Vec3.createVectorHelper(
                    movingObjectPosition.hitVec.xCoord,
                    movingObjectPosition.hitVec.yCoord,
                    movingObjectPosition.hitVec.zCoord
            );
        }
        else
        {
            limit = playerPos.addVector(lookAt.xCoord, lookAt.yCoord, lookAt.zCoord);
        }

        List entities = player.worldObj.getEntitiesWithinAABBExcludingEntity(
                player,
                player.boundingBox.expand(TARGET_SEARCH_RANGE, TARGET_SEARCH_RANGE, TARGET_SEARCH_RANGE),
                new FilterJavelinTarget()
        );
        Entity result = null;
        double d = Double.MAX_VALUE;
        for (int i = entities.size() - 1; i > -1; i--)
        {
            Entity entity = (Entity)entities.get(i);
            if (!entity.canBeCollidedWith())
            {
                continue;
            }

            float aabbSize = 0.3F;
            AxisAlignedBB aabb = entity.boundingBox.expand(aabbSize, aabbSize, aabbSize);
            MovingObjectPosition pos = aabb.calculateIntercept(playerPos, limit);
            if (pos == null)
            {
                continue;
            }

            double dist = playerPos.distanceTo(pos.hitVec);
            if (dist < d)
            {
                result = entity;
                d = dist;
            }
        }
        return result;
    }

    // serverとclientで視点補正がかかっていたりいなかったりするため、その差を吸収する
    // see: http://forum.minecraftuser.jp/viewtopic.php?f=21&t=7907#p63907
    private Vec3 getPlayerPosition(EntityPlayer player)
    {
        Vec3 pos = player.getPosition(1.0F);
        if (player.yOffset == 0.0)
        {
            pos.yCoord += player.getEyeHeight();
        }
        return pos;
    }
}
