package com.balthorite.registry;

import com.balthorite.BalthoriteMod;
import com.balthorite.item.BalthoriteUpgradeTemplateItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, BalthoriteMod.MOD_ID);

    /** Dark blue (lapis-coloured), fire resistant just like Netherite. */
    public static final DeferredHolder<Item, Item> BALTHORITE_INGOT = ITEMS.register("balthorite_ingot",
            () -> new Item(new Item.Properties().fireResistant().rarity(Rarity.RARE)));

    public static final DeferredHolder<Item, BalthoriteUpgradeTemplateItem> BALTHORITE_UPGRADE_TEMPLATE =
            ITEMS.register("balthorite_upgrade_template",
                    () -> new BalthoriteUpgradeTemplateItem(new Item.Properties().rarity(Rarity.UNCOMMON)));

    public static final DeferredHolder<Item, BlockItem> ALLOY_FORGE = ITEMS.register("alloy_forge",
            () -> new BlockItem(ModBlocks.ALLOY_FORGE.get(), new Item.Properties()));
}
