package com.blank038.deathswap.game;

import com.blank038.deathswap.DeathSwap;
import com.blank038.deathswap.enums.GameStatus;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class GameManager {
    private final DeathSwap INSTANCE;
    private final HashMap<String, GameArena> arenaMap = new HashMap<>();
    private final HashMap<UUID, String> playerMap = new HashMap<>();

    public GameManager() {
        INSTANCE = DeathSwap.getInstance();
    }

    public void submitJoin(Player player, String arenaKey) {
        if (arenaKey == null || !arenaMap.containsKey(arenaKey)) {
            player.sendMessage(DeathSwap.getLangData().getString("message.arena-not-exists", true));
            return;
        }
        GameStatus status = arenaMap.get(arenaKey).getGameStatus();
        if (status == GameStatus.STARTED || status == GameStatus.END || status == GameStatus.ERROR) {
            player.sendMessage(DeathSwap.getLangData().getString("message.game-status." + status.name().toLowerCase(), true));
            return;
        }
        if (arenaMap.get(arenaKey).join(player)) playerMap.put(player.getUniqueId(), arenaKey);
    }

    public void submitQuit(Player player) {
        if (playerMap.containsKey(player.getUniqueId())) {
            if (arenaMap.get(playerMap.get(player.getUniqueId())).quit(player))
                playerMap.remove(player.getUniqueId());
        } else {
            player.sendMessage(DeathSwap.getLangData().getString("message.not-in-arena", true));
        }
    }

    public void start() {
    }

    public void stop() {
    }

    public GameArena getArena(String arenaName) {
        return arenaMap.getOrDefault(arenaName, null);
    }

    public boolean hasArena(String key) {
        return arenaMap.containsKey(key);
    }

    public boolean hasPlayer(UUID uuid) {
        return playerMap.containsKey(uuid);
    }
}
