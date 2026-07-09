package com.kongrarainforest;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import com.kongrarainforest.item.KongraArmorMaterial;

/**
 * Handles the "rainforest downpour" mechanic:
 * players exposed to rain for a prolonged period slowly lose health,
 * unless they are wearing a full set of Kongra armor.
 */
public final class RainforestRainHandler {

    // Ticks a player must be in the rain before damage begins (~5 seconds).
    private static final int EXPOSURE_THRESHOLD = 100;
    // How often damage is applied once threshold reached (once per second).
    private static final int DAMAGE_INTERVAL = 20;

    private RainforestRainHandler() {}

    public static void tick(ServerWorld world) {
        if (!world.isRaining()) {
            // Reset exposure for everyone when it is not raining.
            for (PlayerEntity player : world.getPlayers()) {
                RainforestExposure exp = (RainforestExposure) player;
                exp.kongra$setRainExposure(0);
            }
            return;
        }

        for (PlayerEntity player : world.getPlayers()) {
            if (player.isCreative() || player.isSpectator()) {
                continue;
            }

            RainforestExposure exp = (RainforestExposure) player;

            if (isExposedToRain(world, player) && !hasFullKongraArmor(player)) {
                int current = exp.kongra$getRainExposure() + 1;
                exp.kongra$setRainExposure(current);

                if (current >= EXPOSURE_THRESHOLD && (current % DAMAGE_INTERVAL) == 0) {
                    DamageSource source = world.getDamageSources().create(net.minecraft.entity.damage.DamageTypes.DROWN);
                    // Use a custom-styled generic damage; 2 hearts per hit ramping mild.
                    player.damage(source, 2.0f);
                }
            } else {
                // Under cover or protected: recover exposure gradually.
                int current = exp.kongra$getRainExposure();
                if (current > 0) {
                    exp.kongra$setRainExposure(Math.max(0, current - 2));
                }
            }
        }
    }

    private static boolean isExposedToRain(ServerWorld world, PlayerEntity player) {
        BlockPos pos = player.getBlockPos();
        if (!world.hasRain(pos)) {
            return false;
        }
        // Player must be able to see the sky above their head.
        return world.isSkyVisible(pos);
    }

    private static boolean hasFullKongraArmor(PlayerEntity player) {
        return isKongra(player.getInventory().getArmorStack(0))  // boots
                && isKongra(player.getInventory().getArmorStack(1)) // leggings
                && isKongra(player.getInventory().getArmorStack(2)) // chestplate
                && isKongra(player.getInventory().getArmorStack(3)); // helmet
    }

    private static boolean isKongra(net.minecraft.item.ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof net.minecraft.item.ArmorItem armor)) {
            return false;
        }
        return armor.getMaterial() == KongraArmorMaterial.INSTANCE;
    }
}