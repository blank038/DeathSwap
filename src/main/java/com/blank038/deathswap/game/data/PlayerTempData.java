package com.blank038.deathswap.game.data;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 玩家数据备份类
 */
public class PlayerTempData {
    private final UUID uuid;
    private final HashMap<Integer, ItemStack> oldInvItems = new HashMap<>();
    // 玩家等级、饱食度等级
    private final int level, foodLevel;
    // 剩余血量、最大血量
    private final double health, maxHealth;
    // 玩家经验
    private final float exp;

    public PlayerTempData(Player player) {
        this.uuid = player.getUniqueId();
        exp = player.getExp();
        level = player.getLevel();
        foodLevel = player.getFoodLevel();
        health = player.getHealth();
        maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        // 备份玩家背包
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack itemStack = player.getInventory().getItem(i);
            if (itemStack == null || itemStack.getType() == Material.AIR) continue;
            oldInvItems.put(i, itemStack.clone());
        }
        player.getInventory().clear();
    }

    public UUID getPlayerUUID() {
        return uuid;
    }

    public void restore() {
        Player player = Bukkit.getPlayer(getPlayerUUID());
        if (player != null && player.isOnline()) {
            player.setLevel(level);
            player.setExp(exp);
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth);
            player.setHealth(health);
            player.setFoodLevel(foodLevel);
            // 恢复背包
            player.getInventory().clear();
            for (Map.Entry<Integer, ItemStack> entry : oldInvItems.entrySet()) {
                if (entry.getValue() == null) continue;
                player.getInventory().setItem(entry.getKey(), entry.getValue());
            }
        }
    }
}