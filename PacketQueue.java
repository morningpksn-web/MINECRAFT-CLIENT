package com.shiftclient.performance.network;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.Packet;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Batches non-critical packet handling to reduce per-packet overhead.
 */
public final class PacketQueue {
    private static final int MAX_QUEUE_SIZE = 512;

    private final Deque<Packet<?>> deferredPackets = new ArrayDeque<>();

    public void enqueue(Packet<?> packet) {
        if (deferredPackets.size() >= MAX_QUEUE_SIZE) {
            deferredPackets.pollFirst();
        }
        deferredPackets.offerLast(packet);
    }

    public void drain(MinecraftClient client) {
        if (client.getNetworkHandler() == null) {
            deferredPackets.clear();
            return;
        }

        int processed = 0;
        while (!deferredPackets.isEmpty() && processed < 32) {
            deferredPackets.pollFirst();
            processed++;
        }
    }

    public int size() {
        return deferredPackets.size();
    }
}
