package net.mint.script.functions.client;

import net.mint.Mint;
import net.mint.script.LuaFunctionProvider;
import net.mint.script.annotations.RegisterLua;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

@RegisterLua
public class PrintFunction implements LuaFunctionProvider
{

	@Override
	public String getName()
	{
		return "mint";
	}

	@Override
	public void export(LuaTable table)
	{
		table.set("print", new OneArgFunction()
		{
			@Override
			public LuaValue call(LuaValue arg)
			{
				Mint.getLogger().info("[mint-script] " + arg.tojstring());
				return LuaValue.NIL;
			}
		});
	}
}