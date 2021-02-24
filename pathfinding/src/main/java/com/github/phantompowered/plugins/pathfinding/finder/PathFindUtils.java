package com.github.phantompowered.plugins.pathfinding.finder;

import com.github.phantompowered.proxy.api.block.material.Material;

public class PathFindUtils {

    public static double getYOffset(Material material) {
        switch (material) {
            case CARPET:
                return 0.0625;
            case STONE_SLAB:
            case STONE_SLAB2:
            case WOOD_STEP:
            case SANDSTONE_STAIRS:
            case SMOOTH_STAIRS:
            case SPRUCE_WOOD_STAIRS:
            case ACACIA_STAIRS:
            case BIRCH_WOOD_STAIRS:
            case BRICK_STAIRS:
            case COBBLESTONE_STAIRS:
            case DARK_OAK_STAIRS:
            case JUNGLE_WOOD_STAIRS:
            case NETHER_BRICK_STAIRS:
            case QUARTZ_STAIRS:
            case RED_SANDSTONE_STAIRS:
            case WOOD_STAIRS:
            case BED:
                return 0.5;
            default:
                return 0;
        }
    }

}
