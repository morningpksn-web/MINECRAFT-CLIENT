package com.shiftclient.ui.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public abstract class AbstractHudElement implements HudElement, TickableHud {
    protected final String id;
    protected final String displayName;
    protected boolean enabled;
    protected int x;
    protected int y;
    protected float scale = 1.0F;
    protected float opacity = 1.0F;

    protected AbstractHudElement(String id, String displayName, int x, int y, boolean enabled) {
        this.id = id;
        this.displayName = displayName;
        this.x = x;
        this.y = y;
        this.enabled = enabled;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public float getScale() {
        return scale;
    }

    @Override
    public void setScale(float scale) {
        this.scale = Math.max(0.5F, Math.min(2.0F, scale));
    }

    @Override
    public float getOpacity() {
        return opacity;
    }

    @Override
    public void setOpacity(float opacity) {
        this.opacity = Math.max(0.1F, Math.min(1.0F, opacity));
    }

    @Override
    public void tick() {
    }

    protected void drawText(DrawContext context, String text, int color) {
        int alpha = (int) (opacity * 255.0F) << 24;
        context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, text, x, y, (alpha & 0xFF000000) | (color & 0xFFFFFF));
    }

    protected MinecraftClient client() {
        return MinecraftClient.getInstance();
    }
}
