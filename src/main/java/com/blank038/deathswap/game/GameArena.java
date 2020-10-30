package com.blank038.deathswap.game;

import com.blank038.deathswap.DeathSwap;
import com.blank038.deathswap.enums.EditorType;
import com.blank038.deathswap.enums.GameLocType;
import com.blank038.deathswap.enums.GameStatus;
import com.blank038.deathswap.event.*;
import com.blank038.deathswap.game.data.BlockData;
import com.blank038.deathswap.game.data.PlayerTempData;
import com.blank038.deathswap.game.data.SwapData;
import com.blank038.deathswap.game.data.WinnerData;
import net.minecraft.server.v1_12_R1.PacketPlayOutWorldBorder;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author Blank038
 */
public class GameArena {
    private final File file;
    private final HashMap<UUID, PlayerTempData> playerMap = new HashMap<>();
    /**
     * 当前竞技场内玩家列表
     */
    private final List<UUID> gamePlayers = new ArrayList<>(), allowTpList = new ArrayList<>();
    private final String world, arenaKey;
    private int borderInterval, teleportInterval;
    private String arenaName;
    private int min, max, size;
    private GameStatus status = GameStatus.ERROR;
    private GameLocType gameLocType;
    private Location waitLoc, endLoc;
    /**
     * 下方全为游戏临时数据
     */
    private int waitTime, tempSize, borderTime, swapTime;
    private BukkitTask startingTask, borderTask, tpTask;
    private WinnerData winnerData;
    private ScoreBoardManager scoreBoardManager;
    private SwapData swapData;
    private BlockData blockData;
    private boolean firstSwap;


    public GameArena(File file) {
        this.file = file;
        arenaKey = file.getName().replace(".yml", "");

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

        if (waitLoc != null && endLoc != null && gameLocType != null && min >= 2 && size > 0) {
            init();
        }
    }

