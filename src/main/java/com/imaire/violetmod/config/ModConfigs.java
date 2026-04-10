package com.imaire.violetmod.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ModConfigs {
    private static final ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec COMMON_SPEC;

    static {
        MachineConfig.init(COMMON_BUILDER);
        COMMON_SPEC = COMMON_BUILDER.build();
    }

    private ModConfigs() {
    }
}