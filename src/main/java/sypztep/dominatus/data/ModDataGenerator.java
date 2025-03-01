package sypztep.dominatus.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import sypztep.dominatus.data.provider.ModDamageTypeTagProvider;
import sypztep.dominatus.data.provider.ModItemTagProvider;
import sypztep.dominatus.data.provider.ModLanguageProvider;
import sypztep.dominatus.data.provider.RefinementDataProvider;

public class ModDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(ModDamageTypeTagProvider::new);
        pack.addProvider(ModLanguageProvider::new);
        pack.addProvider(RefinementDataProvider::new);
        pack.addProvider(ModItemTagProvider::new);
    }
}
