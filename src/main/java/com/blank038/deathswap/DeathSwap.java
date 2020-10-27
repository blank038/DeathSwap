package com.blank038.deathswap;

import com.blank038.deathswap.command.MainCommand;
import com.blank038.deathswap.configuration.LangData;
import com.blank038.deathswap.listener.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * An like UHC(Survival Game) minigame plugin.
 *
 * @author Blank038, Laotou
 */
public class DeathSwap extends JavaPlugin {
    private static DeathSwap inst;
    private static LangData langData;

    public static DeathSwap getInstance() {
        return inst;
    }

    public static LangData getLangData() {
        return langData;
    }

    /**
     * Initialize DeathSwap.
     */
    @Override
    public void onEnable() {
        inst = this;
        // load config.
        loadConfig();
        // register plugin listener.
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        // set plugin command executor.
        getCommand("deathswap").setExecutor(new MainCommand());
    }

    /**
     * Configuration file initialize and reload.
     */
    public void loadConfig() {
        getDataFolder().mkdir();
        saveDefaultConfig();
        reloadConfig();
        // variable langData initialize.
        if (langData == null) langData = new LangData(this);
        else langData.init();
    }
}