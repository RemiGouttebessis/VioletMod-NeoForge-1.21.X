package com.imaire.violetmod.common.blockentity;

import com.imaire.violetmod.config.MachineConfig;
import com.imaire.violetmod.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;
import org.jetbrains.annotations.Nullable;

public class LogicalComputerBlockEntity extends BlockEntity {
    private int progress = 0;
    private boolean active = false;

    private final EnergyStorage energyStorage = new EnergyStorage(
            MachineConfig.LOGICAL_COMPUTER.capacity(),
            MachineConfig.LOGICAL_COMPUTER.maxReceive(),
            0
    ) {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int received = super.receiveEnergy(maxReceive, simulate);
            if (!simulate && received > 0) {
                setChanged();
            }
            return received;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            int extracted = super.extractEnergy(maxExtract, simulate);
            if (!simulate && extracted > 0) {
                setChanged();
            }
            return extracted;
        }
    };

    public LogicalComputerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LOGICAL_COMPUTER_BE.get(), pos, state);
    }

    public EnergyStorage getEnergyStorage(@Nullable Direction side) {
        return energyStorage;
    }

    public int getProgress() {
        return progress;
    }

    public boolean isActive() {
        return active;
    }

    public int getEnergyStored() {
        return energyStorage.getEnergyStored();
    }

    public int getMaxEnergyStored() {
        return energyStorage.getMaxEnergyStored();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, LogicalComputerBlockEntity be) {
        final int energyPerTick = MachineConfig.LOGICAL_COMPUTER.energyPerTick();
        final int maxProgress = MachineConfig.LOGICAL_COMPUTER.maxProgress();

        boolean wasActive = be.active;

        if (be.energyStorage.getEnergyStored() >= energyPerTick) {
            be.energyStorage.extractEnergy(energyPerTick, false);
            be.progress++;
            be.active = true;

            if (be.progress >= maxProgress) {
                be.progress = 0;
                // future action
            }

            be.setChanged();
        } else {
            be.active = false;
            be.setChanged();
        }

        if (wasActive != be.active) {
            level.sendBlockUpdated(pos, state, state, 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("energy", energyStorage.serializeNBT(registries));
        tag.putInt("progress", progress);
        tag.putBoolean("active", active);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        if (tag.contains("energy")) {
            energyStorage.deserializeNBT(registries, tag.get("energy"));
        }

        progress = tag.getInt("progress");
        active = tag.getBoolean("active");
    }
}