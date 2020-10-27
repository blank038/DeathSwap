package com.blank038.deathswap.command;

import com.blank038.deathswap.DeathSwap;
import com.blank038.deathswap.game.GameArena;
import com.blank038.deathswap.util.CoreUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MainCommand implements CommandExecutor {
    private final DeathSwap INSTANCE;
    public MainCommand() {
        INSTANCE = DeathSwap.getInstance();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (sender.isOp() && args.length >= 2 && args[0].equalsIgnoreCase("editor")){
            GameArena arena = INSTANCE.gameManager.getArena(args[2]);
            if (arena == null) {
                sender.sendMessage(DeathSwap.getLangData().getString("message.does-not-exist",true));
                return true;
            }
            if (args.length == 4){
                String arg_4 = args[3];
                switch (args[1]){
                    case "min":
                        if (!CoreUtil.isInteger(arg_4)){
                            sender.sendMessage(DeathSwap.getLangData().getString("format-error-int",true));
                        }else {
                            arena.setMin(Integer.parseInt(arg_4));
                        }
                        return true;
                    case "max":

                        if (!CoreUtil.isInteger(arg_4)){
                            sender.sendMessage(DeathSwap.getLangData().getString("format-error-int",true));
                        }else {
                            arena.setMax(Integer.parseInt(arg_4));
                        }
                        return true;

                    case "name":

                        arena.setDisplayName(arg_4);
                        return true;

                    default:
                        return false;

                }
            }


        }

        return false;
    }



}