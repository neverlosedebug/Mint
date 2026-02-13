package net.mint.ducks;

import net.minecraft.client.gl.ShaderProgram;

public interface IShaderAccess {
    ShaderProgram mint$getShader();
    void mint$setShader(ShaderProgram shader);
}