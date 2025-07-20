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
        translator.add(key + "attack_power", "Attack Power: $ap");
        translator.add(key + "attack_speed", "Attack Speed: $asp");
        translator.add(key + "accuracy", "Accuracy: $acc");
        translator.add(key + "critical_damage", "Critical Damage: $cdmg %");
        translator.add(key + "critical_chance", "Critical Chance: $ccn %");
        translator.add(key + "pve_damage", "PVE Damage: $pve");
        translator.add(key + "pvp_damage", "PVP Damage: $pvp");

        // MAGIC
        translator.add(key + "magic_damage", "Magic Damage: $mdmg");

        // VITALITY
        translator.add(key + "health", "Health: $hp");
        translator.add(key + "max_health", "Max Health: $maxhp");
        translator.add(key + "defense", "Defense: $dp");
        translator.add(key + "nature_health_regen", "Nature Health Regen: $nhrg");
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
        translator.add(key + "physical_resistance", "Physical Resistance: $physis %");
        translator.add(key + "projectile_resistance", "Projectile Resistance: $projsis %");
    }
    private void generateConfigTranslations(TranslationBuilder translator) {
        String configBase = "text.autoconfig.dominatus";

        // Main config title
        translator.add(configBase + ".title", "Dominatus Configuration");

        // Category translations
        generateClientFeatureConfig(translator, configBase);
        generateGameplayConfig(translator, configBase);
        generateStatConfig(translator, configBase);
        generateDeathPenaltyConfig(translator, configBase);
    }

    private void generateClientFeatureConfig(TranslationBuilder translator, String base) {
        String categoryBase = base + ".category.feature-client";

        // Category
        translator.add(categoryBase, "Client Features");

        // Options
        translator.add(base + ".option.damageCritIndicator", "Critical Hit Indicator");
        translator.add(base + ".option.damageCritIndicator.@Tooltip", "Show visual indicator when landing critical hits");

        translator.add(base + ".option.missingIndicator", "Miss Indicator");
        translator.add(base + ".option.missingIndicator.@Tooltip", "Show visual indicator when attacks miss");

        translator.add(base + ".option.critDamageColor", "Critical Hit Color");
        translator.add(base + ".option.critDamageColor.@Tooltip", "Color for critical hit damage indicators");

        translator.add(base + ".option.enableToastNotifications", "Toast Notifications");
        translator.add(base + ".option.enableToastNotifications.@Tooltip", "Show toast notifications instead of chat messages");

        translator.add(base + ".option.toastPositionLeft", "Toast Position Left");
        translator.add(base + ".option.toastPositionLeft.@Tooltip", "Show toasts on the left side of screen");

        translator.add(base + ".option.toastYOffset", "Toast Y Offset");
        translator.add(base + ".option.toastYOffset.@Tooltip", "Vertical offset from top of screen for toast notifications");

        translator.add(base + ".option.toastMargin", "Toast Margin");
        translator.add(base + ".option.toastMargin.@Tooltip", "Distance from screen edge for toast notifications");
    }

    private void generateGameplayConfig(TranslationBuilder translator, String base) {
        String categoryBase = base + ".category.statconfig_gameplay";

        // Category
        translator.add(categoryBase, "Gameplay Settings");

        // Options
        translator.add(base + ".option.maxLevel", "Maximum Level");
        translator.add(base + ".option.maxLevel.@Tooltip", "The highest level players can reach");

        translator.add(base + ".option.EXP_MAP", "Experience Table");
        translator.add(base + ".option.EXP_MAP.@Tooltip", "Experience required for each level");

        translator.add(base + ".option.startStatpoints", "Starting Benefit Points");
        translator.add(base + ".option.startStatpoints.@Tooltip", "Number of benefit points new players start with");
    }

    private void generateStatConfig(TranslationBuilder translator, String base) {
        String categoryBase = base + ".category.statconfig";

        // Category
        translator.add(categoryBase, "Stat Configuration");

        // Options
        translator.add(base + ".option.tooltipinfo", "Show Tooltip Info");
        translator.add(base + ".option.tooltipinfo.@Tooltip", "Display detailed information in tooltips");

        translator.add(base + ".option.barColor", "Progress Bar Color");
        translator.add(base + ".option.barColor.@Tooltip", "Color of the experience progress bar");

        translator.add(base + ".option.barBGColor", "Progress Bar Background");
        translator.add(base + ".option.barBGColor.@Tooltip", "Background color of the experience progress bar");

        translator.add(base + ".option.renderStyle", "Render Style");
        translator.add(base + ".option.renderStyle.@Tooltip", "Visual style for progress bars");

        // Render style enum values
        translator.add(base + ".option.renderStyle.BAR", "Bar Style");
        translator.add(base + ".option.renderStyle.SLATE", "Slate Style");
    }

    private void generateDeathPenaltyConfig(TranslationBuilder translator, String base) {
        String categoryBase = base + ".category.death_penalty";

        // Category
        translator.add(categoryBase, "Death Penalty");

        // Options
        translator.add(base + ".option.deathPenaltyPercentage", "Death Penalty Percentage");
        translator.add(base + ".option.deathPenaltyPercentage.@Tooltip", "Percentage of next level experience lost on death");

        translator.add(base + ".option.enableDeathPenalty", "Enable Death Penalty");
        translator.add(base + ".option.enableDeathPenalty.@Tooltip", "Whether players lose experience when killed by monsters");
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