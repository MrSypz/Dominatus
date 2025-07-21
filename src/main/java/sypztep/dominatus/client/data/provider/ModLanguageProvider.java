package sypztep.dominatus.client.data.provider;

import com.sun.java.accessibility.util.Translator;
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
        // Existing particle text translations
        translate.add("dominatus.text.missing", "Missing");
        translate.add("dominatus.text.critical", "Critical");
        translate.add("dominatus.text.back", "Back Attack");
        translate.add("dominatus.text.air", "Air Attack");

        playerInfo(translate);
        generateConfigTranslations(translate);
        generateAttributeTranslations(translate);
    }

    private void playerInfo(TranslationBuilder translator) {
        String key = "dominatus.info.";
        translator.add("dominatus.gui.player_info.header", "Player Stats");
        translator.add("dominatus.gui.player_info.header_level", "Stats");
        // Section headers
        translator.add(key + "header_1", "MELEE");
        translator.add(key + "header_2", "MAGIC");
        translator.add(key + "header_3", "VITALITY");
        translator.add(key + "header_4", "STATS");
        translator.add(key + "header_5", "RESISTANCE");

        // MELEE
        translator.add(key + "physical", "Physical: $phyd");
        translator.add(key + "melee_damage", "Melee Damage: $meleed");
        translator.add(key + "projectile_damage", "Projectile Damage: $projd");
        translator.add(key + "attack_speed", "Attack Speed: $asp");
        translator.add(key + "accuracy", "Accuracy: $acc");
        translator.add(key + "critical_damage", "Critical Damage: $cdmg %");
        translator.add(key + "critical_chance", "Critical Chance: $ccn %");

        // MAGIC
        translator.add(key + "magic_damage", "Magic Damage: $mdmg");

        // VITALITY
        translator.add(key + "health", "Health: $hp");
        translator.add(key + "max_health", "Max Health: $maxhp");
        translator.add(key + "defense", "Defense: $dp");
        translator.add(key + "nature_health_regen", "Nature Health Regen: $nhrg");
        translator.add(key + "heal_effective", "Nature Health Regen: $hef");
        translator.add(key + "evasion", "Evasion: $eva");

        // STATS
        translator.add(key + "strength", "Strength: $str");
        translator.add(key + "agility", "Agility: $agi");
        translator.add(key + "vitality", "Vitality: $vit");
        translator.add(key + "intelligence", "Intelligence: $int");
        translator.add(key + "dexterity", "Dexterity: $dex");
        translator.add(key + "luck", "Luck: $luk");

        // RESISTANCE
        translator.add(key + "magic_resistance", "Magic Resistance: $mresis %");
    }
    private void generateConfigTranslations(TranslationBuilder translator) {
        String base = "text.autoconfig.dominatus";

        // Main config title
        translator.add(base + ".title", "Dominatus Configuration");

        // =====================================
        // CATEGORIES
        // =====================================

        translator.add(base + ".category.client_features", "Client Features");
        translator.add(base + ".category.notifications", "Notifications");
        translator.add(base + ".category.progress_bars", "Progress Bars");
        translator.add(base + ".category.gameplay", "Gameplay Settings");
        translator.add(base + ".category.death_penalty", "Death Penalty");

        // =====================================
        // CLIENT FEATURES
        // =====================================

        translator.add(base + ".option.damageCritIndicator", "Critical Hit Indicator");

        translator.add(base + ".option.missingIndicator", "Miss Indicator");

        translator.add(base + ".option.critDamageColor", "Critical Hit Color");

        translator.add(base + ".option.tooltipinfo", "Show Tooltip Info");

        // =====================================
        // NOTIFICATIONS
        // =====================================

        translator.add(base + ".option.enableToastNotifications", "Toast Notifications");

        translator.add(base + ".option.toastPositionLeft", "Toast Position Left");

        translator.add(base + ".option.toastYOffset", "Toast Y Offset");

        translator.add(base + ".option.toastMargin", "Toast Margin");

        translator.add(base + ".option.toastScale", "Toast Scale");


        // =====================================
        // PROGRESS BARS
        // =====================================

        translator.add(base + ".option.barColor", "Progress Bar Color");

        translator.add(base + ".option.barBGColor", "Progress Bar Background");

        translator.add(base + ".option.renderStyle", "Render Style");

        // Render style enum values
        translator.add(base + ".option.renderStyle.BAR", "Bar Style");
        translator.add(base + ".option.renderStyle.SLATE", "Slate Style");

        // =====================================
        // GAMEPLAY SETTINGS
        // =====================================

        translator.add(base + ".option.maxLevel", "Maximum Level");

        translator.add(base + ".option.startStatpoints", "Starting Benefit Points");

        translator.add(base + ".option.EXP_MAP", "Experience Table");

        // =====================================
        // DEATH PENALTY
        // =====================================

        translator.add(base + ".option.enableDeathPenalty", "Enable Death Penalty");

        translator.add(base + ".option.deathPenaltyPercentage", "Death Penalty Percentage");
    }

    private void generateAttributeTranslations(TranslationBuilder translator) {
        translator.add("attribute.name.health_regen", "Health Regeneration");
        translator.add("attribute.name.accuracy", "Accuracy");
        translator.add("attribute.name.evasion", "Evasion");
        translator.add("attribute.name.crit_damage", "Critical Damage");
        translator.add("attribute.name.crit_chance", "Critical Chance");
        translator.add("attribute.name.back_attack", "Back Attack Damage");
        translator.add("attribute.name.melee_attack_damage", "Melee Attack Damage");
        translator.add("attribute.name.magic_attack_damage", "Magic Attack Damage");
        translator.add("attribute.name.projectile_attack_damage", "Projectile Attack Damage");
        translator.add("attribute.name.magic_resistance", "Magic Resistance");
        translator.add("attribute.name.physical_resistance", "Physical Resistance");
    }
}