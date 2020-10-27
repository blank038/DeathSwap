package com.blank038.deathswap.game;

import com.blank038.deathswap.game.data.PlayerTempData;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;

public class GameArena {
    private final int min, max, size;
    private final HashMap<String, PlayerTempData> playerMap = new HashMap<>();
    private String world, arenaName;

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

    public void join(Player player) {

    }

    public void quit(Player player, boolean force) {

    }

    public void onDeath(Player player) {

    }
}