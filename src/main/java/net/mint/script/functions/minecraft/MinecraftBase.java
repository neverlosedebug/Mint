package net.mint.script.functions.minecraft;

import net.mint.script.LuaFunctionProvider;
import net.mint.utils.Globals;
import org.luaj.vm2.LuaTable;

public abstract class MinecraftBase implements LuaFunctionProvider, Globals
{

	@Override
	public String getName()
	{
		return "mc";
	}

	@Override
	public void export(LuaTable root)
	{
		LuaTable category = new LuaTable();
		add(category);
		root.set(getCategoryName(), category);
	}

	protected abstract String getCategoryName();

	protected abstract void add(LuaTable table);

}