package com.shiftclient.discord;

import com.shiftclient.ShiftClientMod;
import net.minecraft.client.MinecraftClient;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Discord Rich Presence via local IPC pipe.
 */
public final class DiscordRpcManager {
    private static final int OPCODE_FRAME = 1;

    private final AtomicBoolean connected = new AtomicBoolean(false);
    private RandomAccessFile pipe;
    private long sessionStart = System.currentTimeMillis() / 1000L;

    public void connect() {
        ShiftClientMod.getInstance().getPerformanceManager().getTaskScheduler().scheduleAsync("discord-connect", () -> {
            for (int i = 0; i < 10; i++) {
                try {
                    pipe = new RandomAccessFile("\\\\?\\pipe\\discord-ipc-" + i, "rw");
                    connected.set(true);
                    updatePresence();
                    return;
                } catch (IOException ignored) {
                    // try next pipe index
                }
            }
            ShiftClientMod.LOGGER.debug("Discord IPC not available");
        });
    }

    public void updatePresence() {
        if (!connected.get()) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        String details = "ShiftClient " + ShiftClientMod.getInstance().getClass().getPackage().getImplementationVersion();
        String state = client.getCurrentServerEntry() != null
                ? "On " + client.getCurrentServerEntry().address
                : client.isInSingleplayer() ? "Singleplayer" : "Main Menu";
        sendPayload(buildPresenceJson(details == null ? "ShiftClient" : details, state));
    }

    public void shutdown() {
        connected.set(false);
        if (pipe != null) {
            try {
                pipe.close();
            } catch (IOException ignored) {
            }
        }
    }

    private void sendPayload(String json) {
        if (pipe == null) {
            return;
        }
        try {
            byte[] data = json.getBytes(StandardCharsets.UTF_8);
            ByteBuffer buffer = ByteBuffer.allocate(8 + data.length).order(ByteOrder.LITTLE_ENDIAN);
            buffer.putInt(OPCODE_FRAME);
            buffer.putInt(data.length);
            buffer.put(data);
            pipe.write(buffer.array());
        } catch (IOException exception) {
            connected.set(false);
        }
    }

    private String buildPresenceJson(String details, String state) {
        return "{\"cmd\":\"SET_ACTIVITY\",\"args\":{\"pid\":" + ProcessHandle.current().pid()
                + ",\"activity\":{\"details\":\"" + escape(details) + "\",\"state\":\"" + escape(state)
                + "\",\"timestamps\":{\"start\":" + sessionStart + "},\"assets\":{\"large_image\":\"shiftclient\"}}}}";
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
