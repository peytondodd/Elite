package com.minecraft.plugin.elite.nohax.manager;

import com.minecraft.plugin.elite.general.api.GeneralPlayer;
import com.minecraft.plugin.elite.nohax.NoHax;
import com.minecraft.plugin.elite.nohax.NoHaxLanguage;
import com.minecraft.plugin.elite.nohax.manager.hax.PlayerAttack;
import com.minecraft.plugin.elite.nohax.manager.hax.PlayerClick;
import com.minecraft.plugin.elite.nohax.manager.hax.PlayerMove;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HaxPlayer extends GeneralPlayer {

    private static Map<UUID, HaxPlayer> players = new HashMap<>();
    private static Map<UUID, HaxPlayer> loggingInPlayers = new HashMap<>();

    private boolean showAlerts;

    private PlayerMove lastMove;
    private PlayerMove lastOnGround;

    private PlayerMove[] moves;
    private PlayerAttack[] attacks;
    private PlayerClick[] clicks;

    private int moveCount;
    private int attackCount;
    private int clickCount;
    private int lastOnGroundMovesAgo;

    private boolean canFly;
    private boolean canSpeed;

    private BukkitRunnable combatLogTask;
    private BukkitRunnable knockbackTask;

    private long invalid;

    public HaxPlayer(Player player) {
        super(player);
        this.showAlerts = false;

        this.moves = new PlayerMove[50];
        this.attacks = new PlayerAttack[5];
        this.clicks = new PlayerClick[150];

        this.moveCount = 0;
        this.attackCount = 0;
        this.clickCount = 0;
        this.lastOnGroundMovesAgo = 0;

        this.invalid = 0;

        this.canFly = false;
        this.canSpeed = false;

        this.combatLogTask = null;
        this.knockbackTask = null;
    }

    public static HaxPlayer get(Player player) {
        return get(player.getUniqueId());
    }

    public static HaxPlayer get(UUID uuid) {
        HaxPlayer result = players.get(uuid);
        if (result == null) {
            HaxPlayer login_result = loggingInPlayers.get(uuid);
            if (login_result == null) {
                return null;
            }
            return login_result;
        }
        return result;
    }

    public static HaxPlayer get(String name) {
        @SuppressWarnings("deprecation") Player player = Bukkit.getPlayer(name);
        if (player != null) {
            HaxPlayer result = get(player.getUniqueId());
            if (result != null)
                return result;
        }
        return null;
    }

    public static HaxPlayer getPlayerLoggingIn(Player player) {
        return getPlayerLoggingIn(player.getUniqueId());
    }

    public static HaxPlayer getPlayerLoggingIn(UUID uuid) {
        return loggingInPlayers.get(uuid);
    }

    @SuppressWarnings("UnusedReturnValue")
    public static HaxPlayer login(Player player) {
        HaxPlayer p = new HaxPlayer(player);
        loggingInPlayers.put(player.getUniqueId(), p);
        return p;
    }

    public void logout() {
        loggingInPlayers.remove(this.getUniqueId());
    }

    public void join() {
        loggingInPlayers.remove(this.getUniqueId());
        players.put(this.getUniqueId(), this);
    }

    public void leave() {
        players.remove(this.getUniqueId());
    }

    public boolean canBypassChecks() {
        return this.getPlayer().hasPermission("enohax.bypass");
    }

    public void showAlerts(boolean alerts) {
        this.showAlerts = alerts;
    }

    public boolean canViewAlerts() {
        return this.showAlerts;
    }

    public void setCombatLog() {
        if(this.isCombatLog()) {
            this.getCombatLogTask().cancel();
            this.setCombatLogTask(null);
        }
        this.setCombatLogTask(new BukkitRunnable() {
            @Override
            public void run() {
                HaxPlayer p = get(getUniqueId());
                if(p == null)
                    cancel();
                if (p.isCombatLog()) {
                    p.sendMessage(NoHaxLanguage.COMBATLOG_SAFE);
                    p.getCombatLogTask().cancel();
                    p.setCombatLogTask(null);
                }
            }
        });
        this.getCombatLogTask().runTaskLater(NoHax.getPlugin(), 200);
    }

    public boolean isCombatLog() {
        return this.combatLogTask != null;
    }

    public BukkitRunnable getCombatLogTask() {
        return this.combatLogTask;
    }

    public void setCombatLogTask(BukkitRunnable runnable) {
        this.combatLogTask = runnable;
    }

    public boolean isKnockbacked() {
        return this.knockbackTask != null;
    }

    public BukkitRunnable getKnockbackTask() {
        return this.knockbackTask;
    }

    public void setKnockbackTask(BukkitRunnable runnable) {
        this.knockbackTask = runnable;
    }

    public void invalidate() {
        this.invalidate(2000);
    }

    public void invalidate(long ms) {
        this.invalid = System.currentTimeMillis() + ms;
    }

    public boolean isValid() {
        return System.currentTimeMillis() > this.invalid;
    }


    public void setLastOnGround(PlayerMove move) {
        this.lastOnGround = move;
    }

    public void setLastOnGroundMovesAgo(int i) {
        this.lastOnGroundMovesAgo = i;
    }

    public PlayerMove getLastOnGround() {
        return this.lastOnGround;
    }

    public int getLastOnGroundMovesAgo() {
        return this.lastOnGroundMovesAgo;
    }

    public void setLastMove(PlayerMove move) {
        this.lastMove = move;
    }

    public PlayerMove getLastMove() {
        return this.lastMove;
    }

    public void setMoveCount(int i) {
        this.moveCount = i;
    }

    public int getMoveCount() {
        return this.moveCount;
    }

    public PlayerMove[] getMoves() {
        return this.moves;
    }

    public void setMoves(PlayerMove[] moves) {
        this.moves = moves;
    }

    public PlayerMove getMovesAgo(int x) {
        if (this.getMoveCount() - x < 0)
            return null;
        int index = Math.abs(this.getMoveCount() - x) % this.getMoves().length;
        return this.getMoves()[index];
    }



    public PlayerAttack[] getAttacks() {
        return this.attacks;
    }

    public int getAttackCount() {
        return this.attackCount;
    }

    public void setAttackCount(int i) {
        this.attackCount = i;
    }

    public PlayerAttack getDamageAgo(int x) {
        if (this.getAttackCount() - x < 0)
            return null;
        int index = Math.abs(this.getAttackCount() - x) % this.getAttacks().length;
        return this.getAttacks()[index];
    }

    public PlayerClick[] getClicks() {
        return this.clicks;
    }

    public int getClickCount() {
        return this.clickCount;
    }

    public void setClickCount(int i) {
        this.clickCount = i;
    }

    public PlayerClick getClicksAgo(int x) {
        if (this.getClickCount() - x < 0)
            return null;
        int index = Math.abs(this.getClickCount() - x) % this.getClicks().length;
        return this.getClicks()[index];
    }


    public boolean isCanFly() {
        return this.canFly || this.getPlayer().getAllowFlight();
    }

    public boolean isCanSpeed() {
        return canSpeed;
    }

    public void setCanFly(boolean fly) {
        this.canFly = fly;
    }

    public void setCanSpeed(boolean speed) {
        this.canSpeed = speed;
    }
}
