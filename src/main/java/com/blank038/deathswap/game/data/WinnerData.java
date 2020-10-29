package com.blank038.deathswap.game.data;

public class WinnerData {
    private final String name;
    private final int kill;

    public WinnerData(String name, int kill) {
        this.name = name;
        this.kill = kill;
    }

    public int getKill() {
        return kill;
    }

    public String getWinner() {
        return name;
    }
}
