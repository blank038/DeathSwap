package com.blank038.deathswap.game;

import com.blank038.deathswap.DeathSwap;
import com.blank038.deathswap.enums.EditorType;
import com.blank038.deathswap.enums.GameLocType;
import com.blank038.deathswap.enums.GameStatus;
import com.blank038.deathswap.game.data.PlayerTempData;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class GameArena {
    private final File file;
    private final HashMap<UUID, PlayerTempData> playerMap = new HashMap<>();
    // 当前竞技场内玩家列表
    private final List<UUID> gamePlayers = new ArrayList<>();
    private final String world;
    private int borderInterval, teleportInterval;
    private String arenaName;
    private int min;
    private int max;
    private int size;
    private GameStatus status;
    private GameLocType gameLocType;
    private Location waitLoc, endLoc;
    // 下方全为游戏临时数据
    private int waitTime, tempSize, borderTime;
    private BukkitTask startingTask, borderTask;


    public GameArena(File file) {
        this.file = file;
        FileConfiguration data = YamlConfiguration.loadConfiguration(file);

        min = data.getInt("min");
        max = data.getInt("max");
        world = data.getString("world");
        size = data.getInt("size");
        arenaName = ChatColor.translateAlternateColorCodes('&',
                data.getString("display-name"));
        borderInterval = data.getInt("wb-interval");
        teleportInterval = data.getInt("tp-interval");

        if (data.contains("loc-type")) {
            gameLocType = GameLocType.valueOf(data.getString("loc-type"));
        }
        loadLoc(data);

        World world = Bukkit.getWorld(this.world);

        if (world != null && waitLoc != null && endLoc != null && gameLocType != null && min >= 2) init();
    }

    /**
     * 初始化竞技场
     */
    public void init() {
        status = GameStatus.WAITING;
        // 设置世界边界大小
        World world = Bukkit.getWorld(this.world);
        world.getWorldBorder().setSize(size);
        world.getWorldBorder().setCenter(getInitLocation(world));
        // 结束线程
        if (startingTask != null) startingTask.cancel();
        if (borderTask != null) borderTask.cancel();
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
        checkStatus();
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
            gamePlayers.remove(player.getUniqueId());
            playerMap.remove(player.getUniqueId());
            TeleportManager.teleportEndLocation(player, endLoc);
            checkStatus();
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
     * 检测房间状态
     */
    public void checkStatus() {
        if (status == GameStatus.STARTING) {
            if (playerMap.size() < min) {
                waitTime = DeathSwap.getInstance().getConfig().getInt("arena-option.wait-time");
                startingTask.cancel();
                status = GameStatus.WAITING;
            }
        } else if (status == GameStatus.STARTED && gamePlayers.size() == 1) {
            normalEnd(gamePlayers.get(0));
        } else if (status == GameStatus.WAITING && playerMap.size() >= min) {
            status = GameStatus.STARTING;
            waitTime = DeathSwap.getInstance().getConfig().getInt("arena-option.wait-time");
            startingTask = Bukkit.getScheduler().runTaskTimerAsynchronously(DeathSwap.getInstance(), () -> {
                if (waitTime <= 0) {
                    Bukkit.getScheduler().runTask(DeathSwap.getInstance(), this::start);
                    startingTask.cancel();
                    return;
                }
                if (waitTime <= 5) {
                    sendAllPlayerText(DeathSwap.getLangData().getString("message.game-starting", true)
                            .replace("%time%", String.valueOf(waitTime)));
                }
                waitTime--;
            }, 20L, 20L);
        }
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
            border.setSize(size);
            // 游戏房间
            for (Map.Entry<UUID, PlayerTempData> entry : playerMap.entrySet()) {
                Player player = Bukkit.getPlayer(entry.getKey());
                Location rc = randomLocation(size - 1);
                player.teleport(rc, PlayerTeleportEvent.TeleportCause.PLUGIN);
                gamePlayers.add(player.getUniqueId());
            }
            tempSize = size;
            // 开启缩圈线程
            borderTask = Bukkit.getScheduler().runTaskTimerAsynchronously(DeathSwap.getInstance(), () -> {
                if (size <= 5) return;
                World world = Bukkit.getWorld(this.world);
                WorldBorder worldBorder = world.getWorldBorder();
                if (worldBorder.getSize() > tempSize) return;
                if (borderTime == 0) {
                    // 设置边界缩圈时间
                    borderTime = borderInterval;
                    // 计算缩圈
                    worldBorder.setWarningDistance(0);
                    worldBorder.setDamageAmount(2);
                    worldBorder.setSize(tempSize / 2.0, borderInterval);
                    size /= 2;
                }
                borderTime--;
            }, 20L, 20L);
        }
    }

    /**
     * 游戏正常结束
     */
    public void normalEnd(UUID uuid) {
        Player winner = Bukkit.getPlayer(uuid);
    }

    /**
     * 强制结束游戏
     */
    public void forceEnd() {
        for (Map.Entry<UUID, PlayerTempData> entry : new HashSet<>(playerMap.entrySet())) {
            entry.getValue().restore();
            playerMap.remove(entry.getKey());
            Player player = Bukkit.getPlayer(entry.getKey());
            TeleportManager.teleportEndLocation(player, endLoc);
        }
        gamePlayers.clear();
        playerMap.clear();
        waitTime = DeathSwap.getInstance().getConfig().getInt("arena-option.wait-time");
    }

    /**
     * 编辑竞技场信息
     *
     * @param type   编辑类型
     * @param object 编辑数据
     */
    public void editorData(EditorType type, Object object) {
        switch (type) {
            case MIN:
                min = (int) object;
                setArenaConfig("min", object);
                break;
            case MAX:
                max = (int) object;
                setArenaConfig("max", object);
                break;
            case END:
                endLoc = ((Location) object).clone();
                setArenaConfig("end-pos", locationToSection(endLoc));
                break;
            case WAIT:
                waitLoc = ((Location) object).clone();
                setArenaConfig("wait-pos", locationToSection(waitLoc));
                break;
            case TIV:
                teleportInterval = (int) object;
                setArenaConfig("tp-interval", teleportInterval);
                break;
            case NAME:
                arenaName = ChatColor.translateAlternateColorCodes('&', (String) object);
                setArenaConfig("display-name", arenaName);
                break;
            case SIZE:
                size = (int) object;
                setArenaConfig("size", size);
                break;
            case TYPE:
                gameLocType = (int) object == 0 ? GameLocType.RANDOM : GameLocType.SPAWN_LOC;
                setArenaConfig("loc-type", gameLocType.name());
                break;
            case WBIV:
                borderInterval = (int) object;
                setArenaConfig("wb-interval", borderInterval);
                break;
            default:
                break;
        }
    }

    public void setArenaConfig(String key, Object obj) {
        FileConfiguration data = YamlConfiguration.loadConfiguration(file);
        data.set(key, obj);
        try {
            data.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ConfigurationSection locationToSection(Location location) {
        ConfigurationSection section = new YamlConfiguration();
        section.set("world", location.getWorld().getName());
        section.set("x", location.getX());
        section.set("y", location.getY());
        section.set("z", location.getZ());
        section.set("yaw", location.getYaw());
        section.set("pitch", location.getPitch());
        return section;
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
