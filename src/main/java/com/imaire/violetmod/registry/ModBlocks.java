package com.imaire.violetmod.registry;

import com.imaire.violetmod.VioletMod;
import com.imaire.violetmod.common.block.LogicalComputerBlock;
import com.imaire.violetmod.common.block.VioletExtractorBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(Registries.BLOCK, VioletMod.MOD_ID);

    public static final Supplier<Block> LOGICAL_COMPUTER = BLOCKS.register("logical_computer",
            () -> new LogicalComputerBlock(BlockBehaviour.Properties.of()
                    .strength(0.5F)
                    .sound(SoundType.METAL)
                    ));

    public static final Supplier<Block> VIOLET_EXTRACTOR = BLOCKS.register("violet_extractor",
            () -> new VioletExtractorBlock(BlockBehaviour.Properties.of()
                    .strength(0.5F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()));
}