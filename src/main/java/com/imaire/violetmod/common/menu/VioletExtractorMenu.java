package com.imaire.violetmod.common.menu;

import com.imaire.violetmod.registry.ModBlocks;
import com.imaire.violetmod.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;

public class VioletExtractorMenu extends AbstractContainerMenu {

    // Slot indices
    public static final int SLOT_INPUT    = 0;
    public static final int SLOT_OUTPUT   = 1;
    public static final int SLOT_OUTPUT_2 = 2;
    private static final int PLAYER_INV_START = 3;
    private static final int PLAYER_INV_END   = 30; // exclusive
    private static final int PLAYER_HOT_START = 30;
    private static final int PLAYER_HOT_END   = 39; // exclusive

    // ContainerData layout (6 shorts)
    // 0: energy low, 1: energy high, 2: maxEnergy low, 3: maxEnergy high
    // 4: progress,   5: maxProgress
    public static final int DATA_SLOTS = 6;

    private final ContainerData data;
    private final ContainerLevelAccess access;

    // ── Client-side constructor (called via IMenuTypeExtension) ───────────────
    public VioletExtractorMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory,
                new SimpleContainer(3),
                new SimpleContainerData(DATA_SLOTS),
                ContainerLevelAccess.NULL);
    }

    // ── Server-side constructor (called from BlockEntity.createMenu) ──────────
    public VioletExtractorMenu(int containerId, Inventory playerInventory,
                               Container container, ContainerData data,
                               Level level, BlockPos pos) {
        this(containerId, playerInventory, container, data,
                ContainerLevelAccess.create(level, pos));
    }

    private VioletExtractorMenu(int containerId, Inventory playerInventory,
                                Container container, ContainerData data,
                                ContainerLevelAccess access) {
        super(ModMenuTypes.VIOLET_EXTRACTOR_MENU.get(), containerId);
        checkContainerSize(container, 3);
        this.data   = data;
        this.access = access;

        // Machine slots
        addSlot(new Slot(container, SLOT_INPUT, 56, 35));
        addSlot(new Slot(container, SLOT_OUTPUT, 116, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) { return false; }
        });
        addSlot(new Slot(container, SLOT_OUTPUT_2, 134, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) { return false; }
        });

        // Player main inventory (3×9)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory,
                        col + row * 9 + 9,
                        8 + col * 18,
                        84 + row * 18));
            }
        }

        // Player hotbar (1×9)
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }

        addDataSlots(data);
    }

    // ── Shift-click ──────────────────────────────────────────────────────────
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack    = slot.getItem();
        ItemStack original = stack.copy();

        if (index == SLOT_OUTPUT || index == SLOT_OUTPUT_2) {
            // Output → player (reversed scan, prefer hotbar)
            slot.onQuickCraft(stack, original);
            if (!moveItemStackTo(stack, PLAYER_INV_START, PLAYER_HOT_END, true))
                return ItemStack.EMPTY;
        } else if (index == SLOT_INPUT) {
            // Input → player
            if (!moveItemStackTo(stack, PLAYER_INV_START, PLAYER_HOT_END, true))
                return ItemStack.EMPTY;
        } else if (index < PLAYER_INV_END) {
            // Player inv → machine input; fallback to hotbar
            if (!moveItemStackTo(stack, SLOT_INPUT, SLOT_INPUT + 1, false))
                if (!moveItemStackTo(stack, PLAYER_HOT_START, PLAYER_HOT_END, false))
                    return ItemStack.EMPTY;
        } else {
            // Hotbar → machine input; fallback to player inv
            if (!moveItemStackTo(stack, SLOT_INPUT, SLOT_INPUT + 1, false))
                if (!moveItemStackTo(stack, PLAYER_INV_START, PLAYER_INV_END, false))
                    return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();

        return original;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ModBlocks.VIOLET_EXTRACTOR.get());
    }

    // ── Data accessors for the screen ────────────────────────────────────────
    public int getEnergy()      { return (data.get(1) << 16) | (data.get(0) & 0xFFFF); }
    public int getMaxEnergy()   { return (data.get(3) << 16) | (data.get(2) & 0xFFFF); }
    public int getProgress()    { return data.get(4); }
    public int getMaxProgress() { return data.get(5); }
}
