package net.mint.commands.impl;

import net.mint.Managers;
import net.mint.services.Services;
import net.mint.commands.Command;
import net.mint.commands.CommandInfo;

@CommandInfo(name = "Help", desc = "Lists all available commands and their descriptions.")
public class HelpCommand extends Command {

    @Override
    public void onCommand(String[] args) {
        Services.CHAT.sendRaw("§sAvailable commands:");

        for (Command cmd : Managers.COMMAND.commands) {
            Services.CHAT.sendRaw("§s" + cmd.getName() + ": §7" + cmd.getDescription());
        }
    }
}