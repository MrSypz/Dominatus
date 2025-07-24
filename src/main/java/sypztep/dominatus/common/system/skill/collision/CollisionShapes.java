package sypztep.dominatus.common.system.skill.collision;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class CollisionShapes {

    /**
     * Get entities in a cone in front of the player
     */
    public static List<LivingEntity> getConeTargets(PlayerEntity caster, double range, double angleDegrees) {
        World world = caster.getWorld();
        Vec3d casterPos = caster.getPos();
        Vec3d lookDir = caster.getRotationVec(1.0f);
        double angleRad = Math.toRadians(angleDegrees);

        // Broad phase: Get entities in bounding box
        Box searchBox = new Box(casterPos.subtract(range, range, range),
                casterPos.add(range, range, range));

        return world.getEntitiesByClass(LivingEntity.class, searchBox, entity -> {
            if (entity == caster || !entity.isAlive()) return false;

            Vec3d toTarget = entity.getPos().subtract(casterPos);
            double distance = toTarget.length();

            if (distance > range) return false;

            // Check if target is within cone angle
            double angle = Math.acos(toTarget.normalize().dotProduct(lookDir));
            return angle <= angleRad / 2;
        });
    }

    /**
     * Get entities in a circle around the player
     */
    public static List<LivingEntity> getCircleTargets(PlayerEntity caster, double radius) {
        World world = caster.getWorld();
        Vec3d center = caster.getPos();

        Box searchBox = new Box(center.subtract(radius, radius, radius),
                center.add(radius, radius, radius));

        return world.getEntitiesByClass(LivingEntity.class, searchBox, entity ->
                entity != caster &&
                        entity.isAlive() &&
                        entity.getPos().distanceTo(center) <= radius
        );
    }

    /**
     * Get a single target (closest enemy in front)
     */
    public static List<LivingEntity> getSingleTarget(PlayerEntity caster, double range) {
        List<LivingEntity> candidates = getConeTargets(caster, range, 45.0); // 45 degree cone

        if (candidates.isEmpty()) return List.of();

        // Return closest target
        Vec3d casterPos = caster.getPos();
        LivingEntity closest = candidates.stream()
                .min((a, b) -> Double.compare(
                        a.getPos().distanceTo(casterPos),
                        b.getPos().distanceTo(casterPos)
                ))
                .orElse(null);

        return closest != null ? List.of(closest) : List.of();
    }
}
