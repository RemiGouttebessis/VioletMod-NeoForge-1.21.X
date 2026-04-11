package com.imaire.violetmod.datagen;

import com.imaire.violetmod.VioletMod;
import com.imaire.violetmod.registry.ModBlocks;
import com.imaire.violetmod.registry.ModDataComponents;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ModLootTableProvider extends LootTableProvider {

    public ModLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Set.of(), List.of(
                new SubProviderEntry(ModBlockLootTables::new, LootContextParamSets.BLOCK)
        ), lookupProvider);
    }

    private static class ModBlockLootTables extends BlockLootSubProvider {

        protected ModBlockLootTables(HolderLookup.Provider lookupProvider) {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), lookupProvider);
        }

        @Override
        protected void generate() {
            add(ModBlocks.LOGICAL_COMPUTER.get(), this::dropWithEnergy);
            add(ModBlocks.VIOLET_EXTRACTOR.get(), this::dropWithEnergy);
        }

        private LootTable.Builder dropWithEnergy(Block block) {
            return LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1.0f))
                            .add(LootItem.lootTableItem(block)
                                    .apply(CopyComponentsFunction
                                            .copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                                            .include(ModDataComponents.ENERGY_STORED.get()))
                            )
                            .when(ExplosionCondition.survivesExplosion())
                    );
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return BuiltInRegistries.BLOCK.stream()
                    .filter(b -> BuiltInRegistries.BLOCK.getKey(b).getNamespace().equals(VioletMod.MOD_ID))
                    .collect(Collectors.toList());
        }
    }
}
