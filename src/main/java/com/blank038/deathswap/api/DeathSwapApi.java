package com.blank038.deathswap.api;

import com.blank038.deathswap.DeathSwap;
import com.blank038.deathswap.enums.GameLocType;
import com.blank038.deathswap.game.GameArena;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * @author Blank038
 */
public class DeathSwapApi {
    private final DeathSwap INSTANCE;

    public DeathSwapApi() {
        INSTANCE = DeathSwap.getInstance();
    }

    public boolean createArena(String arenaName, String world) {
        if (INSTANCE.getGameManager().hasArena(arenaName)) {
            return false;
        }
        File file = new File(INSTANCE.getDataFolder() + "/arenas/", arenaName + ".yml");
        FileConfiguration data = new YamlConfiguration();
        data.set("min", 2);
        data.set("max", 8);
        data.set("size", 100);
        data.set("wb-interval", 50);
        data.set("tp-interval", 300);
        data.set("display-name", arenaName);
        data.set("world", world);
        data.set("loc-type", GameLocType.RANDOM.name());
        try {
            data.save(file);
        } catch (IOException e) {
            INSTANCE.getLogger().info("异常: " + e.getLocalizedMessage());
            return false;
        }
        INSTANCE.getGameManager().addArena(arenaName, new GameArena(file));
        return true;
    }
}
