package net.mint.script.functions.client;

import net.mint.script.LuaFunctionProvider;
import net.mint.script.annotations.RegisterLua;
import net.mint.services.Services;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

@RegisterLua
public class ChatFunction implements LuaFunctionProvider {

    // todo: remake
    @Override
    public String getName() {
        return "mint";
    }

    @Override
    public void export(LuaTable table) {
        table.set("sendChat", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                String message = arg.tojstring();
                Services.CHAT.sendRaw(message);
                return LuaValue.NIL;
            }
        });
    }
}