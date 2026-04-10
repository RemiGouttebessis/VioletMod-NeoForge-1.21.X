package com.imaire.violetmod.common.blockentity;

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
    private static final int CAPACITY = 100_000;
    private static final int MAX_RECEIVE = 200;
    private static final int MAX_EXTRACT = 0;
    private static final int ENERGY_PER_TICK = 2000;
    private static final int MAX_PROGRESS = 100;

    private int progress = 0;

    private final EnergyStorage energyStorage = new EnergyStorage(CAPACITY, MAX_RECEIVE, MAX_EXTRACT) {
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

    public static void serverTick(Level level, BlockPos pos, BlockState state, LogicalComputerBlockEntity be) {
        if (be.energyStorage.getEnergyStored() >= ENERGY_PER_TICK) {
            be.energyStorage.extractEnergy(ENERGY_PER_TICK, false);
            be.progress++;

            if (be.progress >= MAX_PROGRESS) {
                be.progress = 0;
                // ici tu pourras lancer une recette, produire un résultat, etc.
            }

            be.setChanged();
        } else if (be.progress != 0) {
            be.progress = 0;
            be.setChanged();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("energy", energyStorage.serializeNBT(registries));
        tag.putInt("progress", progress);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("energy")) {
            energyStorage.deserializeNBT(registries, tag.get("energy"));
        }
        progress = tag.getInt("progress");
    }
}