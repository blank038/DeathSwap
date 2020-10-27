package com.blank038.deathswap.listener;

import com.blank038.deathswap.DeathSwap;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

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

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

    }

    @EventHandler
    public void onEnttiyDamageByEntity(EntityDamageByEntityEvent event) {

    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {

    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {

    }

    @EventHandler
    public void onPerformCommand(PlayerCommandPreprocessEvent event) {

    }
}
