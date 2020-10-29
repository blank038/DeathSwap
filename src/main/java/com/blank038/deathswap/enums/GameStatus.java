package com.blank038.deathswap.enums;

import com.blank038.deathswap.DeathSwap;
import org.bukkit.ChatColor;

public enum GameStatus {
    WAITING,
    STARTING,
    STARTED,
    END,
    ERROR;

    public String getStatusText() {
        return ChatColor.translateAlternateColorCodes('&',
                DeathSwap.getLangData().getString("message.arena-status." + name().toLowerCase(), false));
    }
}