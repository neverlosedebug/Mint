package net.mint.modules.impl.client;

import net.mint.events.SubscribeEvent;
import net.mint.events.impl.ChatSendEvent;
import net.mint.modules.Category;
import net.mint.modules.Feature;
import net.mint.modules.FeatureInfo;
import net.mint.settings.types.BooleanSetting;
import net.mint.utils.miscellaneous.irc.BotManager;

import java.util.UUID;

@FeatureInfo(name = "IRC", category = Category.Client)
public class IRCFeature extends Feature {

    public final BooleanSetting requestPings = new BooleanSetting("RequestPings", "To allow the request of pings from other Mint users.", true);

    @SubscribeEvent
    public void onChatSend(ChatSendEvent event) {
        if (mc.player == null) return;

        String message = event.getMessage();
        if (message == null || !message.startsWith("@")) return;

        event.setCancelled(true);

        String content = message.substring(1).trim();
        if (content.isEmpty()) return;

        UUID playerUUID = mc.player.getUuid();
        String playerName = mc.player.getName().getString();

        if (BotManager.INSTANCE != null && BotManager.INSTANCE.isConnected()) {
            BotManager.INSTANCE.sendMessageFromPlayer(playerUUID, playerName, content);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}