package com.balthorite.registry;

import com.balthorite.BalthoriteMod;
import com.balthorite.menu.AlloyForgeMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, BalthoriteMod.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<AlloyForgeMenu>> ALLOY_FORGE =
            MENUS.register("alloy_forge", () -> new MenuType<>(AlloyForgeMenu::new, FeatureFlags.DEFAULT_FLAGS));
}
