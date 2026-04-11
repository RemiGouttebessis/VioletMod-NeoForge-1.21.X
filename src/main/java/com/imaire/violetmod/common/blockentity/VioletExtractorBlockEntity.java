package com.imaire.violetmod.common.blockentity;

import com.imaire.violetmod.common.block.VioletExtractorBlock;
import com.imaire.violetmod.common.block.state.VioletExtractorVisualState;
import com.imaire.violetmod.common.menu.VioletExtractorMenu;
import com.imaire.violetmod.common.recipe.VioletExtractorRecipe;
import com.imaire.violetmod.config.MachineConfig;
import com.imaire.violetmod.registry.ModBlockEntities;
import com.imaire.violetmod.registry.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class VioletExtractorBlockEntity extends BaseMachineBlockEntity implements MenuProvider {

    private int currentRecipeDuration = 0;

    // Sided item handlers (cached, immutable after construction)
    private final IItemHandler inputSideHandler;
    private final IItemHandler outputSideHandler;

    // ContainerData (6 shorts synced to client)
    // 0: energy low,    1: energy high
    // 2: maxEnergy low, 3: maxEnergy high
    // 4: progress,      5: maxProgress
    private final ContainerData containerData = new ContainerData() {
        @Override
        public int get(int i) {
            return switch (i) {
                case 0 ->  energyStorage.getEnergyStored()    & 0xFFFF;
                case 1 -> (energyStorage.getEnergyStored()    >> 16) & 0xFFFF;
                case 2 ->  energyStorage.getMaxEnergyStored() & 0xFFFF;
                case 3 -> (energyStorage.getMaxEnergyStored() >> 16) & 0xFFFF;
                case 4 -> progress;
                case 5 -> currentRecipeDuration;
                default -> 0;
            };
        }

        @Override
        public void set(int i, int value) {
            switch (i) {
                case 4 -> progress              = value;
                case 5 -> currentRecipeDuration = value;
            }
        }

        @Override
        public int getCount() { return VioletExtractorMenu.DATA_SLOTS; }
    };

    public VioletExtractorBlockEntity(BlockPos pos, BlockState state) {
        super(
                ModBlockEntities.VIOLET_EXTRACTOR_BE.get(),
                pos, state,
                MachineConfig.VIOLET_EXTRACTOR.capacity(),
                MachineConfig.VIOLET_EXTRACTOR.maxReceive()
        );
        initInventory(3); // slot 0 = input, slot 1 = output 1, slot 2 = output 2
        inputSideHandler  = buildInputHandler();
        outputSideHandler = buildOutputHandler();
    }

    // ── MenuProvider ─────────────────────────────────────────────────────────

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.violetmod.violet_extractor");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new VioletExtractorMenu(containerId, playerInventory,
                Objects.requireNonNull(inventory), containerData,
                Objects.requireNonNull(level), worldPosition);
    }

    // ── Sided capabilities ───────────────────────────────────────────────────

    /** Energy: accepted from all four horizontal sides (and null for direct access). */
    @Override
    @Nullable
    public net.neoforged.neoforge.energy.EnergyStorage getEnergyStorage(@Nullable Direction side) {
        if (side == null
                || side == Direction.NORTH
                || side == Direction.EAST
                || side == Direction.WEST
                || side == Direction.SOUTH) {
            return energyStorage;
        }
        return null;
    }

    /** Items: TOP inserts input, BOTTOM extracts output, other sides return null. */
    @Nullable
    public IItemHandler getItemHandler(@Nullable Direction side) {
        if (side == Direction.UP)   return inputSideHandler;
        if (side == Direction.DOWN) return outputSideHandler;
        return null;
    }

    public SimpleContainer getInventory() {
        return inventory;
    }

    // ── Server tick ──────────────────────────────────────────────────────────

    public static void serverTick(Level level, BlockPos pos, BlockState state, VioletExtractorBlockEntity be) {
        final int energyPerTick = MachineConfig.VIOLET_EXTRACTOR.energyPerTick();

        ItemStack inputItem = be.inventory.getItem(0);
        Optional<RecipeHolder<VioletExtractorRecipe>> recipeHolder = level.getRecipeManager()
                .getRecipeFor(ModRecipes.VIOLET_EXTRACTOR_TYPE.get(), new SingleRecipeInput(inputItem), level);

        if (recipeHolder.isPresent() && be.energyStorage.getEnergyStored() >= energyPerTick) {
            VioletExtractorRecipe recipe = recipeHolder.get().value();
            be.currentRecipeDuration = recipe.duration();

            if (canInsertAllResults(be.inventory, recipe.results())) {
                be.energyStorage.consumeInternal(energyPerTick, false);
                be.progress++;
                be.active = true;

                if (be.progress >= recipe.duration()) {
                    be.progress = 0;
                    inputItem.shrink(1);
                    insertAllResults(be.inventory, recipe.results());
                }
            } else {
                be.active = false;
            }
        } else {
            be.active               = false;
            be.currentRecipeDuration = 0;
        }

        be.setChanged();

        VioletExtractorVisualState newState = computeVisualState(be, inputItem, energyPerTick);
        if (state.getValue(VioletExtractorBlock.VISUAL_STATE) != newState) {
            level.setBlock(pos, state.setValue(VioletExtractorBlock.VISUAL_STATE, newState), 3);
        }
    }

    // ── NBT ──────────────────────────────────────────────────────────────────

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("currentRecipeDuration", currentRecipeDuration);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        currentRecipeDuration = tag.getInt("currentRecipeDuration");
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private static VioletExtractorVisualState computeVisualState(
            VioletExtractorBlockEntity be, ItemStack input, int energyPerTick) {
        if (be.energyStorage.getEnergyStored() < energyPerTick) return VioletExtractorVisualState.POWERLESS;
        if (input.is(Items.CRYING_OBSIDIAN)) return be.active ? VioletExtractorVisualState.CRYING_RUNNING : VioletExtractorVisualState.CRYING;
        if (input.is(Items.OBSIDIAN))        return VioletExtractorVisualState.OBSIDIAN;
        return VioletExtractorVisualState.IDLE;
    }

    private static boolean canInsertAllResults(SimpleContainer inv, List<ItemStack> results) {
        for (int i = 0; i < results.size(); i++) {
            ItemStack result   = results.get(i);
            ItemStack existing = inv.getItem(i + 1);
            if (existing.isEmpty()) continue;
            if (!ItemStack.isSameItemSameComponents(existing, result)) return false;
            if (existing.getCount() + result.getCount() > existing.getMaxStackSize()) return false;
        }
        return true;
    }

    private static void insertAllResults(SimpleContainer inv, List<ItemStack> results) {
        for (int i = 0; i < results.size(); i++) {
            ItemStack result   = results.get(i).copy();
            ItemStack existing = inv.getItem(i + 1);
            if (existing.isEmpty()) inv.setItem(i + 1, result);
            else existing.grow(result.getCount());
        }
    }

    /** TOP: insert-only wrapper on slot 0. */
    private IItemHandler buildInputHandler() {
        return new IItemHandler() {
            @Override public int getSlots() { return 1; }

            @Override
            public ItemStack getStackInSlot(int slot) {
                return slot == 0 ? Objects.requireNonNull(inventory).getItem(0) : ItemStack.EMPTY;
            }

            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                if (slot != 0 || stack.isEmpty()) return stack;
                SimpleContainer inv = Objects.requireNonNull(inventory);
                ItemStack existing = inv.getItem(0);
                if (!existing.isEmpty() && !ItemStack.isSameItemSameComponents(existing, stack)) return stack;
                int insertable = Math.min(stack.getCount(), stack.getMaxStackSize() - existing.getCount());
                if (insertable <= 0) return stack;
                if (!simulate) {
                    if (existing.isEmpty()) inv.setItem(0, stack.copyWithCount(insertable));
                    else existing.grow(insertable);
                    setChanged();
                }
                int leftover = stack.getCount() - insertable;
                return leftover > 0 ? stack.copyWithCount(leftover) : ItemStack.EMPTY;
            }

            @Override public ItemStack extractItem(int slot, int amount, boolean simulate) { return ItemStack.EMPTY; }
            @Override public int getSlotLimit(int slot) { return 64; }
            @Override public boolean isItemValid(int slot, ItemStack stack) { return slot == 0; }
        };
    }

    /** BOTTOM: extract-only wrapper on slots 1 and 2 (exposed as slots 0 and 1 externally). */
    private IItemHandler buildOutputHandler() {
        return new IItemHandler() {
            @Override public int getSlots() { return 2; }

            @Override
            public ItemStack getStackInSlot(int slot) {
                if (slot < 0 || slot > 1) return ItemStack.EMPTY;
                return Objects.requireNonNull(inventory).getItem(slot + 1);
            }

            @Override public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) { return stack; }

            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (slot < 0 || slot > 1) return ItemStack.EMPTY;
                SimpleContainer inv = Objects.requireNonNull(inventory);
                ItemStack existing = inv.getItem(slot + 1);
                if (existing.isEmpty()) return ItemStack.EMPTY;
                int extracted = Math.min(amount, existing.getCount());
                ItemStack result = existing.copyWithCount(extracted);
                if (!simulate) {
                    existing.shrink(extracted);
                    if (existing.isEmpty()) inv.setItem(slot + 1, ItemStack.EMPTY);
                    setChanged();
                }
                return result;
            }

            @Override public int getSlotLimit(int slot) { return 64; }
            @Override public boolean isItemValid(int slot, ItemStack stack) { return false; }
        };
    }
}
