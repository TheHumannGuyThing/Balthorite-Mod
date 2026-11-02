package com.balthorite.client;

import com.balthorite.BalthoriteMod;
import com.balthorite.menu.AlloyForgeMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

/**
 * Smithing-table-like screen with a dark, lapis-trimmed look.
 * Texture sheet layout (256x256):
 *   (0,0)    176x166  main panel
 *   (176,0)  16x16    ghost icon: template
 *   (192,0)  16x16    ghost icon: ingot
 *   (208,0)  16x16    ghost icon: echo shard
 *   (176,16) 22x15    glowing arrow (shown while a result is ready)
 */
public class AlloyForgeScreen extends AbstractContainerScreen<AlloyForgeMenu> {
    private static final ResourceLocation TEXTURE =
            BalthoriteMod.id("textures/gui/container/alloy_forge.png");

    private static final int TITLE_COLOR = 0xC9D6FF;
    private static final int LABEL_COLOR = 0x8FA3D9;

    private static final Component[] SLOT_HINTS = {
            Component.translatable("container.balthorite.alloy_forge.hint.template"),
            Component.translatable("container.balthorite.alloy_forge.hint.base"),
            Component.translatable("container.balthorite.alloy_forge.hint.addition")
    };

    public AlloyForgeScreen(AlloyForgeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        // Faint "what goes here" icons in the empty input slots
        for (int i = 0; i < 3; i++) {
            if (!this.menu.getSlot(i).hasItem()) {
                Slot slot = this.menu.getSlot(i);
                guiGraphics.blit(TEXTURE, this.leftPos + slot.x, this.topPos + slot.y, 176 + i * 16, 0, 16, 16);
            }
        }

        // Glowing arrow when the recipe is satisfied
        if (this.menu.getSlot(AlloyForgeMenu.RESULT_SLOT).hasItem()) {
            guiGraphics.blit(TEXTURE, this.leftPos + 66, this.topPos + 49, 176, 16, 22, 15);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, TITLE_COLOR, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, LABEL_COLOR, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);

        // Hover hint on empty input slots
        if (this.hoveredSlot != null && !this.hoveredSlot.hasItem()
                && this.menu.getCarried().isEmpty() && this.hoveredSlot.index < 3) {
            guiGraphics.renderTooltip(this.font, SLOT_HINTS[this.hoveredSlot.index], mouseX, mouseY);
        }
    }
}
