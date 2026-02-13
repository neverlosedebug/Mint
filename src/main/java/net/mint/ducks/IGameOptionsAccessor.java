package net.mint.ducks;

public interface IGameOptionsAccessor {
    boolean mint$isCustomOptionEnabled(String key);
    void mint$setCustomOption(String key, Object value);
}