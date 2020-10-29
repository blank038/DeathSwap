package com.blank038.deathswap.game;

import com.blank038.deathswap.DeathSwap;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author Blank038
 */
public class TeleportManager {

    public static void teleportEndLocation(Player player, Location location) {
        String key = "game-option.bungee";
        if (DeathSwap.getInstance().getConfig().getBoolean(key)) {
            // 开始传送
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            try {
                out.writeUTF("Connect");
                out.writeUTF(DeathSwap.getInstance().getConfig().getString("game-option.lobby"));
                out.flush();
                player.sendPluginMessage(DeathSwap.getInstance(), "BungeeCord", b.toByteArray());
            } catch (IOException ex) {
                DeathSwap.getInstance().getLogger().info(player.getName() + " 传送大厅异常.");
            }
        } else {
            Chunk chunk = location.getChunk();
            if (!chunk.isLoaded()) {
                chunk.load();
            }
            player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
        }
    }
}