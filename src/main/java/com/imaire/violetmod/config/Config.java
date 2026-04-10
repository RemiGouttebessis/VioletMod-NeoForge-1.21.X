package com.imaire.violetmod.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue LOGICAL_COMPUTER_CAPACITY;
    public static final ModConfigSpec.IntValue LOGICAL_COMPUTER_MAX_RECEIVE;
    public static final ModConfigSpec.IntValue LOGICAL_COMPUTER_ENERGY_PER_TICK;
    public static final ModConfigSpec.IntValue LOGICAL_COMPUTER_MAX_PROGRESS;

    static {
        BUILDER.push("machines");
        BUILDER.push("logical_computer");

        LOGICAL_COMPUTER_CAPACITY = BUILDER
                .comment("Maximum FE stored by the Logical Computer.")
                .translation("config.violetmod.logical_computer.capacity")
                .defineInRange("capacity", 10000, 1, Integer.MAX_VALUE);

        LOGICAL_COMPUTER_MAX_RECEIVE = BUILDER
                .comment("Maximum FE/t accepted by the Logical Computer.")
                .translation("config.violetmod.logical_computer.max_receive")
                .defineInRange("maxReceive", 200, 1, Integer.MAX_VALUE);

        LOGICAL_COMPUTER_ENERGY_PER_TICK = BUILDER
                .comment("FE consumed each tick while the Logical Computer is running.")
                .translation("config.violetmod.logical_computer.energy_per_tick")
                .defineInRange("energyPerTick", 20, 1, Integer.MAX_VALUE);

        LOGICAL_COMPUTER_MAX_PROGRESS = BUILDER
                .comment("Ticks required to complete one Logical Computer cycle.")
                .translation("config.violetmod.logical_computer.max_progress")
                .defineInRange("maxProgress", 100, 1, Integer.MAX_VALUE);

        BUILDER.pop();
        BUILDER.pop();
    }

    public static final ModConfigSpec SPEC = BUILDER.build();

    private Config() {
    }
}