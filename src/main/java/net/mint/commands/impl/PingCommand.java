package net.mint.commands.impl;

import net.mint.Managers;
import net.mint.commands.Command;
import net.mint.commands.CommandInfo;
import net.mint.utils.miscellaneous.irc.BotManager;
import net.mint.services.Services;

@CommandInfo(name = "Ping", desc = "Ping locations in the IRC.")
public class PingCommand extends Command {

    @Override
    public void onCommand(String[] args) {
        if (args.length == 0) {
            Services.CHAT.sendRaw("§sUsage: §7.ping <send/request> [target]");
            return;
        }

        String sub = args[0].toLowerCase();
        BotManager bot = Managers.BOT;

        boolean allowRequest = true;
        switch (sub) {
            case "send":
                if (bot != null && bot.isConnected()) {
                    bot.sendPingString();
                }
                break;

            case "request":
                if (!allowRequest) {
                    Services.CHAT.sendRaw("§cPing requests are currently disabled.");
                    return;
                }
                if (args.length < 2) {
                    Services.CHAT.sendRaw("§sUsage: §7.ping request <target>");
                    return;
                }
                String target = args[1];
                if (bot != null && bot.isConnected()) {
                    bot.requestPing(target);
                }
                break;

            default:
                Services.CHAT.sendRaw("§sUsage: §7.ping <send/request> [target]");
                break;
        }
    }
}