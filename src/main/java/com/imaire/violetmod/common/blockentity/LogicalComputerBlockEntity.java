package com.imaire.violetmod.common.blockentity;

import com.imaire.violetmod.common.block.LogicalComputerBlock;
import com.imaire.violetmod.common.block.state.LogicalComputerVisualState;
import com.imaire.violetmod.config.MachineConfig;
import com.imaire.violetmod.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class LogicalComputerBlockEntity extends BaseMachineBlockEntity {

    public LogicalComputerBlockEntity(BlockPos pos, BlockState state) {
        super(
                ModBlockEntities.LOGICAL_COMPUTER_BE.get(),
                pos, state,
                MachineConfig.LOGICAL_COMPUTER.capacity(),
                MachineConfig.LOGICAL_COMPUTER.maxReceive()
        );
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

        LogicalComputerVisualState newVisualState = computeVisualState(be, energyPerTick);
        if (state.getValue(LogicalComputerBlock.VISUAL_STATE) != newVisualState) {
            level.setBlock(pos, state.setValue(LogicalComputerBlock.VISUAL_STATE, newVisualState), 3);
        }
    }

    private static LogicalComputerVisualState computeVisualState(LogicalComputerBlockEntity be, int energyPerTick) {
        if (be.energyStorage.getEnergyStored() < energyPerTick) {
            return LogicalComputerVisualState.POWERLESS;
        }
        if (be.active) {
            return LogicalComputerVisualState.RUNNING;
        }
        return LogicalComputerVisualState.IDLE;
    }
}