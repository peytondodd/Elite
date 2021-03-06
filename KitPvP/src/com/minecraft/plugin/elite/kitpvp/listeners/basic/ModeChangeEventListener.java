package com.minecraft.plugin.elite.kitpvp.listeners.basic;

import com.minecraft.plugin.elite.general.api.GeneralPlayer;
import com.minecraft.plugin.elite.general.api.events.mode.ModeChangeEvent;
import com.minecraft.plugin.elite.general.api.special.menu.MenuTool;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ModeChangeEventListener implements Listener {
	
	@EventHandler
	public void onModeChange(ModeChangeEvent e) {
		GeneralPlayer p = GeneralPlayer.get(e.getPlayer().getPlayer());
		if(e.isToMode()) {
			p.clear();
			p.giveTool(new MenuTool(p.getLanguage()));
		}
		if(!e.isToMode() && p.hasKit())
			p.clear();
	}
}
