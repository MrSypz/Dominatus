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

}