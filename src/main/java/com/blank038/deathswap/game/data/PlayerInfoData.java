package com.blank038.deathswap.game.data;

import com.blank038.deathswap.DeathSwap;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

/**
 * @author Blank038
 */
public class PlayerInfoData {
    public static final HashMap<String, PlayerInfoData> DATA_MAP = new HashMap<>();
    private final UUID PLAYER_UUID;
    @Getter
    @Setter
    private int games, win, lose, kill, death;

    public PlayerInfoData(Player player) {
        this.PLAYER_UUID = player.getUniqueId();

        File file = new File(DeathSwap.getInstance().getDataFolder() + "/data/", PLAYER_UUID.toString() + ".yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(file);

        games = data.getInt("info.games");
        win = data.getInt("info.win");
        lose = data.getInt("info.lose");
        kill = data.getInt("info.kill");
        death = data.getInt("info.death");
    }

    public void save() {
        File file = new File(DeathSwap.getInstance().getDataFolder() + "/data/", PLAYER_UUID.toString() + ".yml");
        FileConfiguration data = new YamlConfiguration();

        data.set("info.games", games);
        data.set("info.win", win);
        data.set("info.lose", lose);
        data.set("info.kill", kill);
        data.set("info.death", death);

        try {
            data.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
