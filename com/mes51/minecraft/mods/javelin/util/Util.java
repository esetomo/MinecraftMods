package com.mes51.minecraft.mods.javelin.util;

import com.mes51.minecraft.mods.javelin.entities.filter.FilterJavelinTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.util.List;

/**
 * Package: com.mes51.minecraft.mods.javelin.util
 * Date: 13/06/13
 * Time: 23:50
 */
public class Util {
    private static final double TARGET_SEARCH_RANGE = 256.0;

    // tergetの検索
    // see: https://github.com/bbc-mc/LockOnMod/blob/master/bbc_mc/LockOn/LockOn.java
    public static Entity searchTarget(EntityPlayer player)
    {
        Vec3 lookAt = player.getLookVec();
        lookAt = Vec3.createVectorHelper(
                lookAt.xCoord * TARGET_SEARCH_RANGE,
                lookAt.yCoord * TARGET_SEARCH_RANGE,
                lookAt.zCoord * TARGET_SEARCH_RANGE
        );
        Vec3 eye = getPlayerPosition(player);
        return searchTarget(player, eye, lookAt);
    }

    public static Entity searchTarget(Entity entity, Vec3 eye, Vec3 lookAt)
    {
        Vec3 limit = eye.addVector(lookAt.xCoord, lookAt.yCoord, lookAt.zCoord);
        MovingObjectPosition movingObjectPosition = entity.worldObj.rayTraceBlocks_do_do(CopyVec3(eye), limit, false, true);

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
            limit = eye.addVector(lookAt.xCoord, lookAt.yCoord, lookAt.zCoord);
        }

        List entities = entity.worldObj.getEntitiesWithinAABBExcludingEntity(
                entity,
                entity.boundingBox.expand(TARGET_SEARCH_RANGE, TARGET_SEARCH_RANGE, TARGET_SEARCH_RANGE),
                new FilterJavelinTarget()
        );
        Entity result = null;
        double d = Double.MAX_VALUE;
        for (int i = entities.size() - 1; i > -1; i--)
        {
            Entity candidate = (Entity)entities.get(i);
            if (!candidate.canBeCollidedWith())
            {
                continue;
            }

            float aabbSize = 0.3F;
            AxisAlignedBB aabb = candidate.boundingBox.expand(aabbSize, aabbSize, aabbSize);
            MovingObjectPosition pos = aabb.calculateIntercept(eye, limit);
            if (pos == null)
            {
                continue;
            }

            double dist = eye.distanceTo(pos.hitVec);
            if (dist < d)
            {
                result = candidate;
                d = dist;
            }
        }
        return result;
    }

    // serverとclientで視点補正がかかっていたりいなかったりするため、その差を吸収する
    // see: http://forum.minecraftuser.jp/viewtopic.php?f=21&t=7907#p63907
    public static Vec3 getPlayerPosition(EntityPlayer player)
    {
        Vec3 pos = player.getPosition(1.0F);
        if (player.yOffset == 0.0)
        {
            pos.yCoord += player.getEyeHeight();
        }
        return pos;
    }

    public static Vec3 CopyVec3(Vec3 val)
    {
        return Vec3.createVectorHelper(val.xCoord, val.yCoord, val.zCoord);
    }
}
