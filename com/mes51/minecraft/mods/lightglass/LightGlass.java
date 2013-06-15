package com.mes51.minecraft.mods.lightglass;

import com.mes51.minecraft.mods.lightglass.blocks.BlockLightGlass;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

/**
 * Package: com.mes51.minecraft.mods.lightglass
 * Date: 13/06/15
 * Time: 18:57
 */

@Mod(name = "LightGlass", modid = "LightGlass", version = "1.0.0")
@NetworkMod(clientSideRequired = true, serverSideRequired = true)
public class LightGlass {
    @Instance("LightGlass")
    public static LightGlass instance = null;

    @Init
    public void load(FMLInitializationEvent event)
    {
        BlockLightGlass.register();
    }
}
