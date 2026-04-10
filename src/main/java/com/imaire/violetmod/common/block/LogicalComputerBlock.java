package com.imaire.violetmod.common.block;

import com.imaire.violetmod.common.blockentity.LogicalComputerBlockEntity;
import com.imaire.violetmod.registry.ModBlockEntities;
import com.imaire.violetmod.registry.ModDataComponents;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class LogicalComputerBlock extends BaseEntityBlock {

    public static final MapCodec<LogicalComputerBlock> CODEC =
            simpleCodec(LogicalComputerBlock::new);

    public LogicalComputerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void setPlacedBy(
            Level level,
            BlockPos pos,
            BlockState state,
            @org.jetbrains.annotations.Nullable LivingEntity placer,
            ItemStack stack
    ) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (!level.isClientSide && level.getBlockEntity(pos) instanceof LogicalComputerBlockEntity be) {
            int stored = stack.getOrDefault(ModDataComponents.ENERGY_STORED.get(), 0);
            be.setStoredEnergy(stored);
        }
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LogicalComputerBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type
    ) {
        return level.isClientSide ? null : createTickerHelper(
                type,
                ModBlockEntities.LOGICAL_COMPUTER_BE.get(),
                LogicalComputerBlockEntity::serverTick
        );
    }
}