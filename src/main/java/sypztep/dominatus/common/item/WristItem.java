package sypztep.dominatus.common.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.wispforest.accessories.api.AccessoryItem;
import io.wispforest.accessories.api.components.AccessoriesDataComponents;
import io.wispforest.accessories.api.components.AccessoryRenderTransformations;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.common.init.ModDataComponents;
import sypztep.dominatus.common.init.ModEntityAttributes;
import sypztep.dominatus.common.util.refinesystem.RefinementManager;

import java.util.List;

public class WristItem extends AccessoryItem {
    public WristItem(Settings properties) {
        super(properties.component(
                AccessoriesDataComponents.RENDER_TRANSFORMATIONS,
                AccessoryRenderTransformations.builder()
                        .translation(new Vector3f(0,0.5f,-0.05f))
                        .rotation(new Quaternionf().rotateXYZ(
                                (float) Math.toRadians(0.0f),
                                (float) Math.toRadians(2.0f),
                                (float) Math.toRadians(0.0f)
                        ))
                        .scale(new Vector3f(1.65f, 1.65f, 1.65f))
                        .build()
        ).maxCount(1));
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference reference) {
        super.onEquip(stack, reference);
        Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> modifiers = HashMultimap.create();
        if (!stack.contains(ModDataComponents.REFINEMENT)) return;
        int refinable = RefinementManager.getRefinement(stack).refine();
        if (refinable > 0) {
        modifiers.put(
                ModEntityAttributes.PLAYER_VERS_PLAYER_DAMAGE,
                new EntityAttributeModifier(
                        Dominatus.id("yuria_bracket_pvp_bonus"),
                        refinable,
                        EntityAttributeModifier.Operation.ADD_VALUE
                )
        );

        reference.entity().getAttributes().addTemporaryModifiers(modifiers);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);

        if (stack.contains(ModDataComponents.REFINEMENT)) {
            int refinementLevel = RefinementManager.getRefinement(stack).refine();

            if (refinementLevel > 0) {
                tooltip.add(Text.empty());

                tooltip.add(Text.literal("✦ SPECIAL EFFECT ✦").formatted(Formatting.AQUA, Formatting.BOLD));

                tooltip.add(Text.literal(" Player Damage: ").formatted(Formatting.WHITE)
                        .append(Text.literal("+" + refinementLevel + "%").formatted(Formatting.RED, Formatting.BOLD)));

                tooltip.add(Text.empty());
                tooltip.add(Text.literal("\"A mystical bracelet once worn by").formatted(Formatting.DARK_GRAY, Formatting.ITALIC));
                tooltip.add(Text.literal("the warriors of Yuria. Its power").formatted(Formatting.DARK_GRAY, Formatting.ITALIC));
                tooltip.add(Text.literal("against other fighters grows with").formatted(Formatting.DARK_GRAY, Formatting.ITALIC));
                tooltip.add(Text.literal("each successful refinement.\"").formatted(Formatting.DARK_GRAY, Formatting.ITALIC));

                tooltip.add(Text.empty());
            }
        }
    }
}