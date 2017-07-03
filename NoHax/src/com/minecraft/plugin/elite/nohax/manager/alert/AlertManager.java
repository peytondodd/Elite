package com.minecraft.plugin.elite.nohax.manager.alert;

import com.minecraft.plugin.elite.general.api.ePlayer;
import com.minecraft.plugin.elite.general.api.enums.Language;
import com.minecraft.plugin.elite.general.punish.PunishManager;
import com.minecraft.plugin.elite.general.punish.PunishReason;
import com.minecraft.plugin.elite.nohax.NoHax;
import com.minecraft.plugin.elite.nohax.NoHaxLanguage;
import com.minecraft.plugin.elite.nohax.manager.HaxPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class AlertManager {

    private static List<Alert> alerts = new ArrayList<>();

    public static Alert get(ePlayer hacker, AlertType type) {
        for (Alert alert : getAll())
            if (alert.getHacker().getUniqueId().equals(hacker.getUniqueId()) && alert.getType() == type)
                return alert;
        return null;
    }

    public static Collection<Alert> getAll() {
        return alerts;
    }

    public static List<Alert> getAll(ePlayer p) {
        List<Alert> list = getAll().stream().filter(alert -> alert.getHacker().getUniqueId().equals(p.getUniqueId())).collect(Collectors.toList());
        return list;
    }

    public static void clear(ePlayer p) {
        Collection<Alert> list = new ArrayList<>();
        list.addAll(getAll(p));
        alerts.removeAll(list);
    }

    public static void set(ePlayer p, AlertType type, double count) {
        set(p, type, 1, count);
    }

    public static void set(ePlayer p, AlertType type, int sensitivity) {
        double count;
        Alert alert = get(p, type);
        if (alert != null)
            count = alert.getDetections() * 10;
        else
            count = 10;
        set(p, type, sensitivity, count);
    }

    public static void set(ePlayer p, AlertType type, int sensitivity, double count) {
        final Alert alert = new Alert(p, type);
        alerts.add(alert);
        DecimalFormat df = new DecimalFormat("0");
        double percent = count * 100D;
        double chance = (percent > 100 ? 100 : percent);
        final int confirmed = alert.getDetections();
        if (confirmed == 3 * sensitivity || confirmed == 6 * sensitivity || confirmed == 9 * sensitivity) {
            for (Player players : Bukkit.getOnlinePlayers()) {
                HaxPlayer all = HaxPlayer.get(players);
                if (all.canViewAlerts())
                    all.getPlayer().sendMessage(all.getLanguage().get(NoHaxLanguage.ALERT_HACKS)
                            .replaceAll("%p", p.getName())
                            .replaceAll("%hack", alert.toString())
                            .replaceAll("%chance", df.format(chance) + "%"));
            }
            System.out.println(Language.ENGLISH.get(NoHaxLanguage.ALERT_HACKS)
                    .replaceAll("%p", p.getName())
                    .replaceAll("%hack", alert.toString())
                    .replaceAll("%chance", df.format(chance) + "%"));
        }
        if (confirmed == 10 * sensitivity) {
            PunishManager.punish("System - AutoBan", Bukkit.getOfflinePlayer(p.getUniqueId()), PunishReason.CHEATING, alert.toString());
            alerts.remove(alert);
        }

        Bukkit.getScheduler().runTaskLater(NoHax.getPlugin(), () -> alerts.remove(alert), 6000);
    }
}
