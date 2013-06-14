package com.mes51.minecraft.mods.javelin;

import com.mes51.minecraft.mods.javelin.entities.EntityJavelinMissile;
import com.mes51.minecraft.mods.javelin.items.ItemJavelin;
import com.mes51.minecraft.mods.javelin.items.ItemJavelinMissile;
import com.mes51.minecraft.mods.javelin.items.ItemMissileOuter;
import com.mes51.minecraft.mods.javelin.items.ItemShapedCharge;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

import static cpw.mods.fml.common.Mod.Instance;

/**
 * Package: com.mes51.minecraft.mods.javelin
 * Date: 2013/06/03
 * Time: 23:50
 */

@Mod(name = "Javelin", modid = "Javelin", version = "1.0.0")
@NetworkMod(clientSideRequired = true, serverSideRequired = true)
public class Javelin {
    @Instance("Javelin")
    public static Javelin instacne = null;

    @Init
    public void load(FMLInitializationEvent event)
    {
        ItemJavelin.register();
        ItemJavelinMissile.register();
        ItemShapedCharge.register();
        ItemMissileOuter.register();
        EntityJavelinMissile.register();
    }

    @PostInit
    public void postInit(FMLPostInitializationEvent event)
    {
        ItemJavelin.registerRecipe();
        ItemJavelinMissile.registerRecipe();
        ItemShapedCharge.registerRecpie();
        ItemMissileOuter.registerRecpie();
    }
}
