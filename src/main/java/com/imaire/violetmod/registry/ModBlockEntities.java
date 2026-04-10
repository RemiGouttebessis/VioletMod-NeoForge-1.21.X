package com.imaire.violetmod.registry;

import com.imaire.violetmod.VioletMod;
import com.imaire.violetmod.common.blockentity.LogicalComputerBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, VioletMod.MOD_ID);

    public static final Supplier<BlockEntityType<LogicalComputerBlockEntity>> LOGICAL_COMPUTER_BE =
            BLOCK_ENTITIES.register("logical_computer",
                    () -> BlockEntityType.Builder.of(
                            LogicalComputerBlockEntity::new,
                            ModBlocks.LOGICAL_COMPUTER.get()
                    ).build(null));
}