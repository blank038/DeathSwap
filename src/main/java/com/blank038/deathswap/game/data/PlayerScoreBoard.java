package com.blank038.deathswap.game.data;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Blank038
 */
public class PlayerScoreBoard {
    private final List<Team> teams = new ArrayList<>();
    private Scoreboard scoreboard;
    private Objective objective;
    private boolean refresh;

    public PlayerScoreBoard() {
        create();
    }

    public void updatePlayerScoreBoard(String title, List<String> source) {
        objective.setDisplayName(title);
        List<String> list = new ArrayList<>();
        for (String i : source) {
            list.add(i.length() > 32 ? i.substring(0, 32) : i);
        }
        if (refresh) {
            scoreboard.getTeams().forEach(Team::unregister);
            teams.clear();
        }
        // 判断是否第一次设置
        if (scoreboard.getTeams().size() == 0) {
            for (int i = 0; i < list.size(); i++) {
                Team team = scoreboard.registerNewTeam(String.valueOf(i));
                team.addEntry(ChatColor.values()[i].toString());
                objective.getScore(ChatColor.values()[i].toString()).setScore(list.size() - i);
                teams.add(team);
            }
        }
        // 开始设置计分板
        for (int i = 0; i < teams.size(); i++) {
            Team team = teams.get(i);
            String text = list.get(i);
            team.setPrefix(text.length() > 16 ? text.substring(0, 16) : text);
            if (text.length() > 16) {
                String color = text.startsWith(String.valueOf(ChatColor.COLOR_CHAR)) ? text.substring(0, 2) : "";
                team.setSuffix(color + text.substring(16));
            }
        }
    }

    public void setPlayer(Player player) {
        player.setScoreboard(scoreboard);
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }

    private void create() {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        objective = scoreboard.registerNewObjective("ScoreBoard", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        teams.clear();
    }
}