package com.blank038.deathswap.event;

import com.blank038.deathswap.game.GameArena;
import org.bukkit.entity.Player;

/**
 * @author Blank038
 */
public class GameQuitEvent extends GamePlayerEvent {

    public GameQuitEvent(Player player, GameArena arena) {
        super(player, arena);
    }
}