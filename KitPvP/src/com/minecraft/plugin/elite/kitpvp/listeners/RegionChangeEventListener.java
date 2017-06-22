package com.minecraft.plugin.elite.kitpvp.listeners;

import com.minecraft.plugin.elite.general.api.ePlayer;
import com.minecraft.plugin.elite.general.api.events.region.RegionEnterEvent;
import com.minecraft.plugin.elite.general.api.events.region.RegionLeaveEvent;
import com.minecraft.plugin.elite.kitpvp.KitPvP;
import com.minecraft.plugin.elite.kitpvp.KitPvPLanguage;
import com.minecraft.plugin.elite.kitpvp.manager.KitPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RegionChangeEventListener implements Listener {


    @EventHandler
    public void onSpawnLeave(RegionLeaveEvent e) {
        if(e.getRegion().getId().equalsIgnoreCase(KitPvP.REGION_SPAWN)) {
            ePlayer p = e.getPlayer();
            if(p.hasTool())
                p.clearTools();
            p.sendMessage(KitPvPLanguage.REGION_SPAWN_LEAVE);
        }
    }

    @EventHandler
    public void onEnterArena(RegionEnterEvent e) {
        KitPlayer kp = KitPlayer.get(e.getPlayer().getUniqueId());
        if(e.getRegion().getId().equalsIgnoreCase(KitPvP.REGION_EHG)) {
            kp.giveKitInv(false);
        } else if(e.getRegion().getId().equalsIgnoreCase(KitPvP.REGION_FEAST)) {
            kp.giveKitInv(true);
        }
    }
}
