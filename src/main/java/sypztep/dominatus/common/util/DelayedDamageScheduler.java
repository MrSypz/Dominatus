package sypztep.dominatus.common.util;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DelayedDamageScheduler {
    private static final DelayedDamageScheduler INSTANCE = new DelayedDamageScheduler();

    private final List<ScheduledDamage> scheduledDamages = new ArrayList<>();

    public static DelayedDamageScheduler getInstance() {
        return INSTANCE;
    }

    /**
     * Schedule damage to be applied after a delay
     */
    public void scheduleDamage(ServerWorld world, int delayTicks, Runnable damageAction) {
        long executeTime = world.getTime() + delayTicks;
        scheduledDamages.add(new ScheduledDamage(world, executeTime, damageAction));
    }

    /**
     * Tick method - call this from your main mod tick handler
     */
    public void tick() {
        Iterator<ScheduledDamage> iterator = scheduledDamages.iterator();

        while (iterator.hasNext()) {
            ScheduledDamage scheduled = iterator.next();

            // Check if it's time to execute
            if (scheduled.world.getTime() >= scheduled.executeTime) {
                try {
                    scheduled.action.run();
                } catch (Exception e) {
                    // Log error but don't crash
                    System.err.println("Error executing delayed damage: " + e.getMessage());
                }
                iterator.remove();
            }
        }
    }

    private record ScheduledDamage(ServerWorld world, long executeTime, Runnable action) {}
}
