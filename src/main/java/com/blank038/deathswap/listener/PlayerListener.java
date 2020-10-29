package com.blank038.deathswap.listener;

import com.blank038.deathswap.DeathSwap;
import com.blank038.deathswap.enums.GameStatus;
import com.blank038.deathswap.game.GameArena;
import com.blank038.deathswap.game.data.PlayerInfoData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

/**
 * @author Blank038
 */
public class PlayerListener implements Listener {
    private final DeathSwap INSTANCE;

    public PlayerListener() {
        INSTANCE = DeathSwap.getInstance();
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (INSTANCE.getGameManager().hasPlayer(event.getPlayer().getUniqueId())) {

        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        PlayerInfoData.DATA_MAP.put(event.getPlayer().getName(), new PlayerInfoData(event.getPlayer()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PlayerInfoData data = PlayerInfoData.DATA_MAP.getOrDefault(event.getPlayer().getName(), null);
        if (data != null) {
            data.save();
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        GameArena arena = INSTANCE.getGameManager().getPlayerGame(event.getEntity().getUniqueId());
        if (arena != null) {
            event.setDeathMessage(null);
            arena.onDeath(event.getEntity());
        }
    }

    @EventHandler
    public void onEnttiyDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            GameArena arena = INSTANCE.getGameManager().getPlayerGame(event.getDamager().getUniqueId());
            if (arena != null && arena.getGameStatus() != GameStatus.STARTED) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            GameArena arena = INSTANCE.getGameManager().getPlayerGame(event.getEntity().getUniqueId());
            if (arena != null && arena.getGameStatus() != GameStatus.STARTED) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        GameArena arena = INSTANCE.getGameManager().getPlayerGame(event.getPlayer().getUniqueId());
        if (arena != null) {
            arena.onTeleport(event.getPlayer(), event);
        }
    }

    @EventHandler
    public void onPerformCommand(PlayerCommandPreprocessEvent event) {
        String ds = "/ds", dhs = "/deathswap";
        if (event.getMessage().startsWith(ds) || event.getMessage().startsWith(dhs)) {
            return;
        }
        GameArena arena = INSTANCE.getGameManager().getPlayerGame(event.getPlayer().getUniqueId());
        if (arena != null) {
            event.setCancelled(true);
        }
    }
}
