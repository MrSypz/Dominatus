package sypztep.dominatus.client.data.provider;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModLanguageProvider extends FabricLanguageProvider {
    public ModLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translate) {
        // Damage Translation
        translate.add("dominatus.text.missing", "Missing");
        translate.add("dominatus.text.critical", "Critical");
        translate.add("dominatus.text.back", "Back Attack");
        translate.add("dominatus.text.air", "Air Attack");
        generateConfig(translate);
        // Refinement Stone
        translate.add("item.dominatus.refine_weapon_stone", "Refinement Weapon Stone");
        translate.add("item.dominatus.refine_weapon_stone.desc", "A stone used to refine weapons.");
        translate.add("item.dominatus.refine_armor_stone", "Refinement Armor Stone");
        translate.add("item.dominatus.refine_armor_stone.desc", "A stone used to refine armor.");
        translate.add("item.dominatus.refine_weapon_enforge_stone", "Refinement Enforge Weapon Stone");
        translate.add("item.dominatus.refine_weapon_enforge_stone.desc", "A stone used to refine weapon with level > 15.");
        translate.add("item.dominatus.refine_armor_enforge_stone", "Refinement Enforge Armor Stone");
        translate.add("item.dominatus.refine_armor_enforge_stone.desc", "A stone used to refine armor with level > 15.");
        // Reform Stone
        translate.add("item.dominatus.reform_stone_grade_low", "Reform Stone Grade 1");
        translate.add("item.dominatus.reform_stone_grade_low.desc", "A stone used to reform items.");
        translate.add("item.dominatus.reform_stone_grade_mid", "Reform Stone Grade 2");
        translate.add("item.dominatus.reform_stone_grade_mid.desc", "A stone used to reform items.");
        translate.add("item.dominatus.reform_stone_grade_high", "Reform Stone Grade 3");
        translate.add("item.dominatus.reform_stone_grade_high.desc", "A stone used to reform items.");
        translate.add("item.dominatus.gem", "Gem");
        translate.add("item.dominatus.gem.effect", "Effect");
        // Gem Stones
        translate.add("item.dominatus.gem.pri_accuracy", "I Accuracy");
        translate.add("item.dominatus.gem.duo_accuracy", "II Accuracy");
        translate.add("item.dominatus.gem.tri_accuracy", "III Accuracy");
        translate.add("item.dominatus.gem.pri_evasion", "I Evasion");
        translate.add("item.dominatus.gem.duo_evasion", "II Evasion");
        translate.add("item.dominatus.gem.tri_evasion", "III Evasion");
        translate.add("item.dominatus.gem.pri_goliath", "I Goliath");
        translate.add("item.dominatus.gem.duo_goliath", "II Goliath");
        translate.add("item.dominatus.gem.tri_goliath", "III Goliath");
        translate.add("item.dominatus.gem.pri_miner", "I Miner");
        translate.add("item.dominatus.gem.duo_miner", "II Miner");
        translate.add("item.dominatus.gem.tri_miner", "III Miner");

// Death Messages
        translate.add("dominatus:pri_accuracy", "I Accuracy");
        translate.add("dominatus:duo_accuracy", "II Accuracy");
        translate.add("dominatus:tri_accuracy", "III Accuracy");
        translate.add("dominatus:pri_evasion", "I Evasion");
        translate.add("dominatus:duo_evasion", "II Evasion");
        translate.add("dominatus:tri_evasion", "III Evasion");
        translate.add("dominatus:pri_goliath", "I Goliath");
        translate.add("dominatus:duo_goliath", "II Goliath");
        translate.add("dominatus:tri_goliath", "III Goliath");
        translate.add("dominatus:pri_miner", "I Miner");
        translate.add("dominatus:duo_miner", "II Miner");
        translate.add("dominatus:tri_miner", "III Miner");
        // tooltip gem
        translate.add("item.dominatus.gem.effects", "Effects");
        // Attributes
        translate.add("attribute.name.evasion", "Evasion");
        translate.add("attribute.name.accuracy", "Accuracy");
        translate.add("attribute.name.crit_chance", "Critical Chance");
        translate.add("attribute.name.crit_damage", "Critical Damage");
        translate.add("attribute.name.back_attack", "Back Damage");
        translate.add("attribute.name.air_attack", "Air Damage");
        translate.add("attribute.name.down_attack", "Down Damage");
        translate.add("attribute.name.player_vers_entity_damage", "PvE Damage");
        translate.add("attribute.name.player_vers_player_damage", "PvP Damage");
        translate.add("attribute.name.health_regen","Health Regen");
        // Miscellaneous
        translate.add("item.dominatus.loss_fragment", "Loss Fragment");
        translate.add("item.dominatus.loss_fragment.desc", "Combine to obtain Refined Armor Enforge.");
        translate.add("item.dominatus.lahav_fragment", "Lahav Fragment");
        translate.add("item.dominatus.lahav_fragment.desc", "Combine to obtain Refined Weapon Enforge.");
        translate.add("item.dominatus.mahilnant", "Mahilnant");
        translate.add("item.dominatus.mahilnant.desc", "A mysterious item.");
        translate.add("item.dominatus.moonlight_crescent", "Moonlight Crescent");
        translate.add("item.dominatus.moonlight_crescent.desc", "Used to restore the items. durability.");

        translate.add("item.dominatus.kutum_bracket","Kutum Bracket");
        translate.add("item.dominatus.nouver_bracket","Nouver Bracket");
        translate.add("item.dominatus.yuria_bracket","Yuria Bracket");
        // Screen
        translate.add("dominatus.refinebutton_tooltip", "Refinement");
        translate.add("dominatus.reform.screen", "Reform");

        // Tooltip
        translate.add("dominatus.attribute.modifier.armor", "+%s (%s) Armor");
        translate.add("dominatus.attribute.modifier.damage", "%s (%s) Attack Damage");
        translate.add("dominatus.attribute.modifier.accuracy", "%s Accuracy");
        translate.add("dominatus.attribute.modifier.evasion", "%s Evasion");

        //Jade
        translate.add( "tooltip.dominatus.hit_chance", "Hit Chance: %d%%");
        //Jade Config
        translate.add("config.jade.plugin_dominatus.stats_config", "Stats Config");
        //Stats Screen
        generateStatsScreenTranslations(translate);
        genetrateTabGem(translate);
    }
    private void genetrateTabGem(TranslationBuilder translate) {
        translate.add("panel.dominatus.gem_inventory", "Gem Inventory");
        translate.add("panel.dominatus.gem_presets", "Gem Presets");

        translate.add("panel.dominatus.info", "Information");
        translate.add("panel.dominatus.gem_stats", "Gem Stat Bonuses");
    }
    private void generateConfig(TranslationBuilder translate) {
        translate.add("text.autoconfig.dominatus.title", "Dominatus Config");

        translate.add("text.autoconfig.dominatus.category.feature-client", "Client Config");
        translate.add("text.autoconfig.dominatus.category.combat", "Combat Config");

        translate.add("text.autoconfig.dominatus.option.damageCritIndicator", "Crit Indicator");
        translate.add("text.autoconfig.dominatus.option.missingIndicator", "Missing Indicator");
        translate.add("text.autoconfig.dominatus.option.critDamageColor", "Crit Damage Color");

        translate.add("text.autoconfig.dominatus.option.hitDelay", "Hit Delay");
        translate.add("text.autoconfig.dominatus.option.multihit", "Multihit System");
    }
    private void generateStatsScreenTranslations(TranslationBuilder translate) {
        translate.add("screen.dominatus.player_info","Player Information");
        // Tab and panels
        translate.add("tab.dominatus.stats", "Stats");
        translate.add("tab.dominatus.gems", "Gems");
        translate.add("panel.dominatus.stat_description", "Stat Description");
        translate.add("panel.dominatus.player_statistics", "Player Statistics");

        // Section headers
        translate.add("header.dominatus.combat_stats", "COMBAT STATISTICS");
        translate.add("header.dominatus.player_attributes", "PLAYER ATTRIBUTES");
        // Accuracy stat
        translate.add("stat.dominatus.accuracy", "Accuracy");
        translate.add("stat.dominatus.accuracy.label", "Accuracy %s");
        translate.add("stat.dominatus.accuracy.desc",
                "Increases your chance to hit enemies, especially those with high evasion. Each point of accuracy counteracts an opponent's evasion.");
        translate.add("stat.dominatus.accuracy.details",
                "Each point of Accuracy improves your hit chance by approximately 0.5% against enemies with equal level.");
        // Evasion stat
        translate.add("stat.dominatus.evasion", "Evasion");
        translate.add("stat.dominatus.evasion.label", "Evasion %s");
        translate.add("stat.dominatus.evasion.desc",
                "Improves your chance to dodge incoming attacks. Effective against enemies with low accuracy.");
        translate.add("stat.dominatus.evasion.details",
                "Each point of Evasion grants approximately 0.5% chance to completely avoid damage from attacks.");
        // Critical Hit Chance stat
        translate.add("stat.dominatus.crit_chance", "Critical Chance");
        translate.add("stat.dominatus.crit_chance.label", "Critical Chance %s");
        translate.add("stat.dominatus.crit_chance.desc",
                "Determines the probability of landing a critical hit, which deals extra damage.");
        translate.add("stat.dominatus.crit_chance.details",
                "Critical strikes deal additional damage based on your Critical Hit Damage multiplier.");

        // Critical Hit Damage stat
        translate.add("stat.dominatus.crit_damage", "Critical Hit Damage");
        translate.add("stat.dominatus.crit_damage.label", "Critical Hit Damage %s");
        translate.add("stat.dominatus.crit_damage.desc",
                "Multiplier for damage when you score a critical hit.");
        translate.add("stat.dominatus.crit_damage.details",
                "The default critical hit multiplier is 150%. This stat increases that multiplier.");

        // Health stat
        translate.add("stat.dominatus.max_health", "Max Health");
        translate.add("stat.dominatus.max_health.label", "Max Health %s");
        translate.add("stat.dominatus.max_health.desc",
                "Your total max health points. How much your fully health you have.");
        translate.add("stat.dominatus.max_health.details",
                "Max Health can be increased through, command etc..");

        translate.add("stat.dominatus.health_regen", "Nature Health Regen");
        translate.add("stat.dominatus.health_regen.label", "Nature Health Regen %s");
        translate.add("stat.dominatus.health_regen.desc", "Boosts your ability to recover health over time.");
        translate.add("stat.dominatus.health_regen.details", "Grants health every 60 ticks (3 seconds) per point, active only when not in combat.");
        // Armor stat
        translate.add("stat.dominatus.armor", "Armor");
        translate.add("stat.dominatus.armor.label", "Armor %s");
        translate.add("stat.dominatus.armor.desc",
                "Reduces damage taken from physical attacks.");
        translate.add("stat.dominatus.armor.details",
                "Each point of armor reduces incoming physical damage by approximately 4%.");

        // Armor Toughness stat
        translate.add("stat.dominatus.armor_toughness", "Armor Toughness");
        translate.add("stat.dominatus.armor_toughness.label", "Armor Toughness %s");
        translate.add("stat.dominatus.armor_toughness.desc",
                "Improves your resistance to high-damage attacks.");
        translate.add("stat.dominatus.armor_toughness.details",
                "Reduces the effectiveness of damage that bypasses armor. Each point increases protection against strong hits.");

        // Movement Speed stat
        translate.add("stat.dominatus.movement_speed", "Movement Speed");
        translate.add("stat.dominatus.movement_speed.label", "Movement Speed %s");
        translate.add("stat.dominatus.movement_speed.desc",
                "Determines how quickly you can move around the world.");
        translate.add("stat.dominatus.movement_speed.details",
                "Base movement speed is 4 blocks/sec. Values above this will increase your travel speed.");

        // Attack Damage stat
        translate.add("stat.dominatus.attack_damage", "Attack Damage");
        translate.add("stat.dominatus.attack_damage.label", "Attack Damage %s");
        translate.add("stat.dominatus.attack_damage.desc",
                "The base damage you deal with physical attacks.");
        translate.add("stat.dominatus.attack_damage.details",
                "This can be increased with weapons, strength potions, and enchantments.");

// Attack Speed stat
        translate.add("stat.dominatus.attack_speed", "Attack Speed");
        translate.add("stat.dominatus.attack_speed.label", "Attack Speed %s");
        translate.add("stat.dominatus.attack_speed.desc",
                "Determines how quickly you can swing your weapon.");
        translate.add("stat.dominatus.attack_speed.details",
                "Base attack speed is 4 attacks per second. Higher values decrease the cooldown between attacks.");


        // Back Attack stat
        translate.add("stat.dominatus.back_attack", "Back Attack Damage");
        translate.add("stat.dominatus.back_attack.label", "Back Attack Damage %s");
        translate.add("stat.dominatus.back_attack.desc",
                "Increases damage dealt when attacking enemies from behind.");
        translate.add("stat.dominatus.back_attack.details",
                "Each point increases back attack damage by 1% of the base damage.");

        // Air Attack stat
        translate.add("stat.dominatus.air_attack", "Air Attack Damage");
        translate.add("stat.dominatus.air_attack.label", "Air Attack Damage %s");
        translate.add("stat.dominatus.air_attack.desc",
                "Boosts damage dealt to enemies while they are airborne.");
        translate.add("stat.dominatus.air_attack.details",
                "Each point increases air attack damage by 1% of the base damage.");

        // Down Attack stat
        translate.add("stat.dominatus.down_attack", "Down Attack Damage");
        translate.add("stat.dominatus.down_attack.label", "Down Attack Damage %s");
        translate.add("stat.dominatus.down_attack.desc",
                "Enhances damage dealt to enemies that are knocked down.");
        translate.add("stat.dominatus.down_attack.details",
                "Each point increases down attack damage by 1% of the base damage.");

        // Player vs Entity Damage stat
        translate.add("stat.dominatus.player_vers_entity_damage", "PvE Damage");
        translate.add("stat.dominatus.player_vers_entity_damage.label", "PvE Damage %s");
        translate.add("stat.dominatus.player_vers_entity_damage.desc",
                "Increases damage dealt to non-player entities (monsters, animals, etc.).");
        translate.add("stat.dominatus.player_vers_entity_damage.details",
                "Each point adds a flat amount to damage against entities.");

        // Player vs Player Damage stat
        translate.add("stat.dominatus.player_vers_player_damage", "PvP Damage");
        translate.add("stat.dominatus.player_vers_player_damage.label", "PvP Damage %s");
        translate.add("stat.dominatus.player_vers_player_damage.desc",
                "Increases damage dealt to other players in combat.");
        translate.add("stat.dominatus.player_vers_player_damage.details",
                "Each point adds a flat amount to damage against players.");

        //RWI COMPAT
        translate.add("header.dominatus.ranged_stats", "RANGED STATISTICS");
        translate.add("stat.dominatus.ranged_damage", "Ranged Damage");
        translate.add("stat.dominatus.ranged_damage.label", "Ranged Damage %s");
        translate.add("stat.dominatus.ranged_damage.desc",
                "The base damage dealt by ranged weapons like bows or crossbows.");
        translate.add("stat.dominatus.ranged_damage.details",
                "Each point increases the damage of ranged attacks.");

        // Pull Time stat
        translate.add("stat.dominatus.pull_time", "Draw Time");
        translate.add("stat.dominatus.pull_time.label", "Draw Time %s ticks");
        translate.add("stat.dominatus.pull_time.desc",
                "Time required to fully draw a ranged weapon.");
        translate.add("stat.dominatus.pull_time.details",
                "Lower values mean faster drawing. Measured in ticks (20 ticks = 1 second).");

        // Ranged Haste stat
        translate.add("stat.dominatus.ranged_haste", "Ranged Haste");
        translate.add("stat.dominatus.ranged_haste.label", "Ranged Haste %s");
        translate.add("stat.dominatus.ranged_haste.desc",
                "Increases the speed of drawing and firing ranged weapons.");
        translate.add("stat.dominatus.ranged_haste.details",
                "Each point reduces the time between ranged attacks.");

        // Velocity stat
        translate.add("stat.dominatus.velocity", "Projectile Velocity");
        translate.add("stat.dominatus.velocity.label", "Projectile Velocity %s");
        translate.add("stat.dominatus.velocity.desc",
                "Speed of projectiles fired from ranged weapons.");
        translate.add("stat.dominatus.velocity.details",
                "Higher values increase the travel speed of arrows or other projectiles.");
        // Unknown stat fallback
        translate.add("stat.dominatus.unknown", "Unknown Stat");
        translate.add("stat.dominatus.unknown.desc", "No information available for this stat.");
    }
}
