package net.mint.modules.impl.client;

import net.mint.events.SubscribeEvent;
import net.mint.events.impl.ChatSendEvent;
import net.mint.events.impl.ReceiveChatEvent;
import net.mint.modules.Category;
import net.mint.modules.Feature;
import net.mint.modules.FeatureInfo;
import net.mint.settings.types.BooleanSetting;
import net.mint.utils.miscellaneous.irc.BotManager;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@FeatureInfo(name = "IRC", category = Category.Client)
public class IRCFeature extends Feature {

    public final BooleanSetting requestPings = new BooleanSetting("RequestPings", "To allow the request of pings from other Mint users.", true);
    public final List<String> ignoredUsers = new ArrayList<>();
    private static final Pattern CHAT_PATTERN = Pattern.compile("^<([^>]+)>.*");

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

        if (content.toLowerCase().startsWith("ignore")) {
            handleIgnoreCommand(content);
            return;
        }

        if (content.toLowerCase().startsWith("unignore")) {
            handleUnignoreCommand(content);
            return;
        }

        if (content.toLowerCase().equals("listignore")) {
            handleListIgnoreCommand();
            return;
        }

        if (BotManager.INSTANCE == null || !BotManager.INSTANCE.isConnected()) {
            mc.player.sendMessage(Text.literal("§c[IRC] Bot is not connected."), false);
            return;
        }

        if (content.toLowerCase().startsWith("ping")) {
            if (!requestPings.getValue()) {
                mc.player.sendMessage(Text.literal("§c[IRC] Ping requests are disabled in settings."), false);
                return;
            }

            String target = content.length() > 4 ? content.substring(4).trim() : "everyone";
            BotManager.INSTANCE.requestPing(target);
        } else {
            BotManager.INSTANCE.sendMessageFromPlayer(playerUUID, playerName, content);
        }
    }

    @SubscribeEvent
    public void onReceiveChat(ReceiveChatEvent event) {
        if (mc.player == null || event.getMessage() == null) return;

        Text messageText = event.getMessage();
        String messageString = messageText.getString();

        if (!messageString.startsWith("@")) return;

        String senderName = extractSenderName(messageString);

        if (senderName != null && isIgnored(senderName)) {
            event.setMessage(Text.empty());

            event.setCancelled(true); // xd
        }
    }

    private String extractSenderName(String message) {
        Matcher matcher = CHAT_PATTERN.matcher(message);
        if (matcher.matches()) {
            return matcher.group(1).trim();
        }

        int colonIndex = message.indexOf(":");
        if (colonIndex != -1) {
            String prefixPart = message.substring(0, colonIndex);
            int lastSpace = prefixPart.lastIndexOf(" ");
            if (lastSpace != -1 && lastSpace < prefixPart.length() - 1) {
                return prefixPart.substring(lastSpace + 1).trim();
            }
            return prefixPart.trim();
        }

        return null;
    }

    private void handleIgnoreCommand(String content) {
        String[] parts = content.split(" ", 2);
        if (parts.length < 2) {
            mc.player.sendMessage(Text.literal("§c[IRC] Usage: @ignore <nickname>"), false);
            return;
        }
        String targetName = parts[1].trim();
        if (ignoredUsers.contains(targetName)) {
            mc.player.sendMessage(Text.literal("§e[IRC] User §f" + targetName + " §eis already ignored."), false);
        } else {
            ignoredUsers.add(targetName);
            mc.player.sendMessage(Text.literal("§a[IRC] Now ignoring messages from §f" + targetName + "§a."), false);
        }
    }

    private void handleUnignoreCommand(String content) {
        String[] parts = content.split(" ", 2);
        if (parts.length < 2) {
            mc.player.sendMessage(Text.literal("§c[IRC] Usage: @unignore <nickname>"), false);
            return;
        }
        String targetName = parts[1].trim();
        if (ignoredUsers.remove(targetName)) {
            mc.player.sendMessage(Text.literal("§a[IRC] No longer ignoring §f" + targetName + "§a."), false);
        } else {
            mc.player.sendMessage(Text.literal("§e[IRC] User §f" + targetName + " §ewas not in the ignore list."), false);
        }
    }

    private void handleListIgnoreCommand() {
        if (ignoredUsers.isEmpty()) {
            mc.player.sendMessage(Text.literal("§e[IRC] Your ignore list is empty."), false);
        } else {
            StringBuilder list = new StringBuilder("§a[IRC] Ignored users: §f");
            for (int i = 0; i < ignoredUsers.size(); i++) {
                list.append(ignoredUsers.get(i));
                if (i < ignoredUsers.size() - 1) list.append("§7, §f");
            }
            mc.player.sendMessage(Text.literal(list.toString()), false);
        }
    }

    public boolean isIgnored(String playerName) {
        for (String ignored : ignoredUsers) {
            if (ignored.equalsIgnoreCase(playerName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}