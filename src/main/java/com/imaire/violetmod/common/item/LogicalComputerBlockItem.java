package com.imaire.violetmod.common.item;

import com.imaire.violetmod.registry.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class LogicalComputerBlockItem extends BlockItem {
    public LogicalComputerBlockItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);

        int energy = stack.getOrDefault(ModDataComponents.ENERGY_STORED.get(), 0);

        tooltip.add(Component.translatable("tooltip.violetmod.energy")
                .append(Component.literal(": " + energy + " FE"))
                .withStyle(ChatFormatting.GRAY));
    }
}