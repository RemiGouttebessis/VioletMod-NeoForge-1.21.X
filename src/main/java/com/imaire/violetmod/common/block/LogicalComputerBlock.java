package com.imaire.violetmod.common.block;

import com.imaire.violetmod.common.block.state.LogicalComputerVisualState;
import com.imaire.violetmod.common.blockentity.LogicalComputerBlockEntity;
import com.imaire.violetmod.registry.ModBlockEntities;
import com.imaire.violetmod.registry.ModDataComponents;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.Nullable;

public class LogicalComputerBlock extends BaseEntityBlock {

    public static final MapCodec<LogicalComputerBlock> CODEC = simpleCodec(LogicalComputerBlock::new);

    public static final EnumProperty<LogicalComputerVisualState> VISUAL_STATE =
            EnumProperty.create("visual_state", LogicalComputerVisualState.class);

    public LogicalComputerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH)
                        .setValue(VISUAL_STATE, LogicalComputerVisualState.POWERLESS)
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(HorizontalDirectionalBlock.FACING, VISUAL_STATE);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection().getOpposite())
                .setValue(VISUAL_STATE, LogicalComputerVisualState.POWERLESS);
    }

    @Override
    public void setPlacedBy(
            Level level,
            BlockPos pos,
            BlockState state,
            @Nullable LivingEntity placer,
            ItemStack stack
    ) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (!level.isClientSide && level.getBlockEntity(pos) instanceof LogicalComputerBlockEntity be) {
            int stored = stack.getOrDefault(ModDataComponents.ENERGY_STORED.get(), 0);
            be.setStoredEnergy(stored);
        }
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
