package com.mes51.minecraft.mods.javelin.entities.filter;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.IMob;

/**
 * Package: com.mes51.minecraft.mods.javelin.entities.filter
 * Date: 2013/06/05
 * Time: 22:14
 */
public final class FilterJavelinTarget implements IEntitySelector {
    @Override
    public boolean isEntityApplicable(Entity entity) {
        return  entity instanceof IMob || entity instanceof IBossDisplayData;
    }
}
