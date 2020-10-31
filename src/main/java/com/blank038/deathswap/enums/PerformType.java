package com.blank038.deathswap.enums;

import com.blank038.deathswap.DeathSwap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author Blank038
 */
public enum PerformType {
    // 胜利
    WIN,
    // 失败
    LOSE,
    // 击杀敌人
    KILL;

    public void perform(Player player) {
        for (String text : DeathSwap.getInstance().getConfig().getStringList("event-reward." + name().toLowerCase())) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), text.replace("%player%", player.getName()));
        }
    }
}