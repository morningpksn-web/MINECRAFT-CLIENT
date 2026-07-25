package com.shiftclient.performance;

import net.minecraft.client.MinecraftClient;

/**
 * Reduces client FPS while unfocused to save CPU/GPU resources.
 */
public final class DynamicFpsController {
    private static final int FOCUSED_FPS = 240;
    private static final int UNFOCUSED_FPS = 30;

    private boolean enabled = true;
    private int focusedLimit = FOCUSED_FPS;
    private int unfocusedLimit = UNFOCUSED_FPS;

    public void onTick(MinecraftClient client) {
        if (!enabled || client == null) {
            return;
        }
        int target = client.isWindowFocused() ? focusedLimit : unfocusedLimit;
        if (client.getCurrentFpsLimit() != target) {
            client.getWindow().setFramerateLimit(target);
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setFocusedLimit(int focusedLimit) {
        this.focusedLimit = Math.max(30, focusedLimit);
    }

    public void setUnfocusedLimit(int unfocusedLimit) {
        this.unfocusedLimit = Math.max(5, unfocusedLimit);
    }

    public int getFocusedLimit() {
        return focusedLimit;
    }

    public int getUnfocusedLimit() {
        return unfocusedLimit;
    }
}
