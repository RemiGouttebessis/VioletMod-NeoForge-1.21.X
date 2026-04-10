package com.imaire.violetmod;

import com.imaire.violetmod.common.blockentity.LogicalComputerBlockEntity;
import com.imaire.violetmod.common.blockentity.VioletExtractorBlockEntity;
import com.imaire.violetmod.config.ModConfigs;
import com.imaire.violetmod.registry.*;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.slf4j.Logger;

@Mod(VioletMod.MOD_ID)
public class VioletMod {
    public static final String MOD_ID = "violetmod";
    public static final Logger LOGGER = LogUtils.getLogger();

    public VioletMod(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModDataComponents.DATA_COMPONENTS.register(modEventBus);

        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::onClientSetup);

        modContainer.registerConfig(ModConfig.Type.COMMON, ModConfigs.COMMON_SPEC);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(
                    ModBlocks.VIOLET_EXTRACTOR.get(),
                    RenderType.cutout()
            );
        });
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.LOGICAL_COMPUTER_BE.get(),
                (LogicalComputerBlockEntity be, net.minecraft.core.Direction side) -> be.getEnergyStorage(side)
        );

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.VIOLET_EXTRACTOR_BE.get(),
                (VioletExtractorBlockEntity be, net.minecraft.core.Direction side) -> be.getEnergyStorage(side)
        );

    }
}