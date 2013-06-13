package com.mes51.minecraft.mods.javelin.entities;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.mes51.minecraft.mods.javelin.Javelin;
import com.mes51.minecraft.mods.javelin.renderer.RenderJavelinMissile;
import com.mes51.minecraft.mods.javelin.util.Util;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentThorns;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet70GameEvent;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.List;

/**
 * Package: com.mes51.minecraft.mods.javelin.entities
 * Date: 2013/06/04
 * Time: 20:48
 */
public class EntityJavelinMissile extends Entity implements IProjectile, IEntityAdditionalSpawnData {
    private static final float EXPLOSION_RADIUS = 6.0F;
    private static final float INITIAL_VELOCITY = 0.375F;
    private static final float ASCENT_VELOCITY = 1.0F;
    private static final int BOOSTER_FIRING_TIME = 5;
    private static final int DECENT_START_TIME = 60;
    private static final double TARGET_TRACE_POWER = 0.7;
    private static final double TARGET_TRACE_MAX_VELOCITY = 2.0;

    private boolean inGround = false;
    private Entity shootingEntity;
    private int ticksInAir = 0;

    // velocity
    // 爆風などで変な方向に飛ばされないように独自管理にする
    private double vX = 0.0F;
    private double vY = 0.0F;
    private double vZ = 0.0F;

    // missile states
    // 爆発済み
    private boolean exploded = false;
    // ブースター点火
    private boolean fireBooster = false;
    // 下降開始
    private boolean moveToTarget = false;

    private Entity target = null;

    public static void register()
    {
        EntityRegistry.registerModEntity(
                EntityJavelinMissile.class,
                "JavelinMissile",
                EntityRegistry.findGlobalUniqueEntityId(),
                Javelin.instacne,
                256,
                1,
                false
        );
        RenderingRegistry.registerEntityRenderingHandler(EntityJavelinMissile.class, new RenderJavelinMissile());
    }

    public EntityJavelinMissile(World par1World)
    {
        super(par1World);
    }

    // from EntityArrow(World par1World, EntityLiving par2EntityLiving, Entity target)
    public EntityJavelinMissile(World par1World, EntityLiving par2EntityLiving, Entity target)
    {
        super(par1World);
        this.renderDistanceWeight = 10.0D;
        this.shootingEntity = par2EntityLiving;

        this.setSize(0.5F, 0.5F);
        this.setLocationAndAngles(par2EntityLiving.posX, par2EntityLiving.posY + (double)par2EntityLiving.getEyeHeight(), par2EntityLiving.posZ, par2EntityLiving.rotationYaw, par2EntityLiving.rotationPitch);
        this.posX -= (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
        this.posY -= 0.10000000149011612D;
        this.posZ -= (double)(MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.yOffset = 0.0F;
        this.vX = (double)(-MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI));
        this.vZ = (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI));
        this.vY = (double)(-MathHelper.sin(this.rotationPitch / 180.0F * (float)Math.PI));
        this.setThrowableHeading(this.vX, this.vY, this.vZ, INITIAL_VELOCITY, 1.0F);

        this.target = target;
    }

    protected void entityInit()
    {
        this.dataWatcher.addObject(16, Byte.valueOf((byte)0));
    }

    // par1: x方向
    // par3: y方向
    // par5: z方向
    // par7: speed
    // らしい
    public void setThrowableHeading(double par1, double par3, double par5, float par7, float par8)
    {
        // speedを合わせるため、normalizeする
        float f2 = MathHelper.sqrt_double(par1 * par1 + par3 * par3 + par5 * par5);
        par1 /= (double)f2;
        par3 /= (double)f2;
        par5 /= (double)f2;
        this.vX = par1 * par7;
        this.vY = par3 * par7;
        this.vZ = par5 * par7;

        this.setYawAndPitch(par1, par3, par5);
    }

