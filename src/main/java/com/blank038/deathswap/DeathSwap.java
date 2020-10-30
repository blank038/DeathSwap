package com.blank038.deathswap;

import com.blank038.deathswap.api.DeathSwapApi;
import com.blank038.deathswap.command.MainCommand;
import com.blank038.deathswap.configuration.LangData;
import com.blank038.deathswap.game.GameArena;
import com.blank038.deathswap.game.GameManager;
import com.blank038.deathswap.game.ScoreBoardManager;
import com.blank038.deathswap.listener.BlockListener;
import com.blank038.deathswap.listener.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Map;

/**
 * An similar to UHC(Survival Game) minigame plugin.
 * 一个类似于 UHC(生存游戏) 的小游戏插件.
 *
 * @author Blank038, Laotou
 */
public class DeathSwap extends JavaPlugin {
    private static DeathSwap inst;
    private static LangData langData;
    private GameManager gameManager;
    private DeathSwapApi api;

    public static DeathSwap getInstance() {
        return inst;
    }

    public static LangData getLangData() {
        return langData;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public DeathSwapApi getApi() {
        return api;
    }

    /**
     * 初始化插件
     */
    @Override
    public void onEnable() {
        inst = this;
        api = new DeathSwapApi();
        // 载入配置文件
        loadConfig();
        // 注册命令执行器
        getCommand("deathswap").setExecutor(new MainCommand());
        //载入竞技场管理器
        gameManager = new GameManager();
        // 计分板线程
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            for (Map.Entry<String, GameArena> entry : gameManager.allGame().entrySet()) {
                entry.getValue().sendScoreBoardPacket();
            }
        }, 5L, 5L);
        // 注册事件监听器
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockListener(), this);
    }

    @Override
    public void onDisable() {
        for (Map.Entry<String, GameArena> entry : gameManager.allGame().entrySet()) {
            ScoreBoardManager sbm = entry.getValue().getScoreBoardManager();
            if (sbm != null) {
                sbm.clearScoreboard();
            }
        }
    }

    /**
     * 配置文件初始化和重载
     */
    public void loadConfig() {
        getDataFolder().mkdir();
        saveDefaultConfig();
        reloadConfig();
        // 变量 loadData 初始化
        if (langData == null) {
            langData = new LangData(this);
        } else {
            langData.init();
        }

        new File(getDataFolder(), "data").mkdir();

        // 重载竞技场
        if (gameManager != null) {
            gameManager.loadGameArena();
        }
    }
}