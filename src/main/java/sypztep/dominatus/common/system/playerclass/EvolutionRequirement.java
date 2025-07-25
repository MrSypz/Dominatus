package sypztep.dominatus.common.system.playerclass;

import net.minecraft.entity.player.PlayerEntity;
import sypztep.dominatus.common.component.living.LivingLevelComponent;
import sypztep.dominatus.common.component.living.PlayerClassComponent;
import sypztep.dominatus.common.init.ModEntityComponents;
import sypztep.dominatus.common.system.stat.PlayerStatManager;

import java.util.Map;

public record EvolutionRequirement(
        int requiredCharacterLevel,
        int requiredClassLevel,
        Map<String, Integer> statRequirements
) {

    /**
     * Check if player meets all requirements
     */
    public boolean meetsRequirements(PlayerEntity player) {
        LivingLevelComponent livingComp = ModEntityComponents.LIVINGLEVEL.get(player);
        PlayerClassComponent classComp = ModEntityComponents.PLAYERCLASS.get(player);

        // Check character level
        if (livingComp.getLevel() < requiredCharacterLevel) {
            return false;
        }

        // Check class level
        if (classComp.getClassLevel() < requiredClassLevel) {
            return false;
        }

        // Check stat requirements
        PlayerStatManager statManager = livingComp.getPlayerStatManager();
        for (Map.Entry<String, Integer> requirement : statRequirements.entrySet()) {
            int playerStatValue = statManager.getStatValueByName(requirement.getKey());
            if (playerStatValue < requirement.getValue()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Get formatted requirements text
     */
    public String getRequirementsText() {
        StringBuilder sb = new StringBuilder();
        sb.append("Character Level ").append(requiredCharacterLevel);
        sb.append(", Class Level ").append(requiredClassLevel);

        if (!statRequirements.isEmpty()) {
            sb.append(", Stats: ");
            statRequirements.forEach((stat, value) ->
                    sb.append(stat.toUpperCase()).append(" ").append(value).append(" "));
        }

        return sb.toString().trim();
    }
}
