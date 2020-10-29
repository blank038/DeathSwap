package com.blank038.deathswap.enums;

import com.blank038.deathswap.DeathSwap;
import org.bukkit.ChatColor;

/**
 * @author Blank038
 */
public enum GameStatus {
    // 等待中
    WAITING,
    // 开始时
    STARTING,
    // 已开始
    STARTED,
    // 已结束
    END,
    // 出现异常
    ERROR;

    public String getStatusText() {
        return ChatColor.translateAlternateColorCodes('&',
                DeathSwap.getLangData().getString("message.arena-status." + name().toLowerCase(), false));
    }
}