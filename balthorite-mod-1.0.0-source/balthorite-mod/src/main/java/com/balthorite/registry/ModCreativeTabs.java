package com.balthorite.registry;

import com.balthorite.BalthoriteMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BalthoriteMod.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BALTHORITE_TAB = TABS.register("balthorite",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.balthorite"))
                    .icon(() -> new ItemStack(ModItems.BALTHORITE_INGOT.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.ALLOY_FORGE.get());
                        output.accept(ModItems.BALTHORITE_UPGRADE_TEMPLATE.get());
                        output.accept(ModItems.BALTHORITE_INGOT.get());
                    })
                    .build());
}
