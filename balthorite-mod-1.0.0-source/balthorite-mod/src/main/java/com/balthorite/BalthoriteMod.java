package com.balthorite;

import com.balthorite.registry.ModBlocks;
import com.balthorite.registry.ModCreativeTabs;
import com.balthorite.registry.ModItems;
import com.balthorite.registry.ModLootModifiers;
import com.balthorite.registry.ModMenus;
import com.balthorite.registry.ModRecipes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

/**
 * Balthorite Mod - a new tier of progression beyond Netherite.
 */
@Mod(BalthoriteMod.MOD_ID)
public class BalthoriteMod {
    public static final String MOD_ID = "balthorite";

    public BalthoriteMod(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModMenus.MENUS.register(modEventBus);
        ModRecipes.RECIPE_TYPES.register(modEventBus);
        ModRecipes.RECIPE_SERIALIZERS.register(modEventBus);
        ModLootModifiers.LOOT_MODIFIERS.register(modEventBus);
        ModCreativeTabs.TABS.register(modEventBus);

        modEventBus.addListener(this::addToVanillaTabs);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    /** Also list our items in the matching vanilla creative tabs. */
    private void addToVanillaTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.BALTHORITE_INGOT.get());
            event.accept(ModItems.BALTHORITE_UPGRADE_TEMPLATE.get());
        } else if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModItems.ALLOY_FORGE.get());
        }
    }
}
