package com.balthorite.block;

import com.balthorite.menu.AlloyForgeMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * The Alloy Forge: a stateless crafting station (like the crafting table / smithing table).
 * Items placed in its menu are handed back to the player when the menu is closed.
 */
public class AlloyForgeBlock extends Block {
    private static final Component CONTAINER_TITLE = Component.translatable("container.balthorite.alloy_forge");

    public AlloyForgeBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        player.openMenu(state.getMenuProvider(level, pos));
        return InteractionResult.CONSUME;
    }

    @Nullable
    @Override
    protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return new SimpleMenuProvider(
                (containerId, inventory, player) -> new AlloyForgeMenu(containerId, inventory, ContainerLevelAccess.create(level, pos)),
                CONTAINER_TITLE);
    }
}
