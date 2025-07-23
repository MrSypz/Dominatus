package sypztep.dominatus.mixin.passive.shadowstep;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sypztep.dominatus.common.system.skill.passives.agility.ShadowStepPassive;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(method = "playStepSound", at = @At("HEAD"), cancellable = true)
    private void cancelFootstepSound(BlockPos pos, BlockState state, CallbackInfo ci) {
        if (ShadowStepPassive.hasShadowStep((PlayerEntity) (Object) this)) ci.cancel(); // Cancel the footstep sound completely TODO: make warden not hear player walk
    }
}
