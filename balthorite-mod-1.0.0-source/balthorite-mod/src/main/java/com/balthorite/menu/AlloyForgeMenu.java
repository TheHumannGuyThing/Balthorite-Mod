package com.balthorite.menu;

import com.balthorite.recipe.AlloyForgeRecipeInput;
import com.balthorite.recipe.AlloyForgingRecipe;
import com.balthorite.registry.ModBlocks;
import com.balthorite.registry.ModMenus;
import com.balthorite.registry.ModRecipes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

/**
 * Smithing-table style menu: template (left), base (middle), addition (right) -> result.
 * Slot positions match the vanilla smithing table so the layout feels familiar.
 */
public class AlloyForgeMenu extends AbstractContainerMenu {
    public static final int TEMPLATE_SLOT = 0;
    public static final int BASE_SLOT = 1;
    public static final int ADDITION_SLOT = 2;
    public static final int RESULT_SLOT = 3;

    private static final int INVENTORY_START = 4;
    private static final int HOTBAR_START = 31;
    private static final int INVENTORY_END = 40;

    private final ContainerLevelAccess access;
    private final Level level;
    private final List<RecipeHolder<AlloyForgingRecipe>> recipes;

    private final Container inputSlots = new SimpleContainer(3) {
        @Override
        public void setChanged() {
            super.setChanged();
            AlloyForgeMenu.this.slotsChanged(this);
        }
    };
    private final ResultContainer resultSlots = new ResultContainer();

    /** Client-side constructor (the server uses the three-argument version). */
    public AlloyForgeMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL);
    }

    public AlloyForgeMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(ModMenus.ALLOY_FORGE.get(), containerId);
        this.access = access;
        this.level = playerInventory.player.level();
        this.recipes = this.level.getRecipeManager().getAllRecipesFor(ModRecipes.ALLOY_FORGING_TYPE.get());

        // Input slots: template (left), netherite (middle), echo shard (right)
        this.addSlot(new Slot(inputSlots, TEMPLATE_SLOT, 8, 48) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return isTemplate(stack);
            }
        });
        this.addSlot(new Slot(inputSlots, BASE_SLOT, 26, 48) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return isBase(stack);
            }
        });
        this.addSlot(new Slot(inputSlots, ADDITION_SLOT, 44, 48) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return isAddition(stack);
            }
        });

        // Output slot
        this.addSlot(new Slot(resultSlots, 0, 98, 48) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                onResultTaken(player, stack);
            }
        });

        // Player inventory
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        // Hotbar
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    private boolean isTemplate(ItemStack stack) {
        return recipes.stream().anyMatch(holder -> holder.value().isTemplateIngredient(stack));
    }

    private boolean isBase(ItemStack stack) {
        return recipes.stream().anyMatch(holder -> holder.value().isBaseIngredient(stack));
    }

    private boolean isAddition(ItemStack stack) {
        return recipes.stream().anyMatch(holder -> holder.value().isAdditionIngredient(stack));
    }

    private void createResult() {
        AlloyForgeRecipeInput input = new AlloyForgeRecipeInput(
                inputSlots.getItem(TEMPLATE_SLOT), inputSlots.getItem(BASE_SLOT), inputSlots.getItem(ADDITION_SLOT));
        Optional<RecipeHolder<AlloyForgingRecipe>> match =
                level.getRecipeManager().getRecipeFor(ModRecipes.ALLOY_FORGING_TYPE.get(), input, level);
        if (match.isPresent()) {
            resultSlots.setItem(0, match.get().value().assemble(input, level.registryAccess()));
        } else {
            resultSlots.setItem(0, ItemStack.EMPTY);
        }
    }

    private void onResultTaken(Player player, ItemStack stack) {
        stack.onCraftedBy(player.level(), player, stack.getCount());
        shrinkInput(TEMPLATE_SLOT);
        shrinkInput(BASE_SLOT);
        shrinkInput(ADDITION_SLOT);
        // Level event 1044 = the smithing table "clink" sound
        access.execute((lvl, pos) -> lvl.levelEvent(1044, pos, 0));
    }

    private void shrinkInput(int slot) {
        ItemStack stack = inputSlots.getItem(slot);
        if (!stack.isEmpty()) {
            stack.shrink(1);
            inputSlots.setItem(slot, stack);
        }
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        if (container == inputSlots) {
            createResult();
        }
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        // Give unused ingredients back to the player
        this.access.execute((lvl, pos) -> this.clearContainer(player, this.inputSlots));
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, ModBlocks.ALLOY_FORGE.get());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack original = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            original = stack.copy();

            if (index == RESULT_SLOT) {
                if (!this.moveItemStackTo(stack, INVENTORY_START, INVENTORY_END, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stack, original);
            } else if (index < RESULT_SLOT) {
                if (!this.moveItemStackTo(stack, INVENTORY_START, INVENTORY_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                int target = inputSlotFor(stack);
                if (target >= 0) {
                    if (!this.moveItemStackTo(stack, target, target + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < HOTBAR_START) {
                    if (!this.moveItemStackTo(stack, HOTBAR_START, INVENTORY_END, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(stack, INVENTORY_START, HOTBAR_START, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (stack.getCount() == original.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, stack);
        }
        return original;
    }

    private int inputSlotFor(ItemStack stack) {
        if (isTemplate(stack)) return TEMPLATE_SLOT;
        if (isBase(stack)) return BASE_SLOT;
        if (isAddition(stack)) return ADDITION_SLOT;
        return -1;
    }
}
