package com.blank038.deathswap.game;

import com.blank038.deathswap.DeathSwap;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class GameManager {
    private final DeathSwap INSTANCE;
    private final HashMap<String, GameArena> arenaMap = new HashMap<>();
    private final HashMap<String, String> playerMap = new HashMap<>();

    public GameManager() {
        INSTANCE = DeathSwap.getInstance();
    }

    public void submitJoin(Player player, String arenaKey) {
        if (arenaKey == null || !arenaMap.containsKey(arenaKey)) {
            player.sendMessage(DeathSwap.getLangData().getString("message.arena-not-exists", true));
            return;
        }
    }

    public void submitQuit(Player player, boolean force) {
        if (playerMap.containsKey(player.getName())) {

        }
    }

    public void start() {
    }

    public void stop() {
    }


    public GameArena getArena(String arenaName) {

        return arenaMap.get(arenaName);

    }

}
