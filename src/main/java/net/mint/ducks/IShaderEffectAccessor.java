package net.mint.ducks;

import net.minecraft.client.gl.ShaderProgram;
import java.util.List;

public interface IShaderEffectAccessor {
    List<ShaderProgram> mint$getShaders();
    void mint$addUniformValue(String name, float value);
    boolean mint$isLoaded();
}