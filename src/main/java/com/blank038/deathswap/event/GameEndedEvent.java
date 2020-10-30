package com.blank038.deathswap.event;

import com.blank038.deathswap.game.GameArena;
import org.bukkit.entity.Player;

/**
 * @author Blank038
 */
public class GameEndedEvent extends GamePlayerEvent {
    private final boolean forced;

    public GameEndedEvent(Player player, GameArena arena, boolean forced) {
        super(player, arena);
        this.forced = forced;
    }

    public boolean isForced() {
        return forced;
    }
}