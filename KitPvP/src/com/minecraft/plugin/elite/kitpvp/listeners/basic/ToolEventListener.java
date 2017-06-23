package com.minecraft.plugin.elite.kitpvp.listeners.basic;

import com.minecraft.plugin.elite.general.api.ePlayer;
import com.minecraft.plugin.elite.general.api.events.ToolClickEvent;
import com.minecraft.plugin.elite.general.api.special.menu.MenuTool;
import com.minecraft.plugin.elite.kitpvp.KitPvPLanguage;
import com.minecraft.plugin.elite.kitpvp.manager.KitPlayer;
import com.minecraft.plugin.elite.kitpvp.manager.duel.tools.DuelTool;
import com.minecraft.plugin.elite.kitpvp.manager.duel.tools.SpawnTool;
import com.minecraft.plugin.elite.kitpvp.manager.kits.Kit;
import com.minecraft.plugin.elite.kitpvp.manager.kits.KitGUI;
import com.minecraft.plugin.elite.kitpvp.manager.kits.KitSelectorTool;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class ToolEventListener implements Listener {

	@EventHandler
	public void toolClick(ToolClickEvent e) {
		ePlayer p = e.getPlayer();
		if(e.getTool() instanceof KitSelectorTool) {
			if(p.isAdminMode() || p.isWatching()) {
				p.sendMessage(KitPvPLanguage.KIT_ERROR_MODE);
				return;
			}
			KitGUI kitgui = new KitGUI(p.getLanguage());
			p.openGUI(kitgui, kitgui.selector(KitPlayer.get(p.getUniqueId()), 1));
		}
		if(e.getTool() instanceof SpawnTool) {
			p.getPlayer().teleport(p.getPlayer().getWorld().getSpawnLocation());
		}
	}
	
	@EventHandler
	public void onKitItemDrop(PlayerDropItemEvent e) {
		KitPlayer p = KitPlayer.get(e.getPlayer());
		if(p.hasKit()) {
			for(Kit kit : Kit.values()) {
				if(p.hasKit(kit)) {
					kit.getItems().forEach((item) -> {
						if(e.getItemDrop().getItemStack().getType() == item.getType())
							e.setCancelled(true);
					});
				}
			}
		}
	}
}
