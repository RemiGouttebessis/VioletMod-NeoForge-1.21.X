package com.imaire.violetmod.common.block.state;

import net.minecraft.util.StringRepresentable;

public enum LogicalComputerVisualState implements StringRepresentable {
    POWERLESS("powerless"),
    IDLE("idle"),
    RUNNING("running");

    private final String name;

    LogicalComputerVisualState(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}