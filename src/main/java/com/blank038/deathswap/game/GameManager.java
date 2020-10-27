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

    }

    public void submitQuit(Player player, boolean force) {
        if (playerMap.containsKey(player.getName())) {

        }
    }
}
