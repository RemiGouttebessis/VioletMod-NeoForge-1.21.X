package com.imaire.violetmod.common.recipe;

import com.imaire.violetmod.registry.ModRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import java.util.List;

public record VioletExtractorRecipe(
        Ingredient ingredient,
        List<ItemStack> results,
        int energyCost,
        int duration
) implements Recipe<SingleRecipeInput> {

    public static final MapCodec<VioletExtractorRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(VioletExtractorRecipe::ingredient),
                    ItemStack.STRICT_CODEC.listOf().fieldOf("results").forGetter(VioletExtractorRecipe::results),
                    Codec.INT.fieldOf("energy_cost").forGetter(VioletExtractorRecipe::energyCost),
                    Codec.INT.optionalFieldOf("duration", 120).forGetter(VioletExtractorRecipe::duration)
            ).apply(instance, VioletExtractorRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, VioletExtractorRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC, VioletExtractorRecipe::ingredient,
                    ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()), VioletExtractorRecipe::results,
                    ByteBufCodecs.INT, VioletExtractorRecipe::energyCost,
                    ByteBufCodecs.INT, VioletExtractorRecipe::duration,
                    VioletExtractorRecipe::new
            );

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return ingredient.test(input.item());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider provider) {
        return results.isEmpty() ? ItemStack.EMPTY : results.get(0).copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return results.isEmpty() ? ItemStack.EMPTY : results.get(0);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.VIOLET_EXTRACTOR_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.VIOLET_EXTRACTOR_TYPE.get();
    }
}
