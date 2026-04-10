package com.imaire.violetmod.registry;

import com.imaire.violetmod.VioletMod;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, VioletMod.MOD_ID);

    public static final Supplier<CreativeModeTab> VIOLETMOD_TAB = CREATIVE_MODE_TABS.register("violetmod_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.violetmod"))
                    .icon(() -> new ItemStack(ModItems.LOGICAL_COMPUTER_ITEM.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.LOGICAL_COMPUTER_ITEM.get());
                        output.accept(ModItems.VIOLET_EXTRACTOR_ITEM.get());
                    })
                    .build());
}