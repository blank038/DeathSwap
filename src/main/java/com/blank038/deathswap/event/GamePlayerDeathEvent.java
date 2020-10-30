package com.blank038.deathswap.event;

import com.blank038.deathswap.game.GameArena;
import org.bukkit.entity.Player;

/**
 * @author Blank038
 */
public class GamePlayerDeathEvent extends GamePlayerEvent {
    private final Player target;

    public GamePlayerDeathEvent(Player player, GameArena arena, Player killer) {
        super(player, arena);
        this.target = killer;
    }

    public Player getKiller() {
        return target;
    }
}
