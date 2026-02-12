package net.mint.commands.impl;

import net.mint.commands.Command;
import net.mint.commands.CommandInfo;
import net.mint.services.Services;
import net.mint.Mint;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

@CommandInfo(name = "Folder", desc = "Opens the Mint client folder.")
public class FolderCommand extends Command {

    @Override
    public void onCommand(String[] args) {
        File folder = new File(Mint.NAME);
        if (!folder.exists() && !folder.mkdirs()) {
            Services.CHAT.sendRaw("§cFailed to create the Mint folder.");
            return;
        }

        String os = System.getProperty("os.name").toLowerCase();

        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(folder);
                Services.CHAT.sendRaw("§7Opened the §sMint§7 folder.");
            } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
                new ProcessBuilder("xdg-open", folder.getAbsolutePath()).start();
                Services.CHAT.sendRaw("§7Opened the §sMint§7 folder.");
            } else if (os.contains("mac")) {
                new ProcessBuilder("open", folder.getAbsolutePath()).start();
                Services.CHAT.sendRaw("§7Opened the §sMint§7 folder.");
            } else if (os.contains("win")) {
                new ProcessBuilder("explorer", folder.getAbsolutePath()).start();
                Services.CHAT.sendRaw("§7Opened the §sMint§7 folder.");
            } else {
                Services.CHAT.sendRaw("§cOpening folders is not supported on this system.");
            }
        } catch (IOException e) {
            Services.CHAT.sendRaw("§cFailed to open the Mint folder.");
        }
    }
}