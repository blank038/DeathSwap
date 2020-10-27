package com.blank038.deathswap.game;

import com.blank038.deathswap.DeathSwap;
import com.blank038.deathswap.enums.GameLocType;
import com.blank038.deathswap.enums.GameStatus;
import com.blank038.deathswap.game.data.PlayerTempData;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameArena {
    private final HashMap<UUID, PlayerTempData> playerMap = new HashMap<>();
    private final String world;
    private String arenaName;
    private int min, max, size;
    private GameStatus status;
    private GameLocType gameLocType;
    private int tempSize;
    private Location waitLoc, endLoc;

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

    public int getPlayerCount() {
        return playerMap.size();
    }

    public boolean hasPlayer(Player player) {
        return playerMap.containsKey(player.getUniqueId());
    }

    /**
     * 玩家加入当前竞技场
     */
    public boolean join(Player player) {
        if (playerMap.containsKey(player.getUniqueId())) {
            player.sendMessage(DeathSwap.getLangData().getString("message.in-arena", true));
            return false;
        }
        if (playerMap.size() >= max) {
            player.sendMessage(DeathSwap.getLangData().getString("message.max-player", true));
            return false;
        }
        // 传送玩家
        Chunk chunk = waitLoc.getChunk();
        if (!chunk.isLoaded()) chunk.load();
        player.teleport(waitLoc, PlayerTeleportEvent.TeleportCause.PLUGIN);
        // 创建玩家临时数据
        playerMap.put(player.getUniqueId(), new PlayerTempData(player));
        player.sendMessage(DeathSwap.getLangData().getString("message.join", true)
                .replace("%now%", String.valueOf(playerMap.size())).replace("%max%", String.valueOf(max)));
        return true;
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
            // 设置初始坐标
            border.setCenter(getInitLocation(w));
            // 游戏房间
            for (Map.Entry<UUID, PlayerTempData> entry : playerMap.entrySet()) {
                Player player = Bukkit.getPlayer(entry.getKey());
                Location rc = randomLocation(size - 1);
                player.teleport(rc, PlayerTeleportEvent.TeleportCause.PLUGIN);
            }
        }
    }

    private Location getInitLocation(World world) {
        if (gameLocType == GameLocType.SPAWN_LOC) return world.getSpawnLocation();
        else {
            double rx = Math.random() * 10000, rz = Math.random() * 10000;
            return new Location(world, rx, 100, rz);
        }
    }

    public Location randomLocation(int radius) {
        World world = Bukkit.getWorld(this.world);
        Location spawnLocation = world.getWorldBorder().getCenter();
        int minX = spawnLocation.getBlockX() - size, maxX = spawnLocation.getBlockX() + size;
        int minZ = spawnLocation.getBlockZ() - size, maxZ = spawnLocation.getBlockZ() + size;
        int randomX = (int) (minX + Math.random() * (maxX - minX));
        int randomZ = (int) (minZ + Math.random() * (maxZ - minZ));
        return new Location(world, randomX, world.getHighestBlockYAt(randomX, randomZ) + 1, randomZ);
    }

}
