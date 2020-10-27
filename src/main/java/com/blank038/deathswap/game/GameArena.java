package com.blank038.deathswap.game;

import com.blank038.deathswap.enums.GameStatus;
import com.blank038.deathswap.game.data.PlayerTempData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldBorder;
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
    private GameStatus status;
    private int tempSize;

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
     * 玩家加入当前竞技场
     */
    public void join(Player player) {

    }

    /**
     * 玩家退出竞技场或离开服务器
     *
     * @param player 目标玩家
     * @param force  是否为强制退出
     */
    public void quit(Player player, boolean force) {

    }

    /**
     * 玩家死亡触发
     *
     * @param player 目标玩家
     */
    public void onDeath(Player player) {

    }

    /**
     * 开始竞技场
     */
    public void start() {
        if (status == GameStatus.STARTING) {
            // 修改竞技场状态
            status = GameStatus.STARTED;
            // 设置世界出生点及世界边界大小
            World w = Bukkit.getWorld(world);
            WorldBorder border = w.getWorldBorder();

        }
    }
}
