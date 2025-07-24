package sypztep.dominatus.common.system.skill.active;

import net.minecraft.util.Identifier;
import sypztep.dominatus.common.system.playerclass.PlayerClass;
import sypztep.dominatus.common.system.skill.active.skills.MageFireball;
import sypztep.dominatus.common.system.skill.active.skills.NinjaStrike;
import sypztep.dominatus.common.system.skill.active.skills.WarriorCleave;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ActiveSkillRegistry {
    private static final Map<Identifier, ActiveSkill> SKILLS = new HashMap<>();

    public static void register() {
        // Register all skills
        register(new WarriorCleave());
        register(new MageFireball());
        register(new NinjaStrike());
    }

    private static void register(ActiveSkill skill) {
        SKILLS.put(skill.getId(), skill);
    }

    public static ActiveSkill getSkill(Identifier id) {
        return SKILLS.get(id);
    }

    public static List<ActiveSkill> getAllSkills() {
        return List.copyOf(SKILLS.values());
    }

    public static List<ActiveSkill> getSkillsForClass(PlayerClass playerClass) {
        return SKILLS.values().stream()
                .filter(skill -> skill.getRequiredClass() == playerClass)
                .collect(Collectors.toList());
    }
}