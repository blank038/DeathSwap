package com.blank038.deathswap;

import com.blank038.deathswap.command.MainCommand;
import com.blank038.deathswap.listener.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class DeathSwap extends JavaPlugin {
    private static DeathSwap inst;

    public static DeathSwap getInstance() {
        return inst;
    }

    @Override
    public void onEnable() {
        inst = this;
        // 载入配置文件
        loadConfig();
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        getCommand("deathswap").setExecutor(new MainCommand());
    }

    public void loadConfig() {
        getDataFolder().mkdir();
        saveDefaultConfig();
        reloadConfig();
    }
}