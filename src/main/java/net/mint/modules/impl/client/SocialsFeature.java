package net.mint.modules.impl.client;

import net.mint.Managers;
import net.mint.events.SubscribeEvent;
import net.mint.events.impl.ChatSendEvent;
import net.mint.modules.Category;
import net.mint.modules.Feature;
import net.mint.modules.FeatureInfo;
import net.mint.settings.types.BooleanSetting;
import net.mint.settings.types.TextSetting;
import net.mint.utils.miscellaneous.irc.BotManager;
import net.minecraft.text.Text;

@FeatureInfo(name = "Socials", category = Category.Client)
public class SocialsFeature extends Feature {

    public TextSetting prefix = new TextSetting("Prefix", "Command prefix for social commands.", "!");
    public BooleanSetting showFeedback = new BooleanSetting("ShowFeedback", "Send confirmation messages to chat.", true);
    public BooleanSetting allowStats = new BooleanSetting("AllowStats", "Allow usage of stats commands (!stats, !top).", true);

    @SubscribeEvent
    public void onChatSend(ChatSendEvent event) {
        if (mc.player == null || mc.world == null) return;

        String message = event.getMessage();
        if (message == null || message.isEmpty()) return;

        String commandPrefix = prefix.getValue();
        if (commandPrefix.isEmpty()) commandPrefix = "!";

        if (!message.startsWith(commandPrefix)) return;

        String fullCommand = message.substring(commandPrefix.length()).trim();
        if (fullCommand.isEmpty()) return;

        String[] args = fullCommand.split(" ");
        String command = args[0].toLowerCase();

        BotManager bot = BotManager.INSTANCE;

        if (bot == null) {
            sendFeedback("§c[Socials] BotManager is not initialized!");
            return;
        }

        boolean commandHandled = false;

        try {
            switch (command) {
                case "ping":
                    // !ping [target]
                    bot.sendPingString();
                    commandHandled = true;
                    break;

                case "reqping":
                case "requestping":
                    // !reqping <nickname>
                    if (!Managers.BOT.isConnected()) {
                        sendFeedback("§c[Socials] Bot is not connected.");
                        break;
                    }
                    String target = (args.length > 1) ? args[1] : "everyone";
                    bot.requestPing(target);
                    commandHandled = true;
                    break;

                case "clearlogs":
                case "clearlog":
                case "cls":
                    // !clearlogs
                    bot.clearLogs();
                    commandHandled = true;
                    break;

                case "stats":
                    // !stats
                    if (!allowStats.getValue()) {
                        sendFeedback("§c[Socials] Stats commands are disabled in settings.");
                        break;
                    }
                    int total = bot.getTotalMessagesSent();
                    sendFeedback(String.format("§a[Socials] Total messages sent: §f%d", total));
                    commandHandled = true;
                    break;

                case "top":
                case "mostactive":
                    // !top
                    if (!allowStats.getValue()) {
                        sendFeedback("§c[Socials] Stats commands are disabled in settings.");
                        break;
                    }
                    String topUser = bot.getMostActiveUser();
                    sendFeedback("§a[Socials] Most active user: §f" + topUser);
                    commandHandled = true;
                    break;

                case "status":
                case "info":
                    // !status
                    if (bot.isAuthed()) {
                        sendFeedback(String.format("§a[Socials] Logged in as: §f%s §7(§f%s§7)",
                                bot.getAuthedMintUsername(), bot.getAuthedMintRank()));
                    } else {
                        sendFeedback("§e[Socials] Not logged in to Mint services.");
                    }
                    commandHandled = true;
                    break;

                case "help":
                    sendFeedback("§6--- Socials Commands ---");
                    sendFeedback("§f!ping §7- Ping your location.");
                    sendFeedback("§f!reqping <nickname> §7- Request ping from someone.");
                    sendFeedback("§f!clearlogs §7- Clear IRC log file.");
                    sendFeedback("§f!stats §7- Show total messages.");
                    sendFeedback("§f!top §7- Show most active user.");
                    sendFeedback("§f!status §7- Show login status.");
                    commandHandled = true;
                    break;
            }
        } catch (Exception e) {
            sendFeedback("§c[Socials] Error executing command: " + e.getMessage());
            e.printStackTrace();
        }

        if (commandHandled) {
            event.setCancelled(true);
        }
    }

    private void sendFeedback(String text) {
        if (showFeedback.getValue() && mc.player != null) {
            mc.player.sendMessage(Text.literal(text), false);
        }
    }

    @Override
    public void onEnable() {
        if (getNull()) {
            setEnabled(false);
            return;
        }
        sendFeedback("§a[Socials] Module enabled. Use §f!help §afor commands.");
    }

    @Override
    public void onDisable() {
        sendFeedback("§e[Socials] Module disabled.");
    }
}