package com.imaire.violetmod.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class MachineConfig {
    public static final LogicalComputer LOGICAL_COMPUTER;
    public static final VioletExtractor VIOLET_EXTRACTOR;

    static {
        LOGICAL_COMPUTER = new LogicalComputer();
        VIOLET_EXTRACTOR = new VioletExtractor();
    }

    private MachineConfig() {
    }

    public static void init(ModConfigSpec.Builder builder) {
        builder.push("machines");

        LOGICAL_COMPUTER.define(builder);
        VIOLET_EXTRACTOR.define(builder);

        builder.pop();
    }

    public static final class LogicalComputer {
        private ModConfigSpec.IntValue capacity;
        private ModConfigSpec.IntValue maxReceive;
        private ModConfigSpec.IntValue energyPerTick;
        private ModConfigSpec.IntValue maxProgress;

        private void define(ModConfigSpec.Builder builder) {
            builder.push("logical_computer");

            capacity = builder
                    .comment("Maximum FE stored by the Logical Computer.")
                    .translation("config.violetmod.logical_computer.capacity")
                    .defineInRange("capacity", 10_000, 1, Integer.MAX_VALUE);

            maxReceive = builder
                    .comment("Maximum FE/t accepted by the Logical Computer.")
                    .translation("config.violetmod.logical_computer.max_receive")
                    .defineInRange("max_receive", 200, 1, Integer.MAX_VALUE);

            energyPerTick = builder
                    .comment("FE consumed each tick while the Logical Computer runs.")
                    .translation("config.violetmod.logical_computer.energy_per_tick")
                    .defineInRange("energy_per_tick", 20, 1, Integer.MAX_VALUE);

            maxProgress = builder
                    .comment("Ticks required to complete one Logical Computer cycle.")
                    .translation("config.violetmod.logical_computer.max_progress")
                    .defineInRange("max_progress", 100, 1, Integer.MAX_VALUE);

            builder.pop();
        }

        public int capacity() { return capacity.get(); }
        public int maxReceive() { return maxReceive.get(); }
        public int energyPerTick() { return energyPerTick.get(); }
        public int maxProgress() { return maxProgress.get(); }
    }

    public static final class VioletExtractor {
        private ModConfigSpec.IntValue capacity;
        private ModConfigSpec.IntValue maxReceive;
        private ModConfigSpec.IntValue energyPerTick;
        private ModConfigSpec.IntValue maxProgress;

        private void define(ModConfigSpec.Builder builder) {
            builder.push("violet_extractor");

            capacity = builder
                    .comment("Maximum FE stored by the Violet Extractor.")
                    .translation("config.violetmod.violet_extractor.capacity")
                    .defineInRange("capacity", 20_000, 1, Integer.MAX_VALUE);

            maxReceive = builder
                    .comment("Maximum FE/t accepted by the Violet Extractor.")
                    .translation("config.violetmod.violet_extractor.max_receive")
                    .defineInRange("max_receive", 400, 1, Integer.MAX_VALUE);

            energyPerTick = builder
                    .comment("FE consumed each tick while the Violet Extractor runs.")
                    .translation("config.violetmod.violet_extractor.energy_per_tick")
                    .defineInRange("energy_per_tick", 40, 1, Integer.MAX_VALUE);

            maxProgress = builder
                    .comment("Ticks required to complete one Violet Extractor cycle.")
                    .translation("config.violetmod.violet_extractor.max_progress")
                    .defineInRange("max_progress", 120, 1, Integer.MAX_VALUE);

            builder.pop();
        }

        public int capacity() { return capacity.get(); }
        public int maxReceive() { return maxReceive.get(); }
        public int energyPerTick() { return energyPerTick.get(); }
        public int maxProgress() { return maxProgress.get(); }
    }
}