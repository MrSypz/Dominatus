package sypztep.dominatus.common.system.skill.active;

public record SkillResult(boolean success, String message) {
    public static SkillResult success(String message) {
        return new SkillResult(true, message);
    }

    public static SkillResult failure(String message) {
        return new SkillResult(false, message);
    }
}
