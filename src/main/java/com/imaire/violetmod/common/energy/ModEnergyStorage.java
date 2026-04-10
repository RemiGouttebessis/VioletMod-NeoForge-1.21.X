package com.imaire.violetmod.common.energy;

import net.neoforged.neoforge.energy.EnergyStorage;

public class ModEnergyStorage extends EnergyStorage {
    private final Runnable onChange;

    public ModEnergyStorage(int capacity, int maxReceive, int maxExtract, Runnable onChange) {
        super(capacity, maxReceive, maxExtract);
        this.onChange = onChange;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int received = super.receiveEnergy(maxReceive, simulate);
        if (!simulate && received > 0) {
            onChange.run();
        }
        return received;
    }

    public void setEnergyDirect(int energy) {
        this.energy = energy;
        onChange.run();
    }

    public int consumeInternal(int amount, boolean simulate) {
        int extracted = Math.min(this.energy, amount);
        if (!simulate && extracted > 0) {
            this.energy -= extracted;
            onChange.run();
        }
        return extracted;
    }

    @Override
    public boolean canExtract() {
        return false;
    }
}