package com.blank038.deathswap.nms;

import net.minecraft.server.v1_12_R1.PacketPlayOutWorldBorder;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * @author Blank038
 */
public class v1_12_2 implements NMSInterface {

    @Override
    public void sendWorldBorder(Player player, World world) {
        net.minecraft.server.v1_12_R1.WorldBorder worldBorder = new net.minecraft.server.v1_12_R1.WorldBorder();
        worldBorder.world = ((CraftWorld) world).getHandle();
        worldBorder.setCenter(world.getSpawnLocation().getBlockX(), world.getSpawnLocation().getBlockZ());
        worldBorder.setSize(world.getWorldBorder().getSize());
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutWorldBorder(worldBorder, PacketPlayOutWorldBorder.EnumWorldBorderAction.INITIALIZE));
    }
}