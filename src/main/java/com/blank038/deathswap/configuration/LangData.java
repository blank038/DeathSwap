package com.blank038.deathswap.configuration;

import com.blank038.deathswap.DeathSwap;
import com.blank038.deathswap.util.CoreUtil;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class LangData {
    private final DeathSwap INSTANCE;
    private FileConfiguration defLang, secLang;

    public LangData(DeathSwap inst) {
        INSTANCE  = inst;
        init();
    }

    public void init() {
        // start check lang directory and lang file.
        File path = new File(INSTANCE.getDataFolder(), "lang");
        if (!path.exists()) path.mkdir();
        File defFile = new File(path, "zh_CN.yml");
        if (!defFile.exists()) {
            CoreUtil.outputFile(INSTANCE.getResource("/lang/zh_CN.yml"), defFile);
        }
        secLang = YamlConfiguration.loadConfiguration(defFile);
        // start to load config option lang file.
        String lang = INSTANCE.getConfig().getString("lang");
        File langFile = new File(path, lang + ".yml");
        if (!langFile.exists()) {
            CoreUtil.outputFile(INSTANCE.getResource("/lang/" + lang + ".yml"), langFile);
        }
        defLang = YamlConfiguration.loadConfiguration(langFile);
    }

    public String getString(String key, boolean prefix) {
        String header = prefix ? defLang.getString("message.prefix",
                secLang.getString("message.prefix")) : "";
        return ChatColor.translateAlternateColorCodes('&',
                header + defLang.getString(key, secLang.getString(key)));
    }
}
