package com.matias.nightfallstalker;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(NightfallStalkerMod.MOD_ID)
public class NightfallStalkerMod {
    public static final String MOD_ID = "nightfallstalker";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public NightfallStalkerMod() {
        MinecraftForge.EVENT_BUS.register(new TerrorEventHandler());
        LOGGER.info("Nightfall Stalker cargado. No mires detras de ti...");
    }
}
