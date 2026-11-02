package com.balthorite.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/** Behaves like vanilla smithing templates: an item with an explanatory tooltip, consumed by the Alloy Forge. */
public class BalthoriteUpgradeTemplateItem extends Item {
    public BalthoriteUpgradeTemplateItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("item.balthorite.balthorite_upgrade_template.upgrade").withStyle(ChatFormatting.GRAY));
        tooltip.add(CommonComponents.EMPTY);
        tooltip.add(Component.translatable("item.balthorite.balthorite_upgrade_template.applies_to").withStyle(ChatFormatting.GRAY));
        tooltip.add(CommonComponents.space().append(
                Component.translatable("item.balthorite.balthorite_upgrade_template.applies_to.desc").withStyle(ChatFormatting.BLUE)));
        tooltip.add(Component.translatable("item.balthorite.balthorite_upgrade_template.ingredients").withStyle(ChatFormatting.GRAY));
        tooltip.add(CommonComponents.space().append(
                Component.translatable("item.balthorite.balthorite_upgrade_template.ingredients.desc").withStyle(ChatFormatting.BLUE)));
    }
}
