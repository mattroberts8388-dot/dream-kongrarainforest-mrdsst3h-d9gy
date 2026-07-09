package com.kongrarainforest;

import net.minecraft.entity.player.PlayerEntity;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores rain-exposure counters keyed by player UUID.
 * This avoids any mixin requirement while still tracking per-player state.
 */
public final class PlayerExposureTracker {
    private static final ConcurrentHashMap<UUID, Integer> EXPOSURE = new ConcurrentHashMap<>();

    private PlayerExposureTracker() {}

    public static int get(PlayerEntity player) {
        return EXPOSURE.getOrDefault(player.getUuid(), 0);
    }

    public static void set(PlayerEntity player, int value) {
        EXPOSURE.put(player.getUuid(), value);
    }
}