package sypztep.dominatus.common.util.level.benefit;

class DefaultBenefitCalculator implements BenefitCalculator {
    @Override
    public int getBenefitsForLevel(int level) {
        return level / 5 + 3;
    }
}
