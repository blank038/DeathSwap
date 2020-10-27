package com.blank038.deathswap.event;

import com.blank038.deathswap.game.GameArena;
import org.bukkit.entity.Player;

public class GameEndedEvent extends GamePlayerEvent {

    public GameEndedEvent(Player player, GameArena arena) {
        super(player, arena);
    }
}