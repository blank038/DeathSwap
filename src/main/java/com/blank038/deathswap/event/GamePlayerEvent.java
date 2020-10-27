package com.blank038.deathswap.event;

import com.blank038.deathswap.game.GameArena;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GamePlayerEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();
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
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}