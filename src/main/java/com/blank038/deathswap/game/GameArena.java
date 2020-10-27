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
        tempSize = size;
        arenaName = ChatColor.translateAlternateColorCodes('&',
                data.getString("display-name"));

        if (data.contains("loc-type")) {
            gameLocType = GameLocType.valueOf(data.getString("loc-type"));
        }
        loadLoc(data);

        if (world != null && waitLoc != null && endLoc != null && gameLocType != null && min >= 2) {
            init();
        }
    }

    public void init() {

    }

    /**
     * 载入坐标数据
     *
     * @param data 目标配置文件
     */
    public void loadLoc(FileConfiguration data) {
        if (data.contains("end-pos")) {
            String endWorld = data.getString("end-pos.world");
            double x = data.getDouble("end-pos.x");
            double y = data.getDouble("end-pos.y");
            double z = data.getDouble("end-pos.z");
            float yaw = (float) data.getDouble("end-pos.yaw");
            float pitch = (float) data.getDouble("end-pos.pitch");
            if (endWorld == null) {
                return;
            }
            World world = Bukkit.getWorld(endWorld);
            if (world == null) {
                return;
            }
            endLoc = new Location(world, x, y, z, yaw, pitch);
        }
        if (data.contains("wait-pos")) {
            String startWorld = data.getString("wait-pos.world");
            double x = data.getDouble("wait-pos.x");
            double y = data.getDouble("wait-pos.y");
            double z = data.getDouble("wait-pos.z");
            float yaw = (float) data.getDouble("wait-pos.yaw");
            float pitch = (float) data.getDouble("wait-pos.pitch");
            if (startWorld == null) {
                return;
            }
            World world = Bukkit.getWorld(startWorld);
            if (world == null) {
                return;
            }
            waitLoc = new Location(world, x, y, z, yaw, pitch);
        }
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

    public GameStatus getGameStatus() {
        return status;
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
            player.sendMessage(DeathSwap.getLangData().getString("message.arena-full", true));
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
     */
    public boolean quit(Player player) {
        if (playerMap.containsKey(player.getUniqueId())) {
            // 恢复玩家背包
            playerMap.get(player.getUniqueId()).restore();
            TeleportManager.teleportEndLocation(player, endLoc);
            return true;
        }
        return false;
    }

    /**
     * 玩家死亡触发
     *
     * @param player 目标玩家
     */
    public void onDeath(Player player) {

    }

    /**
     * 给房间内全体玩家发送文本
     */
    public void sendAllPlayerText(String text) {
        for (Map.Entry<UUID, PlayerTempData> entry : playerMap.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            player.sendMessage(text);
        }
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
        int minX = spawnLocation.getBlockX() - radius, maxX = spawnLocation.getBlockX() + radius;
        int minZ = spawnLocation.getBlockZ() - radius, maxZ = spawnLocation.getBlockZ() + radius;
        int randomX = (int) (minX + Math.random() * (maxX - minX));
        int randomZ = (int) (minZ + Math.random() * (maxZ - minZ));
        return new Location(world, randomX, world.getHighestBlockYAt(randomX, randomZ) + 1, randomZ);
    }
}
