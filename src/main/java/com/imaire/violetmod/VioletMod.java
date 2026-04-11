package com.imaire.violetmod;

import com.imaire.violetmod.client.screen.VioletExtractorScreen;
import com.imaire.violetmod.common.blockentity.BaseMachineBlockEntity;
import com.imaire.violetmod.common.blockentity.VioletExtractorBlockEntity;
import com.imaire.violetmod.config.ModConfigs;
import com.imaire.violetmod.datagen.ModBlockStateProvider;
import com.imaire.violetmod.datagen.ModItemModelProvider;
import com.imaire.violetmod.datagen.ModLanguageProvider;
import com.imaire.violetmod.datagen.ModLootTableProvider;
import com.imaire.violetmod.registry.*;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;
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
        ModRecipes.RECIPE_SERIALIZERS.register(modEventBus);
        ModRecipes.RECIPE_TYPES.register(modEventBus);
        ModMenuTypes.MENU_TYPES.register(modEventBus);

        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::onClientSetup);
        modEventBus.addListener(this::registerScreens);
        modEventBus.addListener(this::gatherData);

        modContainer.registerConfig(ModConfig.Type.COMMON, ModConfigs.COMMON_SPEC);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemBlockRenderTypes.setRenderLayer(
                ModBlocks.VIOLET_EXTRACTOR.get(),
                RenderType.translucent()
        ));
    }

    private void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.VIOLET_EXTRACTOR_MENU.get(), VioletExtractorScreen::new);
    }

    private void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput packOutput = gen.getPackOutput();

        gen.addProvider(event.includeClient(), new ModBlockStateProvider(packOutput, event.getExistingFileHelper()));
        gen.addProvider(event.includeClient(), new ModItemModelProvider(packOutput, event.getExistingFileHelper()));
        gen.addProvider(event.includeServer(), new ModLootTableProvider(packOutput, event.getLookupProvider()));
        gen.addProvider(event.includeClient(), new ModLanguageProvider(packOutput, "en_us"));
        gen.addProvider(event.includeClient(), new ModLanguageProvider(packOutput, "fr_fr"));
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        // Energy — both machines share the same accessor from BaseMachineBlockEntity
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.LOGICAL_COMPUTER_BE.get(),
                BaseMachineBlockEntity::getEnergyStorage
        );
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.VIOLET_EXTRACTOR_BE.get(),
                BaseMachineBlockEntity::getEnergyStorage
        );

        // Item handler — sided: TOP=input, BOTTOM=output, others=null
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.VIOLET_EXTRACTOR_BE.get(),
                VioletExtractorBlockEntity::getItemHandler
        );
    }
}
