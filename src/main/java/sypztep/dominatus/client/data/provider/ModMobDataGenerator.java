package sypztep.dominatus.client.data.provider;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import sypztep.dominatus.Dominatus;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class ModMobDataGenerator implements DataProvider {

    private final FabricDataOutput output;

    public ModMobDataGenerator(FabricDataOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        return generateMobExp(writer);
    }

    private CompletableFuture<?> generateMobExp(DataWriter writer) {
        CompletableFuture<?>[] futures = new CompletableFuture[]{
                // Hostile Mobs
                createMobExpFile(writer, EntityType.ZOMBIE, 5),
                createMobExpFile(writer, EntityType.SKELETON, 5),
                createMobExpFile(writer, EntityType.SPIDER, 5),
                createMobExpFile(writer, EntityType.CREEPER, 5),
                createMobExpFile(writer, EntityType.ENDERMAN, 5),
                createMobExpFile(writer, EntityType.WITCH, 5),

                // Nether Mobs
                createMobExpFile(writer, EntityType.ZOMBIFIED_PIGLIN, 5),
                createMobExpFile(writer, EntityType.PIGLIN, 5),
                createMobExpFile(writer, EntityType.PIGLIN_BRUTE, 10),
                createMobExpFile(writer, EntityType.BLAZE, 10),
                createMobExpFile(writer, EntityType.GHAST, 5),
                createMobExpFile(writer, EntityType.WITHER_SKELETON, 5),
                createMobExpFile(writer, EntityType.MAGMA_CUBE, 4),
                createMobExpFile(writer, EntityType.HOGLIN, 5),
                createMobExpFile(writer, EntityType.ZOGLIN, 5),
                createMobExpFile(writer, EntityType.STRIDER, 1),

                // End Mobs
                createMobExpFile(writer, EntityType.ENDERMITE, 3),
                createMobExpFile(writer, EntityType.SHULKER, 5),

                // Ocean Mobs
                createMobExpFile(writer, EntityType.DROWNED, 5),
                createMobExpFile(writer, EntityType.GUARDIAN, 10),
                createMobExpFile(writer, EntityType.ELDER_GUARDIAN, 10),

                // Cave Mobs
                createMobExpFile(writer, EntityType.CAVE_SPIDER, 5),
                createMobExpFile(writer, EntityType.SILVERFISH, 5),

                // Illagers
                createMobExpFile(writer, EntityType.PILLAGER, 5),
                createMobExpFile(writer, EntityType.VINDICATOR, 5),
                createMobExpFile(writer, EntityType.EVOKER, 10),
                createMobExpFile(writer, EntityType.VEX, 3),
                createMobExpFile(writer, EntityType.RAVAGER, 20),
                createMobExpFile(writer, EntityType.ILLUSIONER, 5),

                // Slimes
                createMobExpFile(writer, EntityType.SLIME, 4),

                // Bosses
                createMobExpFile(writer, EntityType.WITHER, 50),
                createMobExpFile(writer, EntityType.ENDER_DRAGON, 500),
                createMobExpFile(writer, EntityType.WARDEN, 100),

                // Passive Mobs
                createMobExpFile(writer, EntityType.COW, 1),
                createMobExpFile(writer, EntityType.PIG, 1),
                createMobExpFile(writer, EntityType.SHEEP, 1),
                createMobExpFile(writer, EntityType.CHICKEN, 1),
                createMobExpFile(writer, EntityType.RABBIT, 1),
                createMobExpFile(writer, EntityType.HORSE, 1),
                createMobExpFile(writer, EntityType.DONKEY, 1),
                createMobExpFile(writer, EntityType.MULE, 1),
                createMobExpFile(writer, EntityType.LLAMA, 1),
                createMobExpFile(writer, EntityType.TRADER_LLAMA, 1),
                createMobExpFile(writer, EntityType.VILLAGER, 0),
                createMobExpFile(writer, EntityType.WANDERING_TRADER, 0),

                // Neutral Mobs
                createMobExpFile(writer, EntityType.WOLF, 1),
                createMobExpFile(writer, EntityType.POLAR_BEAR, 1),
                createMobExpFile(writer, EntityType.PANDA, 1),
                createMobExpFile(writer, EntityType.BEE, 1),
                createMobExpFile(writer, EntityType.GOAT, 1),
                createMobExpFile(writer, EntityType.AXOLOTL, 1),
                createMobExpFile(writer, EntityType.GLOW_SQUID, 1),
                createMobExpFile(writer, EntityType.SQUID, 1),

                // 1.19+ Mobs
                createMobExpFile(writer, EntityType.ALLAY, 0),
                createMobExpFile(writer, EntityType.FROG, 1),
                createMobExpFile(writer, EntityType.TADPOLE, 1),

                // 1.20+ Mobs
                createMobExpFile(writer, EntityType.CAMEL, 1),
                createMobExpFile(writer, EntityType.SNIFFER, 1),

                // 1.21+ Mobs
                createMobExpFile(writer, EntityType.ARMADILLO, 1),
                createMobExpFile(writer, EntityType.BOGGED, 5),
                createMobExpFile(writer, EntityType.BREEZE, 10)
        };

        return CompletableFuture.allOf(futures);
    }

    private CompletableFuture<?> createMobExpFile(DataWriter writer, EntityType<?> entityType, int expReward) {
        Identifier entityId = Registries.ENTITY_TYPE.getId(entityType);
        String namespace = entityId.getNamespace();
        String path = entityId.getPath();

        // Create the file path: data/dominatus/mobexp/namespace/path.json
        Path filePath = this.output.getPath()
                .resolve("data")
                .resolve(Dominatus.MODID)
                .resolve("mobexp")
                .resolve(namespace)
                .resolve(path + ".json");

        JsonObject jsonObject = createJsonObject(expReward);

        return DataProvider.writeToPath(writer, jsonObject, filePath);
    }

    private JsonObject createJsonObject(int expReward) {
        JsonObject root = new JsonObject();
        root.addProperty("expReward", expReward);
        return root;
    }

    @Override
    public String getName() {
        return "Mob Exp Data";
    }
}