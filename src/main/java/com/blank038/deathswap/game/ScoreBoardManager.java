package com.blank038.deathswap.game;

import com.blank038.deathswap.DeathSwap;
import com.blank038.deathswap.enums.GameStatus;
import com.blank038.deathswap.game.data.PlayerScoreBoard;
import com.blank038.deathswap.game.data.PlayerTempData;
import com.blank038.deathswap.game.data.WinnerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class ScoreBoardManager {
    private final GameArena game;
    private final HashMap<String, PlayerScoreBoard> playerMap = new HashMap<>();
    private final FileConfiguration data;
    private GameStatus gameStatus;

    public ScoreBoardManager(GameArena gameArena) {
        game = gameArena;
        // 读取计分板
        File file = new File(DeathSwap.getInstance().getDataFolder(), "scoreboards.yml");
        if (!file.exists()) DeathSwap.getInstance().saveResource("scoreboards.yml", true);
        data = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * 更新玩家计分板
     *
     * @param gameStatus 竞技场状态
     */
    public synchronized void sendPlayerScoreBroad(GameStatus gameStatus) {
        // 初始化时间格式
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(DeathSwap.getInstance().getConfig().getString("simple-date-format"));
        // 根据游戏状态发送计分板
        if (gameStatus != this.gameStatus) refresh();
        // 刷新玩家计分板
        switch (gameStatus) {
            case WAITING:
            case STARTING:
                for (Map.Entry<String, PlayerScoreBoard> entry : playerMap.entrySet()) {
                    Player player = Bukkit.getPlayer(entry.getKey());
                    if (player != null && player.isOnline()) {
                        List<String> list = formatWaitingList(player, sdf.format(date));
                        entry.getValue().updatePlayerScoreBoard(reColor(data.getString("waiting.title")), list);
                    }
                }
                break;
            case STARTED:
                for (Map.Entry<String, PlayerScoreBoard> entry : playerMap.entrySet()) {
                    Player player = Bukkit.getPlayer(entry.getKey());
                    if (player != null && player.isOnline()) {
                        List<String> list = formatGameList(player, sdf.format(date));
                        entry.getValue().updatePlayerScoreBoard(reColor(data.getString(gameStatus.name().toLowerCase() + ".title")), list);
                    }
                }
                break;
            case END:
                for (Map.Entry<String, PlayerScoreBoard> entry : playerMap.entrySet()) {
                    Player player = Bukkit.getPlayer(entry.getKey());
                    if (player != null && player.isOnline()) {
                        List<String> list = formatEndList(player, sdf.format(date));
                        entry.getValue().updatePlayerScoreBoard(reColor(data.getString(gameStatus.name().toLowerCase() + ".title")), list);
                    }
                }
                break;
            default:
                break;
        }
        this.gameStatus = gameStatus;
    }

    /**
     * 清理全部计分板玩家
     */
    public void clearScoreboard() {
        for (Map.Entry<String, PlayerScoreBoard> entry : new HashSet<>(playerMap.entrySet())) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null && player.isOnline()) {
                removePlayer(player);
            }
        }
        playerMap.clear();
    }

    /**
     * 增加玩家至计分板
     */
    public void addPlayer(Player player) {
        playerMap.put(player.getName(), new PlayerScoreBoard());
        playerMap.get(player.getName()).setPlayer(player);
    }

    /**
     * 刷新计分板
     */
    public void refresh() {
        for (Map.Entry<String, PlayerScoreBoard> entry : playerMap.entrySet()) {
            entry.getValue().setRefresh(true);
        }
    }

    /**
     * 将玩家移除计分板列表
     */
    public void removePlayer(Player player) {
        playerMap.remove(player.getName());
        player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
    }

    private List<String> formatWaitingList(Player player, String date) {
        // 开始设置将要使用的计分板内容
        List<String> result = new ArrayList<>();
        for (String text : data.getStringList("waiting.info")) {
            result.add(reColor(text).replace("%date%", date).replace("%map%", game.getArenaName())
                    .replace("%now%", String.valueOf(game.getPlayerCount())).replace("%max%", String.valueOf(game.getMax()))
                    .replace("%status%", game.getGameStatus().getStatusText().replace("%time%", String.valueOf(game.getWaitTime())))
                    .replace("%player%", player.getName()));
        }
        return result;
    }

    private List<String> formatGameList(Player player, String date) {
        // 获取玩家对局信息
        PlayerTempData tempPlayerData = game.getPlayerTempData(player.getUniqueId());
        // 获取玩家数据
        int kill = tempPlayerData == null ? 0 : tempPlayerData.getKillCount();
        // 开始设置将要使用的计分板内容
        List<String> result = new ArrayList<>();
        for (String text : data.getStringList("started.info")) {
            result.add(reColor(text).replace("%date%", date).replace("%kill%", String.valueOf(kill))
                    .replace("%now%", String.valueOf(game.getLivingPlayerCount())).replace("%swap%", String.valueOf(game.getSwapTime()))
                    .replace("%status%", game.getWorldBroadStatus()).replace("%player%", player.getName()));
        }
        return result;
    }

    private List<String> formatEndList(Player player, String date) {
        // 获取玩家对局信息
        WinnerData winnerData = game.getWinnerData();
        // 获取顽疾数据
        String winner = winnerData == null ? "???" : winnerData.getWinner();
        int kill = winnerData == null ? 0 : winnerData.getKill();
        // 开始设置将要使用的计分板内容
        List<String> result = new ArrayList<>();
        for (String text : data.getStringList("end.info")) {
            result.add(reColor(text).replace("%date%", date).replace("%kill%", String.valueOf(kill))
                    .replace("%winner%", winner).replace("%player%", player.getName()));
        }
        return result;
    }

    private String reColor(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
