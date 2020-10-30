package com.blank038.deathswap.game.data;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Monster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Blank038
 */
public class BlockData {
    private final HashMap<Block, Material> blockMaterial = new HashMap<>();
    private final HashMap<Block, Byte> blockData = new HashMap<>();
    private final List<Block> placedBlocks = new ArrayList<>();
    private final List<Chunk> chunks = new ArrayList<>();
    private final World world;

    public BlockData(World world) {
        this.world = world;
    }

    public void reset() {
        // 载入区块
        for (Chunk chunk : chunks) {
            if (!chunk.isLoaded()) {
                chunk.load();
            }
        }
        // 复原方块
        for (Block block : placedBlocks) {
            world.getBlockAt(block.getLocation()).setType(Material.AIR);
        }
        for (Map.Entry<Block, Material> entry : blockMaterial.entrySet()) {
            Block at = world.getBlockAt(entry.getKey().getLocation());
            at.setType(entry.getValue());
            at.setData(blockData.get(entry.getKey()));
        }
        // 清理掉落物
        List<Entity> entities = new ArrayList<>();
        for (Chunk chunk : world.getLoadedChunks()) {
            for (Entity entity : chunk.getEntities()) {
                if (entity instanceof Monster || entity instanceof Arrow || entity instanceof Item) {
                    entities.add(entity);
                }
            }
        }
        for (Entity i : new ArrayList<>(entities)) {
            i.remove();
        }
    }

    public void addPlaceBlock(Block block) {
        placedBlocks.add(block);
    }

    public void addBreakBlock(Block block) {
        blockMaterial.put(block, block.getType());
        blockData.put(block, block.getData());
    }

    public void addChunk(Chunk chunk) {
        chunks.add(chunk);
    }
}
