package com.imaire.violetmod;

import com.imaire.violetmod.common.blockentity.LogicalComputerBlockEntity;
import com.imaire.violetmod.registry.ModBlockEntities;
import com.imaire.violetmod.registry.ModBlocks;
import com.imaire.violetmod.registry.ModItems;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(VioletMod.MOD_ID)
public class VioletMod {
    public static final String MOD_ID = "violetmod";
    public static final Logger LOGGER = LogUtils.getLogger();

    public VioletMod(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);

        modEventBus.addListener(this::registerCapabilities);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.LOGICAL_COMPUTER_BE.get(),
                (LogicalComputerBlockEntity be, net.minecraft.core.Direction side) -> be.getEnergyStorage(side)
        );
    }
}
