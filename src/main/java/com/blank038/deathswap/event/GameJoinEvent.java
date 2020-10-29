package com.blank038.deathswap.event;

import com.blank038.deathswap.game.GameArena;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

/**
 * @author Blank038
 */
public class GameJoinEvent extends GamePlayerEvent implements Cancellable {
    private boolean cancelled;

    public GameJoinEvent(Player player, GameArena arena) {
        super(player, arena);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}