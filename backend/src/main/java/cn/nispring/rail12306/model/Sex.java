package cn.nispring.rail12306.model;

public enum Sex {

    MALE("M", "男"),
    FEMALE("F", "女")
    ;

    private final String code;
    private final String displayName;

    Sex(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Sex fromCode(String code) {
        for (Sex sex : values()) {
            if (sex.code.equals(code)) {
                return sex;
            }
        }
        throw new IllegalArgumentException("Unknown sex code: " + code);
    }
}
