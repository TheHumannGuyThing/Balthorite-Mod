package com.balthorite.registry;

import com.balthorite.BalthoriteMod;
import com.balthorite.recipe.AlloyForgingRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, BalthoriteMod.MOD_ID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, BalthoriteMod.MOD_ID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<AlloyForgingRecipe>> ALLOY_FORGING_TYPE =
            RECIPE_TYPES.register("alloy_forging",
                    () -> RecipeType.<AlloyForgingRecipe>simple(BalthoriteMod.id("alloy_forging")));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AlloyForgingRecipe>> ALLOY_FORGING_SERIALIZER =
            RECIPE_SERIALIZERS.register("alloy_forging", AlloyForgingRecipe.Serializer::new);
}
