package com.blank038.deathswap.event;

import com.blank038.deathswap.game.GameArena;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameJoinEvent extends Event implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private final Player player;
    private final GameArena arena;
    private boolean cancelled;

    public GameJoinEvent(Player player, GameArena arena) {
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
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
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