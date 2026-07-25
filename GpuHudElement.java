package com.shiftclient.ui.hud;

import net.minecraft.client.gui.DrawContext;

public final class GpuHudElement extends AbstractHudElement {
    public GpuHudElement() {
        super("gpu", "GPU Usage", 160, 28, false);
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        drawText(context, "GPU N/A", 0xAAAAAA);
    }
}
