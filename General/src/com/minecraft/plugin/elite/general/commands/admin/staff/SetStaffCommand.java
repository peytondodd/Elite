package com.minecraft.plugin.elite.general.commands.admin.staff;

import com.minecraft.plugin.elite.general.General;
import com.minecraft.plugin.elite.general.GeneralLanguage;
import com.minecraft.plugin.elite.general.api.abstracts.eCommand;
import com.minecraft.plugin.elite.general.api.ePlayer;
import com.minecraft.plugin.elite.general.api.enums.Language;
import com.minecraft.plugin.elite.general.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class SetStaffCommand extends eCommand implements TabCompleter {

    public SetStaffCommand() {
        super("setstaff", "egeneral.setrank", true);
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String lbl, String[] args) {
        if (args.length == 1)
            return General.STAFF_SLOTS;
        return null;
    }

    @SuppressWarnings("deprecation")
    public boolean execute(CommandSender cs, Command cmd, String[] args) {

        Language lang = Language.ENGLISH;
        if(cs instanceof Player)
            lang = ePlayer.get((Player) cs).getLanguage();
        if (args.length > 2) {
            OfflinePlayer z = Bukkit.getOfflinePlayer(args[1]);
            String slot = args[0].toLowerCase();
            if(General.STAFF_SLOTS.contains(slot)) {
                StringBuilder details = new StringBuilder();
                for(int i = 2; i < args.length; i++)
                    details.append(args[i]).append(" ");
                String role = details.toString().trim();
                Database db = General.getDB();
                db.update(General.DB_STAFF, "uuid", z.getUniqueId(), "rank", slot);
                db.update(General.DB_STAFF, "role", role, "rank", slot);
                cs.sendMessage(lang.get(GeneralLanguage.STAFF_UPDATED));
                return true;
            } else {
                cs.sendMessage(lang.get(GeneralLanguage.STAFF_NULL));
                return true;
            }
        } else {
            cs.sendMessage(lang.get(GeneralLanguage.STAFF_USAGE));
            return true;
        }
    }
}
