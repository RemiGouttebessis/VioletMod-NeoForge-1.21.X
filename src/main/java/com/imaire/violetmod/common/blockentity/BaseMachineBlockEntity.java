package com.imaire.violetmod.common.blockentity;

import com.imaire.violetmod.common.energy.ModEnergyStorage;
import com.imaire.violetmod.registry.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;
import org.jetbrains.annotations.Nullable;

public abstract class BaseMachineBlockEntity extends BlockEntity {

    protected int progress = 0;
    protected boolean active = false;

    protected final ModEnergyStorage energyStorage;

    @Nullable
    protected SimpleContainer inventory;

    protected BaseMachineBlockEntity(
            BlockEntityType<?> type,
            BlockPos pos,
            BlockState state,
            int capacity,
            int maxReceive
    ) {
        super(type, pos, state);
        this.energyStorage = new ModEnergyStorage(capacity, maxReceive, 0, this::setChanged);
    }

    protected void initInventory(int size) {
        this.inventory = new SimpleContainer(size);
    }

    public EnergyStorage getEnergyStorage(@Nullable Direction side) {
        return energyStorage;
    }

    public void setStoredEnergy(int energy) {
        int clamped = Math.max(0, Math.min(energy, energyStorage.getMaxEnergyStored()));
        energyStorage.setEnergyDirect(clamped);
        setChanged();
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(ModDataComponents.ENERGY_STORED.get(), energyStorage.getEnergyStored());
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("energy", energyStorage.serializeNBT(registries));
        tag.putInt("progress", progress);
        tag.putBoolean("active", active);
        if (inventory != null) {
            tag.put("inventory", inventory.createTag(registries));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("energy")) {
            energyStorage.deserializeNBT(registries, tag.get("energy"));
        }
        progress = tag.getInt("progress");
        active = tag.getBoolean("active");
        if (inventory != null && tag.contains("inventory")) {
            inventory.fromTag(tag.getList("inventory", 10), registries);
        }
    }
}