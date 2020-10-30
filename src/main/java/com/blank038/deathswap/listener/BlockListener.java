package com.blank038.deathswap.listener;

import com.blank038.deathswap.DeathSwap;
import com.blank038.deathswap.enums.GameStatus;
import com.blank038.deathswap.game.GameArena;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;

import java.util.UUID;

/**
 * @author Blank038
 */
public class BlockListener implements Listener {
    private final DeathSwap INSTANCE;

    public BlockListener() {
        this.INSTANCE = DeathSwap.getInstance();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (INSTANCE.getGameManager().hasPlayer(uuid)) {
            GameArena arena = INSTANCE.getGameManager().getPlayerGame(uuid);
            if (arena.getGameStatus() != GameStatus.STARTED) {
                event.setCancelled(true);
            } else {
                arena.getBlockData().addBreakBlock(event.getBlock());
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (INSTANCE.getGameManager().hasPlayer(uuid)) {
            GameArena arena = INSTANCE.getGameManager().getPlayerGame(uuid);
            if (arena.getGameStatus() != GameStatus.STARTED) {
                event.setCancelled(true);
            } else {
                arena.getBlockData().addPlaceBlock(event.getBlock());
            }
        }
    }

    @EventHandler
    public void onUseBunket(BlockBurnEvent event) {
        blockBreak(event, event.getBlock());
    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent event) {
        blockBreak(event, event.getBlock());
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
        blockBreak(event, event.getBlock());
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
        blockBreak(event, event.getBlock());
    }

    private void blockBreak(Cancellable cancellable, Block block) {
        String world = block.getWorld().getName();
        if (INSTANCE.getGameManager().hasWorld(world)) {
            GameArena arena = INSTANCE.getGameManager().getArenaByWorld(world);
            if (arena.getGameStatus() != GameStatus.STARTED) {
                cancellable.setCancelled(true);
            } else {
                arena.getBlockData().addBreakBlock(block);
            }
        }
    }
}
