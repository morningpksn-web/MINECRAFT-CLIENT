package com.shiftclient.performance;

import com.shiftclient.performance.memory.MemoryMonitor;
import com.shiftclient.performance.memory.ObjectPool;
import com.shiftclient.performance.network.PacketQueue;
import com.shiftclient.performance.profiler.FrameProfiler;
import com.shiftclient.performance.render.CullingState;
import com.shiftclient.performance.resource.ResourceCache;
import com.shiftclient.performance.startup.StartupOptimizer;
import com.shiftclient.performance.threading.TaskScheduler;
import net.minecraft.client.MinecraftClient;

/**
 * Central coordinator for all performance subsystems.
 */
public final class PerformanceManager {
    private final TaskScheduler taskScheduler = new TaskScheduler();
    private final FrameProfiler frameProfiler = new FrameProfiler();
    private final MemoryMonitor memoryMonitor = new MemoryMonitor();
    private final PacketQueue packetQueue = new PacketQueue();
    private final ResourceCache resourceCache = new ResourceCache(taskScheduler);
    private final StartupOptimizer startupOptimizer = new StartupOptimizer(taskScheduler);
    private final CullingState cullingState = new CullingState();
    private final ObjectPool<StringBuilder> stringBuilderPool =
            new ObjectPool<>(() -> new StringBuilder(128), StringBuilder::setLength, 32);

    private DynamicFpsController dynamicFpsController;
    private boolean initialized;

    public void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
        startupOptimizer.start();
        taskScheduler.start();
        frameProfiler.start();
        memoryMonitor.start(taskScheduler);
        resourceCache.start();
        dynamicFpsController = new DynamicFpsController();
    }

    public void shutdown() {
        taskScheduler.shutdown();
        resourceCache.shutdown();
        frameProfiler.stop();
    }

    public void onClientTick(MinecraftClient client) {
        frameProfiler.onTick(client);
        dynamicFpsController.onTick(client);
        packetQueue.drain(client);
        resourceCache.tick();
        cullingState.update(client);
    }

    public void onFrameStart() {
        frameProfiler.beginFrame();
    }

    public void onFrameEnd() {
        frameProfiler.endFrame();
    }

    public TaskScheduler getTaskScheduler() {
        return taskScheduler;
    }

    public FrameProfiler getFrameProfiler() {
        return frameProfiler;
    }

    public MemoryMonitor getMemoryMonitor() {
        return memoryMonitor;
    }

    public PacketQueue getPacketQueue() {
        return packetQueue;
    }

    public ResourceCache getResourceCache() {
        return resourceCache;
    }

    public CullingState getCullingState() {
        return cullingState;
    }

    public ObjectPool<StringBuilder> getStringBuilderPool() {
        return stringBuilderPool;
    }

    public DynamicFpsController getDynamicFpsController() {
        return dynamicFpsController;
    }
}
