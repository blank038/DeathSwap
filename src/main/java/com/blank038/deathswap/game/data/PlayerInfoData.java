package com.blank038.deathswap.game.data;

import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * @author Blank038
 */
public class PlayerInfoData {
    public static final HashMap<String, PlayerInfoData> DATA_MAP = new HashMap<>();
    private int win, lose, kill, death;

    public PlayerInfoData(Player player) {

    }

    public void save() {

    }
}
