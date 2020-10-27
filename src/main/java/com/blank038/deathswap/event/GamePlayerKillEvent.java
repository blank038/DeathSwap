package com.blank038.deathswap.event;

import com.blank038.deathswap.game.GameArena;
import org.bukkit.entity.Player;

public class GamePlayerKillEvent extends GamePlayerEvent {
    private final Player target;

    public GamePlayerKillEvent(Player player, GameArena arena, Player target) {
        super(player, arena);
        this.target = target;
    }

    public Player getEntity() {
        return target;
    }
}
