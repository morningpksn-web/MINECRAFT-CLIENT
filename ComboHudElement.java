package com.shiftclient.ui.hud;

import com.shiftclient.event.AttackEvent;
import com.shiftclient.event.EventBus;
import net.minecraft.client.gui.DrawContext;

public final class ComboHudElement extends AbstractHudElement {
    private int combo;
    private long lastHit;

    public ComboHudElement() {
        super("combo", "Combo Counter", 80, 16, false);
    }

    public void register(EventBus eventBus) {
        eventBus.subscribe(AttackEvent.class, event -> {
            long now = System.currentTimeMillis();
            combo = now - lastHit <= 1500L ? combo + 1 : 1;
            lastHit = now;
        });
    }

    @Override
    public void tick() {
        if (System.currentTimeMillis() - lastHit > 1500L) {
            combo = 0;
        }
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        if (combo <= 0) {
            return;
        }
        drawText(context, "Combo x" + combo, 0xFFFFAA00);
    }
}
