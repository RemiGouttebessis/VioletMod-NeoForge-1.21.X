package com.imaire.violetmod.common.blockentity;

import com.imaire.violetmod.common.energy.ModEnergyStorage;
import com.imaire.violetmod.config.MachineConfig;
import com.imaire.violetmod.registry.ModBlockEntities;
import com.imaire.violetmod.registry.ModDataComponents;
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

    private final ModEnergyStorage energyStorage = new ModEnergyStorage(
            MachineConfig.LOGICAL_COMPUTER.capacity(),
            MachineConfig.LOGICAL_COMPUTER.maxReceive(),
            0,
            this::setChanged
    );

    public void setStoredEnergy(int energy) {
        int clamped = Math.max(0, Math.min(energy, energyStorage.getMaxEnergyStored()));
        energyStorage.setEnergyDirect(clamped);
        setChanged();
    }

    @Override
    protected void collectImplicitComponents(net.minecraft.core.component.DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(ModDataComponents.ENERGY_STORED.get(), this.energyStorage.getEnergyStored());
    }

    public LogicalComputerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LOGICAL_COMPUTER_BE.get(), pos, state);
    }

    public EnergyStorage getEnergyStorage(@Nullable Direction side) {
        return energyStorage;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, LogicalComputerBlockEntity be) {
        final int energyPerTick = MachineConfig.LOGICAL_COMPUTER.energyPerTick();
        final int maxProgress = MachineConfig.LOGICAL_COMPUTER.maxProgress();

        if (be.energyStorage.getEnergyStored() >= energyPerTick) {
            be.energyStorage.consumeInternal(energyPerTick, false);
            be.progress++;
            be.active = true;

            if (be.progress >= maxProgress) {
                be.progress = 0;
            }

            be.setChanged();
        } else {
            be.active = false;
            be.setChanged();
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