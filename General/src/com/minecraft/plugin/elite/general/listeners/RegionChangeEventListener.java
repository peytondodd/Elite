package com.minecraft.plugin.elite.general.listeners;

import com.minecraft.plugin.elite.general.api.GeneralPlayer;
import com.minecraft.plugin.elite.general.api.events.mode.ModeChangeEvent;
import com.minecraft.plugin.elite.general.api.events.region.RegionEnterEvent;
import com.minecraft.plugin.elite.general.api.events.region.RegionLeaveEvent;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class RegionChangeEventListener implements Listener {

    @EventHandler
    public void callRegionChange(PlayerMoveEvent e) {
        GeneralPlayer p = GeneralPlayer.get(e.getPlayer());
        check(p, e.getFrom(), e.getTo());
    }

    @EventHandler
    public void callRegionChange(PlayerTeleportEvent e) {
        GeneralPlayer p = GeneralPlayer.get(e.getPlayer());
        check(p, e.getFrom(), e.getTo());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onModeChange(ModeChangeEvent e) {
        GeneralPlayer p = e.getPlayer();
        WorldGuardPlugin wgp = WorldGuardPlugin.inst();
        RegionManager manager = wgp.getRegionManager(p.getPlayer().getWorld());
        ApplicableRegionSet regions = manager.getApplicableRegions(p.getPlayer().getLocation());
        if(e.isToMode()) {
            for(ProtectedRegion reg : regions) {
                RegionLeaveEvent event = new RegionLeaveEvent(p, reg);
                Bukkit.getPluginManager().callEvent(event);
            }
        } else {
            for(ProtectedRegion reg : regions) {
                RegionEnterEvent event = new RegionEnterEvent(p, reg);
                Bukkit.getPluginManager().callEvent(event);
            }
        }
    }

    private void check(GeneralPlayer p, Location from, Location to) {
        WorldGuardPlugin wgp = WorldGuardPlugin.inst();
        RegionManager manager = wgp.getRegionManager(p.getPlayer().getWorld());

        final ApplicableRegionSet oldRegions = manager.getApplicableRegions(from);
        final ApplicableRegionSet newRegions = manager.getApplicableRegions(to);

        for(ProtectedRegion reg : oldRegions) {
            if(!newRegions.getRegions().contains(reg)) {
                RegionLeaveEvent event = new RegionLeaveEvent(p, reg);
                Bukkit.getPluginManager().callEvent(event);
            }
        }

        for(ProtectedRegion reg : newRegions) {
            if(!oldRegions.getRegions().contains(reg)) {
                RegionEnterEvent event = new RegionEnterEvent(p, reg);
                Bukkit.getPluginManager().callEvent(event);
            }
        }
    }
}
