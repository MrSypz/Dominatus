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
        // Config
        translate.add("text.autoconfig.dominatus.title", "Dominatus Config");
        translate.add("text.autoconfig.dominatus.option.damageCritIndicator", "Crit Indicator");
        translate.add("text.autoconfig.dominatus.option.missingIndicator", "Missing Indicator");
        translate.add("text.autoconfig.dominatus.option.critDamageColor", "Crit Damage Color");
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
        // Attributes
        translate.add("attribute.name.evasion", "Evasion");
        translate.add("attribute.name.accuracy", "Accuracy");
        translate.add("attribute.name.crit_chance", "Critical Chance");
        translate.add("attribute.name.crit_damage", "Critical Damage");
        translate.add("attribute.name.player_vers_entity_damage", "PvE Damage");
        translate.add("attribute.name.player_vers_player_damage", "PvP Damage");
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
    }
    private void generateStatsScreenTranslations(TranslationBuilder translate) {
        translate.add("screen.dominatus.player_info","Player Information");
        // Tab and panels
        translate.add("tab.dominatus.stats", "Stats");
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
                "Each point of Accuracy improves your hit chance by approximately 0.125% against enemies with equal level.");
        // Evasion stat
        translate.add("stat.dominatus.evasion", "Evasion");
        translate.add("stat.dominatus.evasion.label", "Evasion %s");
        translate.add("stat.dominatus.evasion.desc",
                "Improves your chance to dodge incoming attacks. Effective against enemies with low accuracy.");
        translate.add("stat.dominatus.evasion.details",
                "Each point of Evasion grants approximately 0.15% chance to completely avoid damage from attacks.");
        // Critical Hit Chance stat
        translate.add("stat.dominatus.crit_chance", "Critical Hit Chance");
        translate.add("stat.dominatus.crit_chance.label", "Critical Hit Chance %s");
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
        translate.add("stat.dominatus.health", "Health");
        translate.add("stat.dominatus.health.label", "Health %s");
        translate.add("stat.dominatus.health.desc",
                "Your total health points. When this reaches zero, you die.");
        translate.add("stat.dominatus.health.details",
                "Health can be increased through leveling, equipment, and certain foods.");

        // Armor stat
        translate.add("stat.dominatus.armor", "Armor");
        translate.add("stat.dominatus.armor.label", "Armor %s");
        translate.add("stat.dominatus.armor.desc",
                "Reduces damage taken from physical attacks.");
        translate.add("stat.dominatus.armor.details",
                "Each point of armor reduces incoming physical damage by approximately 4%.");

        // Movement Speed stat
        translate.add("stat.dominatus.movement_speed", "Movement Speed");
        translate.add("stat.dominatus.movement_speed.label", "Movement Speed %s");
        translate.add("stat.dominatus.movement_speed.desc",
                "Determines how quickly you can move around the world.");
        translate.add("stat.dominatus.movement_speed.details",
                "The base movement speed is 0.10. Values above this will make you move faster.");

        // Attack Damage stat
        translate.add("stat.dominatus.attack_damage", "Attack Damage");
        translate.add("stat.dominatus.attack_damage.label", "Attack Damage %s");
        translate.add("stat.dominatus.attack_damage.desc",
                "The base damage you deal with physical attacks.");
        translate.add("stat.dominatus.attack_damage.details",
                "This can be increased with weapons, strength potions, and enchantments.");

        // Unknown stat fallback
        translate.add("stat.dominatus.unknown", "Unknown Stat");
        translate.add("stat.dominatus.unknown.desc", "No information available for this stat.");


    }
}
