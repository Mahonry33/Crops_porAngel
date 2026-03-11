package com.harvestindicator;

import net.minecraft.block.*;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Utility class that determines whether a crop block is ready to harvest.
 * Covers: wheat, potatoes, carrots, beetroot, nether wart,
 *         melons, pumpkins, cocoa beans, and sweet berries.
 */
public class CropReadinessChecker {

    /**
     * Returns true if the block at the given position is a harvestable crop
     * that has reached its maximum growth stage.
     */
    public static boolean isReadyToHarvest(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        // --- Standard age-based crops (wheat=7, potato=7, carrot=7, beetroot=3) ---
        if (block instanceof CropBlock cropBlock) {
            return cropBlock.isMature(state);
        }

        // --- Nether Wart (age 0-3, mature at 3) ---
        if (block instanceof NetherWartBlock) {
            int age = state.get(NetherWartBlock.AGE);
            return age == 3;
        }

        // --- Cocoa Beans (age 0-2, mature at 2) ---
        if (block instanceof CocoaBlock) {
            int age = state.get(CocoaBlock.AGE);
            return age == 2;
        }

        // --- Melon block (fully grown melon cube next to a stem) ---
        if (block instanceof MelonBlock) {
            return true; // If a melon block exists, it's ready to harvest
        }

        // --- Pumpkin block (fully grown pumpkin cube next to a stem) ---
        if (block instanceof PumpkinBlock || block == Blocks.CARVED_PUMPKIN) {
            // We only want uncarved natural pumpkins
            return block == Blocks.PUMPKIN;
        }

        // --- Sweet Berry Bush (age 0-3, mature at 3, harvestable from 2) ---
        if (block instanceof SweetBerryBushBlock) {
            int age = state.get(SweetBerryBushBlock.AGE);
            return age >= 2; // yields berries at age 2 and 3
        }

        return false;
    }

    /**
     * Returns a render Y offset so the icon floats nicely above different block types.
     */
    public static double getIconYOffset(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (block instanceof MelonBlock || block instanceof PumpkinBlock) {
            return 1.2; // Full-cube blocks, icon higher
        }
        if (block instanceof CocoaBlock) {
            return 0.9;
        }
        return 1.0; // Default for flat crop models
    }
}
