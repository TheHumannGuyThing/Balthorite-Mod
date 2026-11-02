package com.balthorite.recipe;

import com.balthorite.registry.ModRecipes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/**
 * Data-driven recipe for the Alloy Forge. Example JSON:
 * <pre>
 * {
 *   "type": "balthorite:alloy_forging",
 *   "template": {"item": "balthorite:balthorite_upgrade_template"},
 *   "base": {"item": "minecraft:netherite_ingot"},
 *   "addition": {"item": "minecraft:echo_shard"},
 *   "result": {"id": "balthorite:balthorite_ingot", "count": 1}
 * }
 * </pre>
 */
public class AlloyForgingRecipe implements Recipe<AlloyForgeRecipeInput> {
    private final Ingredient template;
    private final Ingredient base;
    private final Ingredient addition;
    private final ItemStack result;

    public AlloyForgingRecipe(Ingredient template, Ingredient base, Ingredient addition, ItemStack result) {
        this.template = template;
        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    @Override
    public boolean matches(AlloyForgeRecipeInput input, Level level) {
        return template.test(input.template()) && base.test(input.base()) && addition.test(input.addition());
    }

    @Override
    public ItemStack assemble(AlloyForgeRecipeInput input, HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result;
    }

    @Override
    public boolean isSpecial() {
        // Not shown in the recipe book (there is no recipe book for the Alloy Forge).
        return true;
    }

    public boolean isTemplateIngredient(ItemStack stack) {
        return template.test(stack);
    }

    public boolean isBaseIngredient(ItemStack stack) {
        return base.test(stack);
    }

    public boolean isAdditionIngredient(ItemStack stack) {
        return addition.test(stack);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.ALLOY_FORGING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.ALLOY_FORGING_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<AlloyForgingRecipe> {
        public static final MapCodec<AlloyForgingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.fieldOf("template").forGetter(recipe -> recipe.template),
                Ingredient.CODEC.fieldOf("base").forGetter(recipe -> recipe.base),
                Ingredient.CODEC.fieldOf("addition").forGetter(recipe -> recipe.addition),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result)
        ).apply(instance, AlloyForgingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, AlloyForgingRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, recipe -> recipe.template,
                Ingredient.CONTENTS_STREAM_CODEC, recipe -> recipe.base,
                Ingredient.CONTENTS_STREAM_CODEC, recipe -> recipe.addition,
                ItemStack.STREAM_CODEC, recipe -> recipe.result,
                AlloyForgingRecipe::new);

        @Override
        public MapCodec<AlloyForgingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, AlloyForgingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
