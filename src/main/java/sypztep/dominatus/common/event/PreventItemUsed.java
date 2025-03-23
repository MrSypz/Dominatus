package sypztep.dominatus.common.event;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import sypztep.dominatus.common.init.ModDataComponents;
//import sypztep.tyrannus.common.util.ItemStackHelper;

public class PreventItemUsed implements UseItemCallback, AttackEntityCallback, AttackBlockCallback, UseBlockCallback {

    public static void register() {
        PreventItemUsed handler = new PreventItemUsed();
        UseItemCallback.EVENT.register(handler);
        AttackEntityCallback.EVENT.register(handler);
        AttackBlockCallback.EVENT.register(handler);
        UseBlockCallback.EVENT.register(handler);
    }

    @Override
    public TypedActionResult<ItemStack> interact(PlayerEntity player, World world, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (shouldPreventUse(stack)) {
            notifyPlayer(player);
            return TypedActionResult.fail(stack);
        }
        return TypedActionResult.pass(stack);
    }

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        ItemStack stack = player.getStackInHand(hand);
        if (shouldPreventUse(stack)) {
            notifyPlayer(player);
            return ActionResult.FAIL;
        }
        return ActionResult.PASS;
    }

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        ItemStack stack = player.getStackInHand(hand);
        if (shouldPreventUse(stack)) {
            notifyPlayer(player);
            return ActionResult.FAIL;
        }
        return ActionResult.PASS;
    }

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction) {
        ItemStack stack = player.getStackInHand(hand);
        if (shouldPreventUse(stack)) {
            notifyPlayer(player);
            return ActionResult.FAIL;
        }
        return ActionResult.PASS;
    }

    private boolean shouldPreventUse(ItemStack stack) {
        if (stack.isEmpty() || !stack.isDamageable()) {
            return false;
        }

        if (stack.contains(ModDataComponents.REFINEMENT)) {
//            return ItemStackHelper.shouldBreak(stack) || ItemStackHelper.willBreakNextUse(stack);
        }

        return false;
    }

    private void notifyPlayer(PlayerEntity player) {
        if (player.getWorld().isClient && player.age % 20 == 0) { // Once per second
            player.sendMessage(Text.translatable("message.dominatus.item_broken"), true);
        }
    }
}