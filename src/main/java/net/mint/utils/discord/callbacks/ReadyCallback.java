package net.mint.utils.discord.callbacks;

import com.sun.jna.Callback;
import net.mint.utils.discord.DiscordUser;

public interface ReadyCallback extends Callback {
    void apply(final DiscordUser p0);
}