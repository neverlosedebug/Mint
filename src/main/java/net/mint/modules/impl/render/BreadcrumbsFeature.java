package net.mint.modules.impl.render;

import net.mint.events.SubscribeEvent;
import net.mint.events.impl.RenderWorldEvent;
import net.mint.events.impl.TickEvent;
import net.mint.modules.Category;
import net.mint.modules.Feature;
import net.mint.modules.FeatureInfo;
import net.mint.settings.types.ColorSetting;
import net.mint.settings.types.NumberSetting;
import net.mint.utils.graphics.impl.Renderer3D;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@FeatureInfo(name = "Breadcrumbs", category = Category.Render)
public class BreadcrumbsFeature extends Feature {
    private final List<Vec3d> vecs = new CopyOnWriteArrayList<>();
    public NumberSetting length = new NumberSetting("Length", "Changes the length until breadcrumbs stop being created", 15, 5, 100);
    public ColorSetting color = new ColorSetting("Color", "The color of the trail.", new Color(-1));

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (getNull())
            return;

        if (vecs.size() > length.getValue().intValue()) vecs.remove(0);

        vecs.add(new Vec3d(mc.player.getX(), mc.player.getBoundingBox().minY, mc.player.getZ()));
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldEvent event) {
        if (getNull())
            return;

        for (int i = 0; i < vecs.size(); i++) {
            try {
                Vec3d from = vecs.get(i - 1);
                Vec3d to = vecs.get(i);

                Renderer3D.renderLine(event.getContext(), from, to, color.getColor(), color.getColor());
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void onDisable() {
        vecs.clear();
    }

}
