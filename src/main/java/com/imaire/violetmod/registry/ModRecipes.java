package com.imaire.violetmod.registry;

import com.imaire.violetmod.VioletMod;
import com.imaire.violetmod.common.recipe.VioletExtractorRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ModRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, VioletMod.MOD_ID);

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, VioletMod.MOD_ID);

    public static final Supplier<RecipeType<VioletExtractorRecipe>> VIOLET_EXTRACTOR_TYPE =
            RECIPE_TYPES.register("violet_extracting", () ->
                    RecipeType.simple(ResourceLocation.fromNamespaceAndPath(VioletMod.MOD_ID, "violet_extracting"))
            );

    public static final Supplier<RecipeSerializer<VioletExtractorRecipe>> VIOLET_EXTRACTOR_SERIALIZER =
            RECIPE_SERIALIZERS.register("violet_extracting", () -> new RecipeSerializer<>() {
                @Override
                public com.mojang.serialization.MapCodec<VioletExtractorRecipe> codec() {
                    return VioletExtractorRecipe.CODEC;
                }

                @Override
                public StreamCodec<RegistryFriendlyByteBuf, VioletExtractorRecipe> streamCodec() {
                    return VioletExtractorRecipe.STREAM_CODEC;
                }
            });

    private ModRecipes() {}
}
