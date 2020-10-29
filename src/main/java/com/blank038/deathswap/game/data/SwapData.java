package com.blank038.deathswap.game.data;

import java.util.HashMap;

/**
 * @author Blank038
 */
public class SwapData {
    private final HashMap<String, String> swapMap = new HashMap<>();

    public void reset() {
        swapMap.clear();
    }

    public void add(String name, String target) {
        swapMap.put(name, target);
    }

    public void remove(String name) {
        swapMap.remove(swapMap.remove(name));
    }

    public boolean hasPlayer(String name) {
        return swapMap.containsKey(name);
    }

    public String getTarget(String name) {
        return swapMap.get(name);
    }
}
