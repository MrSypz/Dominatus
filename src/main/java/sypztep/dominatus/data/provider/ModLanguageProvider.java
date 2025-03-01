package sypztep.dominatus.data.provider;

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
        // Miscellaneous
        translate.add("item.dominatus.loss_fragment", "Loss Fragment");
        translate.add("item.dominatus.loss_fragment.desc", "Combine to obtain Refined Armor Enforge.");
        translate.add("item.dominatus.lahav_fragment", "Lahav Fragment");
        translate.add("item.dominatus.lahav_fragment.desc", "Combine to obtain Refined Weapon Enforge.");
        translate.add("item.dominatus.mahilnant", "Mahilnant");
        translate.add("item.dominatus.mahilnant.desc", "A mysterious item.");
        translate.add("item.dominatus.moonlight_crescent", "Moonlight Crescent");
        translate.add("item.dominatus.moonlight_crescent.desc", "Used to restore the items. durability.");

        // Screen
        translate.add("dominatus.refinebutton_tooltip", "Refinement");
        translate.add("dominatus.reform.screen", "Reform");

        // Tooltip
        translate.add("dominatus.attribute.modifier.armor", "+%s (%s) Armor");
        translate.add("dominatus.attribute.modifier.damage", "%s (%s) Attack Damage");
        translate.add("dominatus.attribute.modifier.accuracy", "%s Accuracy");
        translate.add("dominatus.attribute.modifier.evasion", "%s Evasion");
    }
}
