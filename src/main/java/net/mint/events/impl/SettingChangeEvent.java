package net.mint.events.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.mint.events.Event;
import net.mint.settings.Setting;

@Getter
@AllArgsConstructor
public class SettingChangeEvent extends Event
{
	private final Setting setting;
}