    @SideOnly(Side.CLIENT)
    public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9)
    {
        this.setPosition(par1, par3, par5);
        this.setRotation(par7, par8);
    }

    public void onUpdate()
    {
        if (this.exploded)
        {
            this.setDead();
            return;
        }

        if (this.inGround)
        {
            this.explodeMissile();
        }
        else
        {
            this.ticksInAir++;
            Vec3 pos = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
            Vec3 lookAt = Vec3.createVectorHelper(this.posX + this.vX, this.posY + this.vY, this.posZ + this.vZ);
            Entity entity = Util.searchTarget(this, pos, lookAt);

            MovingObjectPosition movingobjectposition = null;
            if (entity != null)
            {
                movingobjectposition = new MovingObjectPosition(entity);
            }

            if (movingobjectposition != null)
            {
                this.explodeMissile();
                return;
            }

            if (this.fireBooster)
            {
                for (int i = 0; i < 4; ++i)
                {
                    this.worldObj.spawnParticle("crit", this.posX + this.vX * (double)i / 4.0, this.posY + this.vY * (double)i / 4.0, this.posZ + this.vZ * (double)i / 4.0, -this.vX, -this.vY + 0.2, -this.vZ);
                }
            }

            this.posX += this.vX;
            this.posY += this.vY;
            this.posZ += this.vZ;

            if (this.fireBooster)
            {
                if (this.moveToTarget)
                {
                    if (target != null)
                    {
                        double dX = (this.target.posX - this.posX);
                        double dZ = (this.target.posZ - this.posZ);
                        double length = Math.min(Math.sqrt(dX * dX + dZ * dZ) * TARGET_TRACE_POWER, TARGET_TRACE_MAX_VELOCITY);
                        double rad = Math.atan2(dZ, dX);
                        double cos = Math.cos(rad);
                        double sin = Math.sin(rad);
                        this.vX = cos * length;
                        this.vZ = sin * length;
                    }
                }
                else if (this.ticksInAir > DECENT_START_TIME)
                {
                    this.vY -= ASCENT_VELOCITY * 3.0F;
                    this.moveToTarget = true;
                }
            }
            else if (this.ticksInAir > BOOSTER_FIRING_TIME)
            {
                this.vY += ASCENT_VELOCITY;
                this.fireBooster = true;
            }

            double speed = Math.sqrt(this.vX * this.vX + this.vY * this.vY + this.vZ * this.vZ);
            this.setYawAndPitch(this.vX / speed, this.vY / speed, this.vZ / speed);
            this.setPosition(this.posX, this.posY, this.posZ);
            this.doBlockCollisions();
        }
    }

    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setByte("inGround", (byte)(this.inGround ? 1 : 0));
    }

    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        this.inGround = par1NBTTagCompound.getByte("inGround") == 1;
    }

    public void onCollideWithPlayer(EntityPlayer par1EntityPlayer) { }

    protected boolean canTriggerWalking()
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public float getShadowSize()
    {
        return 0.0F;
    }

    public boolean canAttackWithItem()
    {
        return false;
    }

    @Override
    public void writeSpawnData(ByteArrayDataOutput data) {
        data.writeInt(this.ticksInAir);
        data.writeDouble(this.vX);
        data.writeDouble(this.vY);
        data.writeDouble(this.vZ);
        data.writeBoolean(this.exploded);
        data.writeBoolean(this.fireBooster);
        data.writeBoolean(this.moveToTarget);
        if (target != null)
        {
            data.writeInt(target.entityId);
        }
        else
        {
            data.writeInt(0);
        }
        if (shootingEntity != null)
        {
            data.writeInt(shootingEntity.entityId);
        }
        else
        {
            data.writeInt(0);
        }
    }

    @Override
    public void readSpawnData(ByteArrayDataInput data) {
        this.ticksInAir = data.readInt();
        this.vX = data.readDouble();
        this.vY = data.readDouble();
        this.vZ = data.readDouble();
        this.exploded = data.readBoolean();
        this.fireBooster = data.readBoolean();
        this.moveToTarget = data.readBoolean();
        target = worldObj.getEntityByID(data.readInt());
        shootingEntity = worldObj.getEntityByID(data.readInt());
    }

    private void explodeMissile()
    {
        if (!exploded)
        {
            worldObj.createExplosion(this, this.posX, this.posY, this.posZ, EXPLOSION_RADIUS, false);
            this.setDead();
            exploded = true;
        }
    }

    // yawとpitchを求める
    // see: http://forums.bukkit.org/threads/how-do-i-get-yaw-and-pitch-from-a-vector.50317/
    private void setYawAndPitch(double xDir, double yDir, double zDir)
    {
        // 角度はdegreeで保持される模様
        this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(xDir, zDir) / Math.PI * 180.0);
        this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(yDir, Math.sqrt(xDir * xDir + zDir * zDir)) / Math.PI * 180.0);
    }
}
