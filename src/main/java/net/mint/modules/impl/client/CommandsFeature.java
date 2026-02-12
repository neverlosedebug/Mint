package net.mint.modules.impl.client;

import net.mint.Managers;
import net.mint.commands.CommandManager;
import net.mint.events.SubscribeEvent;
import net.mint.events.impl.ChatSendEvent;
import net.mint.modules.Category;
import net.mint.modules.Feature;
import net.mint.modules.FeatureInfo;

@FeatureInfo(name = "Commands", category = Category.Client)
public class CommandsFeature extends Feature {

    @SubscribeEvent
    public void onChat(ChatSendEvent event) {
        if (mc.player == null) return;

        String msg = event.getMessage();
        if (msg == null) return;

        if (msg.startsWith(CommandManager.PREFIX)) {
            boolean handled = Managers.COMMAND.handleChatMessage(msg);
            if (handled) event.setCancelled(true);
        }
    }
}