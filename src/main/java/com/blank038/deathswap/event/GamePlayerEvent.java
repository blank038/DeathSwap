package com.blank038.deathswap.event;

import com.blank038.deathswap.game.GameArena;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Blank038
 */
public class GamePlayerEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final Player player;
    private final GameArena arena;

    public GamePlayerEvent(Player player, GameArena arena) {
        this.player = player;
        this.arena = arena;
    }

    public Player getPlayer() {
        return player;
    }

    public GameArena getArena() {
        return arena;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}