package com.blank038.deathswap.game;

import com.blank038.deathswap.game.data.PlayerTempData;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;

public class GameArena {
    private final HashMap<String, PlayerTempData> playerMap = new HashMap<>();
    private final String world;
    private String arenaName;
    private int min, max, size;

    public GameArena(File file) {
        FileConfiguration data = YamlConfiguration.loadConfiguration(file);

        min = data.getInt("min");
        max = data.getInt("max");
        world = data.getString("world");
        size = data.getInt("size");
        arenaName = ChatColor.translateAlternateColorCodes('&',
                data.getString("display-name"));
    }

    public GameArena(String world, String arenaName) {
        this.world = world;
        min = 2;
        max = 8;
        size = 100;
        this.arenaName = ChatColor.translateAlternateColorCodes('&', arenaName);
    }

    public String getArenaName() {
        return arenaName;
    }

    public void setDisplayName(String name) {
        arenaName = ChatColor.translateAlternateColorCodes('&', name);
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public String getGameWorld() {
        return world;
    }

    public int getWorldBorderSize() {
        return size;
    }

    public void setWorldBorderSize(int size) {
        this.size = size;
    }

    /**
     * Player try to join this arena.
     */
    public void join(Player player) {

    }

    /**
     * On player quit arena or quit server.
     *
     * @param player Target player.
     * @param force  Is force quit?
     */
    public void quit(Player player, boolean force) {

    }

    /**
     * Player died trigger.
     *
     * @param player Target player.
     */
    public void onDeath(Player player) {

    }
}
