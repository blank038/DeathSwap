package com.blank038.deathswap.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerListener implements Listener {

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
