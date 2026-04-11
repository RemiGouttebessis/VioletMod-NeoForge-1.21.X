package com.imaire.violetmod.common.block;

import com.imaire.violetmod.common.block.state.VioletExtractorVisualState;
import com.imaire.violetmod.common.blockentity.VioletExtractorBlockEntity;
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

public class VioletExtractorBlock extends BaseEntityBlock {
    public static final MapCodec<VioletExtractorBlock> CODEC = simpleCodec(VioletExtractorBlock::new);

    public static final EnumProperty<VioletExtractorVisualState> VISUAL_STATE =
            EnumProperty.create("visual_state", VioletExtractorVisualState.class);

    public VioletExtractorBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH)
                        .setValue(VISUAL_STATE, VioletExtractorVisualState.POWERLESS)
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
                .setValue(VISUAL_STATE, VioletExtractorVisualState.POWERLESS);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VioletExtractorBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(
            Level level, BlockState state, BlockEntityType<T> type
    ) {
        return level.isClientSide ? null : createTickerHelper(
                type,
                ModBlockEntities.VIOLET_EXTRACTOR_BE.get(),
                VioletExtractorBlockEntity::serverTick
        );
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

        if (!level.isClientSide && level.getBlockEntity(pos) instanceof VioletExtractorBlockEntity be) {
            int stored = stack.getOrDefault(ModDataComponents.ENERGY_STORED.get(), 0);
            be.setStoredEnergy(stored);
        }
    }
}