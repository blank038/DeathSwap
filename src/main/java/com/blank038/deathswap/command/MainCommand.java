package com.blank038.deathswap.command;

import com.blank038.deathswap.DeathSwap;
import com.blank038.deathswap.enums.EditorType;
import com.blank038.deathswap.game.GameArena;
import com.blank038.deathswap.util.CoreUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MainCommand implements CommandExecutor {
    private final DeathSwap INSTANCE;

    public MainCommand() {
        INSTANCE = DeathSwap.getInstance();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
        } else {
            switch (args[0]) {
                case "editor":
                    editor(sender, args);
                    break;
                case "join":
                    join(sender, args);
                    break;
                case "quit":
                    quit(sender);
                    break;
                case "info":
                    info(sender);
                    break;
                default:
                    break;
            }
        }
        return false;
    }

    private void sendHelp(CommandSender sender) {
        for (String text : DeathSwap.getLangData().getStringList("message.help." +
                (sender.hasPermission("deathswap.admin") ? "admin" : "default"), false)) {
            sender.sendMessage(text);
        }
    }

    private void join(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            if (args.length == 1) {
                sender.sendMessage(DeathSwap.getLangData().getString("message.need-arena-key", true));
                return;
            }
            DeathSwap.getInstance().getGameManager().submitJoin((Player) sender, args[1]);
        }
    }

    private void quit(CommandSender sender) {
        if (sender instanceof Player) {
            DeathSwap.getInstance().getGameManager().submitQuit((Player) sender);
        }
    }

    private void info(CommandSender sender) {

    }

    private void editor(CommandSender sender, String[] args) {
        if (sender instanceof Player && sender.hasPermission("deathswap.admin")) {
            if (args.length == 1) {
                sender.sendMessage(DeathSwap.getLangData().getString("message.editor.err-args", true));
                return;
            }
            EditorType type;
            try {
                type = EditorType.valueOf(args[1].toUpperCase());
            } catch (Exception e) {
                sender.sendMessage(DeathSwap.getLangData().getString("message.editor.err-type", true));
                return;
            }
            if (args.length == 2) {
                sender.sendMessage(DeathSwap.getLangData().getString("message.editor.err-arena", true));
                return;
            }
            GameArena arena = INSTANCE.getGameManager().getArena(args[2]);
            if (arena == null) {
                sender.sendMessage(DeathSwap.getLangData().getString("message.does-not-exist", true));
                return;
            }
            Player player = (Player) sender;
            if (type == EditorType.END || type == EditorType.WAIT) {
                arena.editorData(type, player.getLocation().clone());
            } else {
                if (args.length == 3) {
                    sender.sendMessage(DeathSwap.getLangData().getString("message.editor.err-value", true));
                    return;
                }
                if (type != EditorType.NAME && !CoreUtil.isInteger(args[3])) {
                    sender.sendMessage(DeathSwap.getLangData().getString("message.editor.not-number", true));
                    return;
                }
                arena.editorData(type, args[3]);
            }
            player.sendMessage(DeathSwap.getLangData().getString("message.editor.set", true));
        }
    }
}