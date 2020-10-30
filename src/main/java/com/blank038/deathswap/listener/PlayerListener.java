package com.blank038.deathswap.listener;

import com.blank038.deathswap.DeathSwap;
import com.blank038.deathswap.enums.GameStatus;
import com.blank038.deathswap.event.GameEndedEvent;
import com.blank038.deathswap.event.GamePlayerDeathEvent;
import com.blank038.deathswap.game.GameArena;
import com.blank038.deathswap.game.data.PlayerInfoData;
import com.blank038.deathswap.game.data.PlayerTempData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.world.ChunkLoadEvent;

import java.util.Map;
import java.util.UUID;

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
        event.getRecipients().clear();
        if (INSTANCE.getGameManager().hasPlayer(event.getPlayer().getUniqueId())) {
            for (Map.Entry<UUID, PlayerTempData> r : INSTANCE.getGameManager().getPlayerGame(
                    event.getPlayer().getUniqueId()).getAllPlayerData().entrySet()
            ) {
                event.getRecipients().add(Bukkit.getPlayer(r.getKey()));
            }
        } else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!INSTANCE.getGameManager().hasPlayer(player.getUniqueId())) {
                    event.getRecipients().add(player);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        PlayerInfoData.DATA_MAP.put(event.getPlayer().getName(), new PlayerInfoData(event.getPlayer()));

        if (INSTANCE.getConfig().getBoolean("game-option.bungee")
                && INSTANCE.getGameManager().getBungeeArena() != null) {
            INSTANCE.getGameManager().submitJoin(event.getPlayer(), INSTANCE.getGameManager().getBungeeArena().getArenaKey());
        }
    }

    @EventHandler
    public void onServerMotd(ServerListPingEvent event) {
        if (INSTANCE.getConfig().getBoolean("game-option.bungee")) {
            GameArena arena = INSTANCE.getGameManager().getBungeeArena();
            GameStatus status = arena == null ? GameStatus.ERROR : arena.getGameStatus();
            event.setMotd(DeathSwap.getLangData().getString("message.motd." + status.name().toLowerCase(), false));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PlayerInfoData data = PlayerInfoData.DATA_MAP.getOrDefault(event.getPlayer().getName(), null);
        if (data != null) {
            data.save();
        }
        GameArena arena = INSTANCE.getGameManager().getPlayerGame(event.getPlayer().getUniqueId());
        if (arena != null) {
            arena.quit(event.getPlayer());
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

    @EventHandler
    public void onGameEnded(GameEndedEvent event) {
        if (event.isForced()) {
            return;
        }
        if (event.getArena().getWinnerData().getWinner().equals(event.getPlayer().getName())) {
            PlayerInfoData data = PlayerInfoData.DATA_MAP.getOrDefault(event.getPlayer().getName(), new PlayerInfoData(event.getPlayer()));
            data.setGames(data.getGames() + 1);
            data.setWin(data.getWin() + 1);
            data.save();
        }
    }

    @EventHandler
    public void onGamePlayerDeath(GamePlayerDeathEvent event) {
        Player player = event.getPlayer(), killer = event.getKiller();
        if (killer != null) {
            PlayerInfoData data = PlayerInfoData.DATA_MAP.getOrDefault(killer.getName(), null);
            if (data != null) {
                data.setKill(data.getKill() + 1);
            }
        }
        PlayerInfoData data = PlayerInfoData.DATA_MAP.getOrDefault(player.getName(), null);
        if (data != null) {
            data.setDeath(data.getDeath() + 1);
            data.setLose(data.getLose() + 1);
            data.setGames(data.getGames() + 1);
        }
    }

    @EventHandler
    public void onChunk(ChunkLoadEvent event) {
        if (!INSTANCE.getGameManager().hasWorld(event.getWorld().getName())) {
            return;
        }
        GameArena arena = INSTANCE.getGameManager().getArenaByWorld(event.getWorld().getName());
        if (arena != null && arena.getGameStatus() == GameStatus.STARTED) {
            arena.getBlockData().addChunk(event.getChunk());
        }
    }
}
