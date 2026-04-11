package com.imaire.violetmod.common.blockentity;

import com.imaire.violetmod.common.block.VioletExtractorBlock;
import com.imaire.violetmod.common.block.state.VioletExtractorVisualState;
import com.imaire.violetmod.common.recipe.VioletExtractorRecipe;
import com.imaire.violetmod.config.MachineConfig;
import com.imaire.violetmod.registry.ModBlockEntities;
import com.imaire.violetmod.registry.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class VioletExtractorBlockEntity extends BaseMachineBlockEntity {

    public VioletExtractorBlockEntity(BlockPos pos, BlockState state) {
        super(
                ModBlockEntities.VIOLET_EXTRACTOR_BE.get(),
                pos, state,
                MachineConfig.VIOLET_EXTRACTOR.capacity(),
                MachineConfig.VIOLET_EXTRACTOR.maxReceive()
        );
        initInventory(2); // slot 0 = input, slot 1 = output
    }

    public SimpleContainer getInventory() {
        return inventory;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, VioletExtractorBlockEntity be) {
        final int energyPerTick = MachineConfig.VIOLET_EXTRACTOR.energyPerTick();

        ItemStack inputItem = be.inventory.getItem(0);
        SingleRecipeInput recipeInput = new SingleRecipeInput(inputItem);

        Optional<RecipeHolder<VioletExtractorRecipe>> recipeHolder = level.getRecipeManager()
                .getRecipeFor(ModRecipes.VIOLET_EXTRACTOR_TYPE.get(), recipeInput, level);

        if (recipeHolder.isPresent() && be.energyStorage.getEnergyStored() >= energyPerTick) {
            VioletExtractorRecipe recipe = recipeHolder.get().value();

            if (canInsertResult(be.inventory, recipe.result())) {
                be.energyStorage.consumeInternal(energyPerTick, false);
                be.progress++;
                be.active = true;

                if (be.progress >= recipe.duration()) {
                    be.progress = 0;
                    inputItem.shrink(1);
                    insertResult(be.inventory, recipe.result().copy());
                }

                be.setChanged();
            } else {
                be.active = false;
                be.setChanged();
            }
        } else {
            be.active = false;
            be.setChanged();
        }

        VioletExtractorVisualState newVisualState = computeVisualState(be, inputItem, energyPerTick);
        if (state.getValue(VioletExtractorBlock.VISUAL_STATE) != newVisualState) {
            level.setBlock(pos, state.setValue(VioletExtractorBlock.VISUAL_STATE, newVisualState), 3);
        }
    }

    private static VioletExtractorVisualState computeVisualState(
            VioletExtractorBlockEntity be,
            ItemStack inputItem,
            int energyPerTick
    ) {
        if (be.energyStorage.getEnergyStored() < energyPerTick) {
            return VioletExtractorVisualState.POWERLESS;
        }

        if (inputItem.is(net.minecraft.world.item.Items.CRYING_OBSIDIAN)) {
            return be.active ? VioletExtractorVisualState.CRYING_RUNNING : VioletExtractorVisualState.CRYING;
        }

        if (inputItem.is(net.minecraft.world.item.Items.OBSIDIAN)) {
            return VioletExtractorVisualState.OBSIDIAN;
        }

        return VioletExtractorVisualState.IDLE;
    }

    private static boolean canInsertResult(SimpleContainer inv, ItemStack result) {
        ItemStack existing = inv.getItem(1);
        if (existing.isEmpty()) return true;
        if (!ItemStack.isSameItemSameComponents(existing, result)) return false;
        return existing.getCount() + result.getCount() <= existing.getMaxStackSize();
    }

    private static void insertResult(SimpleContainer inv, ItemStack result) {
        ItemStack existing = inv.getItem(1);
        if (existing.isEmpty()) {
            inv.setItem(1, result);
        } else {
            existing.grow(result.getCount());
        }
    }
}