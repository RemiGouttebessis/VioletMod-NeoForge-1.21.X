package com.imaire.violetmod.registry;

import com.imaire.violetmod.VioletMod;
import com.imaire.violetmod.common.item.LogicalComputerBlockItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, VioletMod.MOD_ID);

    public static final Supplier<Item> LOGICAL_COMPUTER_ITEM = ITEMS.register("logical_computer",
            () -> new LogicalComputerBlockItem(ModBlocks.LOGICAL_COMPUTER.get(), new Item.Properties()));

    public static final Supplier<Item> VIOLET_EXTRACTOR_ITEM = ITEMS.register("violet_extractor",
            () -> new LogicalComputerBlockItem(ModBlocks.VIOLET_EXTRACTOR.get(), new Item.Properties()));

}