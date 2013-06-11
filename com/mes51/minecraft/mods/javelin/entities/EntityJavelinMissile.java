package com.mes51.minecraft.mods.javelin.entities;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.mes51.minecraft.mods.javelin.Javelin;
import com.mes51.minecraft.mods.javelin.renderer.RenderJavelinMissile;
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
    private static final float INITIAL_VELOCITY = 0.25F;
    private static final float ASCENT_VELOCITY = 1.0F;
    private static final int BOOSTER_FIRING_TIME = 5;
    private static final int DECENT_START_TIME = 60;
    private static final double TARGET_TRACE_POWER = 0.7;
    private static final double TARGET_TRACE_MAX_VELOCITY = 2.0;

    private boolean inGround = false;

    /** The owner of this arrow. */
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
        this.setThrowableHeading(this.vX, this.vY, this.vZ, INITIAL_VELOCITY * 1.5F, 1.0F);

        this.target = target;
    }

    protected void entityInit()
    {
        this.dataWatcher.addObject(16, Byte.valueOf((byte)0));
    }

    /**
     * Similar to setArrowHeading, it's point the throwable entity to a x, y, z direction.
     */
    public void setThrowableHeading(double par1, double par3, double par5, float par7, float par8)
    {
        float f2 = MathHelper.sqrt_double(par1 * par1 + par3 * par3 + par5 * par5);
        par1 /= (double)f2;
        par3 /= (double)f2;
        par5 /= (double)f2;
        par1 += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double)par8;
        par3 += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double)par8;
        par5 += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double)par8;
        par1 *= (double)par7;
        par3 *= (double)par7;
        par5 *= (double)par7;
        this.vX = par1;
        this.vY = par3;
        this.vZ = par5;
        float f3 = MathHelper.sqrt_double(par1 * par1 + par5 * par5);
        this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(par1, par5) * 180.0D / Math.PI);
        this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(par3, (double)f3) * 180.0D / Math.PI);
    }

    @SideOnly(Side.CLIENT)

    /**
     * Sets the position and rotation. Only difference from the other one is no bounding on the rotation. Args: posX,
     * posY, posZ, yaw, pitch
     */
    public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9)
    {
        this.setPosition(par1, par3, par5);
        this.setRotation(par7, par8);
    }

    @SideOnly(Side.CLIENT)

    /**
     * Sets the velocity to the args. Args: x, y, z
     */
    public void setVelocity(double par1, double par3, double par5)
    {
        this.vX = par1;
        this.vY = par3;
        this.vZ = par5;

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F)
        {
            float f = MathHelper.sqrt_double(par1 * par1 + par5 * par5);
            this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(par1, par5) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(par3, (double)f) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch;
            this.prevRotationYaw = this.rotationYaw;
            this.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
        }
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        //super.onUpdate();
        if (this.exploded)
        {
            this.setDead();
            return;
        }

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F)
        {
            float f = MathHelper.sqrt_double(this.vX * this.vX + this.vZ * this.vZ);
            this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(this.vX, this.vZ) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(this.vY, (double)f) * 180.0D / Math.PI);
        }

        if (this.inGround)
        {
            this.explodeMissile();
        }
        else
        {
            ++this.ticksInAir;
            Vec3 vec3 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.posY, this.posZ);
            Vec3 vec31 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX + this.vX, this.posY + this.vY, this.posZ + this.vZ);
            MovingObjectPosition movingobjectposition = this.worldObj.rayTraceBlocks_do_do(vec3, vec31, false, true);
            vec3 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.posY, this.posZ);
            vec31 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX + this.vX, this.posY + this.vY, this.posZ + this.vZ);

            if (movingobjectposition != null)
            {
                vec31 = this.worldObj.getWorldVec3Pool().getVecFromPool(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord, movingobjectposition.hitVec.zCoord);
            }

            Entity entity = null;
            List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.addCoord(this.vX, this.vY, this.vZ).expand(1.0D, 1.0D, 1.0D));
            double d0 = 0.0D;
            int l;
            float f1;

            for (l = 0; l < list.size(); ++l)
            {
                Entity entity1 = (Entity)list.get(l);

                if (entity1.canBeCollidedWith() && (entity1 != this.shootingEntity || this.ticksInAir >= 5))
                {
                    f1 = 0.3F;
                    AxisAlignedBB axisalignedbb1 = entity1.boundingBox.expand((double)f1, (double)f1, (double)f1);
                    MovingObjectPosition movingobjectposition1 = axisalignedbb1.calculateIntercept(vec3, vec31);

                    if (movingobjectposition1 != null)
                    {
                        double d1 = vec3.distanceTo(movingobjectposition1.hitVec);

                        if (d1 < d0 || d0 == 0.0D)
                        {
                            entity = entity1;
                            d0 = d1;
                        }
                    }
                }
            }

            if (entity != null)
            {
                movingobjectposition = new MovingObjectPosition(entity);
            }

            float f2;
            float f3;

            if (movingobjectposition != null)
            {
                this.explodeMissile();
                return;
            }

            if (this.fireBooster)
            {
                for (l = 0; l < 4; ++l)
                {
                    this.worldObj.spawnParticle("crit", this.posX + this.vX * (double)l / 4.0D, this.posY + this.vY * (double)l / 4.0D, this.posZ + this.vZ * (double)l / 4.0D, -this.vX, -this.vY + 0.2D, -this.vZ);
                }
            }

            this.posX += this.vX;
            this.posY += this.vY;
            this.posZ += this.vZ;
            f2 = MathHelper.sqrt_double(this.vX * this.vX + this.vZ * this.vZ);
            this.rotationYaw = (float)(Math.atan2(this.vX, this.vZ) * 180.0D / Math.PI);

            for (this.rotationPitch = (float)(Math.atan2(this.vY, (double)f2) * 180.0D / Math.PI); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F)
            {
                ;
            }

            while (this.rotationPitch - this.prevRotationPitch >= 180.0F)
            {
                this.prevRotationPitch += 360.0F;
            }

            while (this.rotationYaw - this.prevRotationYaw < -180.0F)
            {
                this.prevRotationYaw -= 360.0F;
            }

            while (this.rotationYaw - this.prevRotationYaw >= 180.0F)
            {
                this.prevRotationYaw += 360.0F;
            }

            this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
            this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;

            if (this.isInWater())
            {
                for (int j1 = 0; j1 < 4; ++j1)
                {
                    f3 = 0.25F;
                    this.worldObj.spawnParticle("bubble", this.posX - this.vX * (double)f3, this.posY - this.vY * (double)f3, this.posZ - this.vZ * (double)f3, this.vX, this.vY, this.vZ);
                }
            }

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
                        this.vX = cos * length ;
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

            this.setPosition(this.posX, this.posY, this.posZ);
            this.doBlockCollisions();
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setByte("inGround", (byte)(this.inGround ? 1 : 0));
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        this.inGround = par1NBTTagCompound.getByte("inGround") == 1;
    }

    /**
     * Called by a player entity when they collide with an entity
     */
    public void onCollideWithPlayer(EntityPlayer par1EntityPlayer) { }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public float getShadowSize()
    {
        return 0.0F;
    }

    /**
     * If returns false, the item will not inflict any damage against entities.
     */
    public boolean canAttackWithItem()
    {
        return false;
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
}