    /**
     * 初始化竞技场
     */
    public void init() {
        status = GameStatus.WAITING;

        waitTime = DeathSwap.getInstance().getConfig().getInt("arena-option.wait-time");
        tempSize = size;
        swapTime = teleportInterval;
        borderTime = borderInterval;

        gamePlayers.clear();
        playerMap.clear();
        allowTpList.clear();

        // 结束线程
        if (startingTask != null) {
            startingTask.cancel();
        }
        if (borderTask != null) {
            borderTask.cancel();
        }
        if (tpTask != null) {
            tpTask.cancel();
        }
        if (swapData != null) {
            swapData.reset();
        }

        if (scoreBoardManager == null) {
            scoreBoardManager = new ScoreBoardManager(this);
        } else {
            scoreBoardManager.clearScoreboard();
        }

        if (blockData != null) {
            blockData.reset();
        }
        blockData = new BlockData(Bukkit.getWorld(world));
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

    public String getGameWorld() {
        return world;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public int getWorldBorderSize() {
        return size;
    }

    public int getPlayerCount() {
        return playerMap.size();
    }

    public int getWaitTime() {
        return waitTime;
    }

    public int getSwapTime() {
        return swapTime;
    }

    public int getLivingPlayerCount() {
        return gamePlayers.size();
    }

    public boolean hasPlayer(Player player) {
        return playerMap.containsKey(player.getUniqueId());
    }

    public PlayerTempData getPlayerTempData(UUID uuid) {
        return playerMap.getOrDefault(uuid, null);
    }

    public WinnerData getWinnerData() {
        return winnerData;
    }

    public ScoreBoardManager getScoreBoardManager() {
        return scoreBoardManager;
    }

    public GameStatus getGameStatus() {
        return status;
    }

    public BlockData getBlockData() {
        return blockData;
    }

    /**
     * 获取边界状态
     *
     * @return 边界状态
     */
    public String getWorldBroadStatus() {
        WorldBorder world = Bukkit.getWorld(this.world).getWorldBorder();
        if (world.getSize() <= tempSize) {
            return DeathSwap.getLangData().getString("message.world-broad-status.wait", false).replace("%time%", String.valueOf(borderTime));
        } else {
            return DeathSwap.getLangData().getString("message.world-broad-status.run", false);
        }
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
        World world = Bukkit.getWorld(this.world);
        if (world == null) {
            player.sendMessage(DeathSwap.getLangData().getString("message.game-status.error", true));
            return false;
        }
        // 唤起事件
        GameJoinEvent event = new GameJoinEvent(player, this);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }
        // 传送玩家
        Chunk chunk = waitLoc.getChunk();
        if (!chunk.isLoaded()) {
            chunk.load();
        }
        allowTpList.add(player.getUniqueId());
        player.teleport(waitLoc, PlayerTeleportEvent.TeleportCause.PLUGIN);
        // 创建玩家临时数据
        playerMap.put(player.getUniqueId(), new PlayerTempData(player));
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setExp(0);
        player.setLevel(0);
        player.sendMessage(DeathSwap.getLangData().getString("message.join", true)
                .replace("%now%", String.valueOf(playerMap.size())).replace("%max%", String.valueOf(max)));
        scoreBoardManager.addPlayer(player);
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
            gamePlayers.remove(player.getUniqueId());
            playerMap.remove(player.getUniqueId()).restore();
            scoreBoardManager.removePlayer(player);
            DeathSwap.getInstance().getGameManager().removePlayer(player.getUniqueId());
            TeleportManager.teleportEndLocation(player, endLoc);
            checkStatus();

            // 唤起事件
            GameQuitEvent event = new GameQuitEvent(player, this);
            Bukkit.getPluginManager().callEvent(event);

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
        if (gamePlayers.contains(player.getUniqueId())) {
            // 判断击杀者
            Player killer = player.getKiller();
            if (killer == null && swapData.hasPlayer(player.getName())) {
                String target = swapData.getTarget(player.getName());
                killer = Bukkit.getPlayer(target);
                swapData.remove(player.getName());
                killer.sendMessage(DeathSwap.getLangData().getString("message.kill-message.swap", true)
                        .replace("%player%", player.getName()));
                sendAllPlayerText(DeathSwap.getLangData().getString("message.kill-message.swap-notify", true)
                        .replace("%target%", target).replace("%player%", player.getName()));
            } else if (killer != null && killer.isOnline() && gamePlayers.contains(killer.getUniqueId())
                    && playerMap.containsKey(killer.getUniqueId())) {
                playerMap.get(killer.getUniqueId()).addKill();
                killer.sendMessage(DeathSwap.getLangData().getString("message.kill-message.damage", true)
                        .replace("%player%", player.getName()));
                sendAllPlayerText(DeathSwap.getLangData().getString("message.kill-message.damage-notify", true)
                        .replace("%target%", player.getName()).replace("%player%", killer.getName()));
            } else if (killer == null && !swapData.hasPlayer(player.getName())) {
                sendAllPlayerText(DeathSwap.getLangData().getString("message.kill-message.die", true)
                        .replace("%player%", player.getName()));
            }

            // 唤起事件
            GamePlayerDeathEvent event = new GamePlayerDeathEvent(player, this, killer);
            Bukkit.getPluginManager().callEvent(event);

            // 踢出玩家
            quit(player);
        }
    }

    /**
     * 玩家传送事件
     */
    public void onTeleport(Player player, PlayerTeleportEvent event) {
        if (allowTpList.contains(player.getUniqueId())) {
            if (firstSwap) {
                sendWorldBoardPacket(player, event.getTo().getWorld());
            }
            allowTpList.remove(player.getUniqueId());
        } else {
            event.setCancelled(true);
        }
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
                scoreBoardManager.refresh();
            }
        } else if (status == GameStatus.STARTED && gamePlayers.size() == 1) {
            normalEnd(gamePlayers.get(0));
        } else if (status == GameStatus.WAITING && playerMap.size() >= min) {
            status = GameStatus.STARTING;
            scoreBoardManager.refresh();
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

    public void sendScoreBoardPacket() {
        if (scoreBoardManager != null) {
            scoreBoardManager.sendPlayerScoreBroad(status);
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
            // 初始化临时数据
            tempSize = size;
            swapTime = teleportInterval;
            borderTime = borderInterval;
            swapData = new SwapData();
            firstSwap = true;
            // 设置世界出生点及世界边界大小
            World w = Bukkit.getWorld(world);
            WorldBorder border = w.getWorldBorder();
            border.setCenter(getInitLocation(w, 0));
            border.setSize(tempSize);
            // 游戏房间
            for (Map.Entry<UUID, PlayerTempData> entry : playerMap.entrySet()) {
                Player player = Bukkit.getPlayer(entry.getKey());
                Location rc = randomLocation((tempSize / 2));
                allowTpList.add(player.getUniqueId());
                gamePlayers.add(player.getUniqueId());
                player.teleport(rc, PlayerTeleportEvent.TeleportCause.PLUGIN);
                Bukkit.getScheduler().runTaskLater(DeathSwap.getInstance(), () -> sendWorldBoardPacket(player, rc.getWorld()), 20L);
            }
            // 开启缩圈线程
            borderTask = Bukkit.getScheduler().runTaskTimerAsynchronously(DeathSwap.getInstance(), () -> {
                if (tempSize <= 5) {
                    return;
                }
                World world = Bukkit.getWorld(this.world);
                WorldBorder worldBorder = world.getWorldBorder();
                if (worldBorder.getSize() > tempSize) {
                    return;
                }
                if (borderTime == 0) {
                    // 设置边界缩圈时间
                    borderTime = borderInterval;
                    tempSize /= 2;
                    // 计算缩圈
                    worldBorder.setWarningDistance(0);
                    worldBorder.setDamageAmount(1);
                    worldBorder.setSize(tempSize, 60);
                }
                borderTime--;
            }, 20L, 20L);
            // 开始玩家之间互换位置的线程
            tpTask = Bukkit.getScheduler().runTaskTimerAsynchronously(DeathSwap.getInstance(), () -> {
                if (swapTime == 0) {
                    swapTime = teleportInterval;
                    swapData.reset();
                    Collections.shuffle(gamePlayers);
                    for (int i = 1; i < gamePlayers.size(); i += 2) {
                        Player p1 = Bukkit.getPlayer(gamePlayers.get(i)), p2 = Bukkit.getPlayer(gamePlayers.get(i - 1));
                        swapData.add(p1.getName(), p2.getName());
                        swapData.add(p2.getName(), p1.getName());
                        allowTpList.add(p1.getUniqueId());
                        allowTpList.add(p2.getUniqueId());
                        // 开始传送
                        Bukkit.getScheduler().runTask(DeathSwap.getInstance(), () -> {
                            Location l1 = p1.getLocation().clone(), l2 = p2.getLocation().clone();
                            p1.teleport(l2, PlayerTeleportEvent.TeleportCause.PLUGIN);
                            p2.teleport(l1, PlayerTeleportEvent.TeleportCause.PLUGIN);
                        });
                    }
                    if (teleportInterval > 10) {
                        Bukkit.getScheduler().runTaskLater(DeathSwap.getInstance(), () -> swapData.reset(), 200L);
                    }
                    return;
                }
                if (swapTime <= 10) {
                    sendAllPlayerText(DeathSwap.getLangData().getString("message.tp-countdown", true)
                            .replace("%time%", String.valueOf(swapTime)));
                }
                swapTime--;
            }, 20L, 20L);
            firstSwap = false;
        }
    }

    /**
     * 游戏正常结束
     */
    public void normalEnd(UUID uuid) {
        status = GameStatus.END;

        Player winner = Bukkit.getPlayer(uuid);
        winnerData = new WinnerData(winner.getName(), playerMap.get(uuid).getKillCount());

        playerMap.remove(uuid).restore();
        allowTpList.add(uuid);
        DeathSwap.getInstance().getGameManager().removePlayer(uuid);
        winner.teleport(endLoc, PlayerTeleportEvent.TeleportCause.PLUGIN);

        // 唤起事件
        GameEndedEvent event = new GameEndedEvent(winner, this, false);
        Bukkit.getPluginManager().callEvent(event);

        Bukkit.getScheduler().runTaskLater(DeathSwap.getInstance(), this::init, 100L);
    }

    /**
     * 强制结束游戏
     */
    public void forceEnd() {
        status = GameStatus.END;

        // 唤起事件
        GameEndedEvent event = new GameEndedEvent(null, this, true);
        Bukkit.getPluginManager().callEvent(event);

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

    public void sendWorldBoardPacket(Player player, World world) {
        net.minecraft.server.v1_12_R1.WorldBorder worldBorder = new net.minecraft.server.v1_12_R1.WorldBorder();
        worldBorder.world = ((CraftWorld) world).getHandle();
        worldBorder.setCenter(world.getSpawnLocation().getBlockX(), world.getSpawnLocation().getBlockZ());
        worldBorder.setSize(Bukkit.getWorld(this.world).getWorldBorder().getSize());
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutWorldBorder(worldBorder, PacketPlayOutWorldBorder.EnumWorldBorderAction.INITIALIZE));
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
                teleportInterval = Integer.parseInt(String.valueOf(object));
                setArenaConfig("tp-interval", teleportInterval);
                break;
            case NAME:
                arenaName = ChatColor.translateAlternateColorCodes('&', (String) object);
                setArenaConfig("display-name", arenaName);
                break;
            case SIZE:
                size = Integer.parseInt(String.valueOf(object));
                setArenaConfig("size", size);
                break;
            case TYPE:
                gameLocType = Integer.parseInt(String.valueOf(object)) == 0 ? GameLocType.RANDOM : GameLocType.SPAWN_LOC;
                setArenaConfig("loc-type", gameLocType.name());
                break;
            case WBIV:
                borderInterval = Integer.parseInt(String.valueOf(object));
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

    public HashMap<UUID, PlayerTempData> getAllPlayerData() {
        return playerMap;
    }

    private Location getInitLocation(World world, int count) {
        Location location;
        if (gameLocType == GameLocType.SPAWN_LOC) {
            location = world.getSpawnLocation();
        } else {
            double rx = Math.random() * 100000, rz = Math.random() * 100000;
            location = new Location(world, rx, world.getHighestBlockYAt((int) rx, (int) rz), rz);
        }
        Chunk chunk = location.getChunk();
        if (!chunk.isLoaded()) {
            chunk.load();
        }
        if (world.getBiome(location.getBlockX(), location.getBlockZ()) != Biome.OCEAN || count == 10) {
            return location;
        }
        return getInitLocation(world, count + 1);
    }

    public Location randomLocation(int radius) {
        World world = Bukkit.getWorld(this.world);
        Location spawnLocation = world.getWorldBorder().getCenter().clone();
        int fr = -radius;
        int randomX = (int) (fr + Math.random() * (radius - fr));
        int randomZ = (int) (fr + Math.random() * (radius - fr));
        int lastX = randomX + spawnLocation.getBlockX(), lastZ = randomZ + spawnLocation.getBlockZ();
        return new Location(world, lastX, world.getHighestBlockYAt(lastX, lastZ) + 1, lastZ);
    }

    public String getArenaKey() {
        return arenaKey;
    }
}
