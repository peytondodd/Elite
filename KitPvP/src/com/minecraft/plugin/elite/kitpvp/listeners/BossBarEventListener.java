package com.minecraft.plugin.elite.kitpvp.listeners;

import com.minecraft.plugin.elite.general.api.ePlayer;
import com.minecraft.plugin.elite.general.api.special.BossBar;
import com.minecraft.plugin.elite.kitpvp.KitPvP;
import com.minecraft.plugin.elite.kitpvp.manager.KitPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.List;

public class BossBarEventListener implements Listener {

    @EventHandler
    public void showBarOnViewPlayerWithKit(PlayerMoveEvent e) {
        ePlayer p = ePlayer.get(e.getPlayer());
        if(p.isInRegion(KitPvP.REGION_FEAST) || p.isInRegion(KitPvP.REGION_EHG) || p.isInRegion(KitPvP.REGION_DUEL)) {
            ePlayer nearest = null;
            List<ePlayer> possible = new ArrayList<>();
            for(Entity ent : p.getPlayer().getNearbyEntities(3, 3, 3))
                if(ent instanceof Player)
                    if(p.hasLineOfSight(ent))
                        possible.add(ePlayer.get((Player) ent));

            for(ePlayer near : possible)
                if(nearest == null || p.getPlayer().getLocation().distance(near.getPlayer().getLocation()) < p.getPlayer().getLocation().distance(nearest.getPlayer().getLocation()))
                    nearest = near;

            if(nearest != null) {
                KitPlayer kz = KitPlayer.get(nearest.getUniqueId());
                if(!nearest.isAdminMode() && !nearest.isWatching()) {
                    String text = null;
                    if(p.isInRegion(KitPvP.REGION_DUEL)) {
                        text = Long.toString(nearest.getELO()) + " ELO";
                    } else if(kz.hasKit()) {
                       text = kz.getKit().getName();
                    }

                    if(text != null) {
                        BossBar bar = BossBar.get(p);
                        if(bar == null) {
                            BossBar newBar = new BossBar(p);
                            newBar.show(nearest.getName() + " - " + text, 10);
                        } else {
                            bar.changeTo(nearest.getName() + " - " + text, 10);
                        }
                    }
                }
            }
        }
    }
}
