package net.mint.ducks;

import net.minecraft.util.math.Vec3d;

public interface IParticleAccessor {
    Vec3d mint$getVelocity();
    void mint$setVelocity(double x, double y, double z);
    int mint$getAge();
    int mint$getMaxAge();
    void mint$setMaxAge(int maxAge);
    boolean mint$isAlive();
}