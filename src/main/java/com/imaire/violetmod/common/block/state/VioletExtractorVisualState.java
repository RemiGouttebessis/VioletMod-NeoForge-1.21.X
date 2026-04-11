package com.imaire.violetmod.common.block.state;

import net.minecraft.util.StringRepresentable;

public enum VioletExtractorVisualState implements StringRepresentable {
    POWERLESS("powerless"),
    IDLE("idle"),
    OBSIDIAN("obsidian"),
    CRYING("crying"),
    CRYING_RUNNING("crying_running");

    private final String name;

    VioletExtractorVisualState(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}