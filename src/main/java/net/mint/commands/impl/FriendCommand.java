package net.mint.commands.impl;

import net.mint.commands.Command;
import net.mint.commands.CommandInfo;
import net.mint.Managers;
import net.mint.services.Services;

@CommandInfo(name = "Friend", desc = "Manage your friends.")
public class FriendCommand extends Command {

    @Override
    public void onCommand(String[] args) {
        if (args.length == 0) {
            Services.CHAT.sendRaw("§7Usage: §s.friend §7<add/del/delete/list> §s[name]");
            return;
        }

        String action = args[0].toLowerCase();

        switch (action) {
            case "add":
                if (args.length < 2) {
                    Services.CHAT.sendRaw("§7Usage: §s.friend add <name>");
                    return;
                }
                Managers.FRIEND.addFriend(args[1]);
                Services.CHAT.sendRaw("§s" + args[1] + " §7has been added to your friends.");
                break;

            case "del":
            case "delete":
                if (args.length < 2) {
                    Services.CHAT.sendRaw("§7Usage: §s.friend del/delete <name>");
                    return;
                }
                Managers.FRIEND.removeFriend(args[1]);
                Services.CHAT.sendRaw("§s" + args[1] + " §7has been removed from your friends.");
                break;

            case "list":
                if (Managers.FRIEND.getFriends().isEmpty()) {
                    Services.CHAT.sendRaw("§sYou §7have no friends.");
                } else {
                    Services.CHAT.sendRaw("§7Friends: §s" + String.join(", ", Managers.FRIEND.getFriends()));
                }
                break;

            default:
                Services.CHAT.sendRaw("§7Usage: §s.friend §7<add/del/delete/list> §s[name]");
                break;
        }
    }
}