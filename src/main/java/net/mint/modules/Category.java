package net.mint.modules;

import lombok.AllArgsConstructor;
import net.mint.interfaces.Nameable;

@AllArgsConstructor
public enum Category implements Nameable {
    Combat("Combat"),
    Misc("Misc"),
    Render("Render"),
    Movement("Movement"),
    Player("Player"),
    Legit("Legit"),
    Client("Client"),
    Scripts("Scripts");

    private final String name;

    @Override
    public String getName() {
        return name;
    }
}