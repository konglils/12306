package cn.nispring.rail12306.model;

public enum DiscountType {

    ADULT(1, "成人"),
    CHILD(2, "儿童"),
    STUDENT(3, "学生"),
    DISABLED_SOLDIER(4, "残疾军人"),
    ;

    private final int code;
    private final String displayName;

    DiscountType(int code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    @com.fasterxml.jackson.annotation.JsonValue
    public int getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    @com.fasterxml.jackson.annotation.JsonCreator
    public static DiscountType fromCode(int code) {
        for (DiscountType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown discount type code: " + code);
    }
}
