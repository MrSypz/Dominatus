package sypztep.dominatus.common.util.level;

import net.minecraft.nbt.NbtCompound;
import sypztep.dominatus.ModConfig;

class PlayerBenefitSystem {
    private int availableBenefits;
    private final BenefitCalculator calculator;

    public PlayerBenefitSystem() {
        this(new DefaultBenefitCalculator());
    }

    public PlayerBenefitSystem(BenefitCalculator calculator) {
        this.calculator = calculator;
        this.availableBenefits = ModConfig.startStatpoints;
    }

    public void onLevelUp(int newLevel) {
        int benefitsGained = calculator.getBenefitsForLevel(newLevel);
        addBenefits(benefitsGained);
    }

    public int getAvailableBenefits() {
        return availableBenefits;
    }

    public void addBenefits(int benefits) {
        this.availableBenefits += benefits;
    }

    public boolean spendBenefits(int benefits) {
        if (availableBenefits >= benefits) {
            this.availableBenefits -= benefits;
            return true;
        }
        return false;
    }

    public void writeToNbt(NbtCompound tag) {
        tag.putInt("AvailableBenefits", availableBenefits);
    }

    public void readFromNbt(NbtCompound tag) {
        this.availableBenefits = tag.getInt("AvailableBenefits");
    }
}
