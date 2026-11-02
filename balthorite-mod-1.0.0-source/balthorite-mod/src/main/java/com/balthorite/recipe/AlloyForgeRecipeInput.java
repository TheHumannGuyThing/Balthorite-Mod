package com.balthorite.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

/** The three inputs of the Alloy Forge: template (left), base (middle), addition (right). */
public record AlloyForgeRecipeInput(ItemStack template, ItemStack base, ItemStack addition) implements RecipeInput {
    @Override
    public ItemStack getItem(int index) {
        return switch (index) {
            case 0 -> template;
            case 1 -> base;
            case 2 -> addition;
            default -> throw new IllegalArgumentException("Recipe does not contain slot " + index);
        };
    }

    @Override
    public int size() {
        return 3;
    }
}
