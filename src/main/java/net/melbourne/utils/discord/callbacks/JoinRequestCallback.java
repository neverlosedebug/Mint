package net.melbourne.utils.discord.callbacks;

import com.sun.jna.Callback;
import net.melbourne.utils.discord.DiscordUser;

public interface JoinRequestCallback extends Callback {
    void apply(final DiscordUser p0);
}