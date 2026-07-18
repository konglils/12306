package cn.nispring.rail12306.model;

public enum SeatType {

    HARD_SEAT("1", "硬座"),
    SOFT_SEAT("2", "软座"),
    HARD_SLEEPER("3", "硬卧"),
    SOFT_SLEEPER("4", "软卧"),
    PREMIUM_SOFT_SLEEPER("6", "高级软卧"),
    FIRST_CLASS_SOFT_SEAT("7", "一等软座"),
    SECOND_CLASS_SOFT_SEAT("8", "二等软座"),
    BUSINESS("9", "商务"),
    PREMIUM_MOTOR_SLEEPER("A", "高级动卧"),
    PREMIUM_FIRST_CLASS("D", "优选一等"),
    MOTOR_SLEEPER("F", "动卧"),
    PRIVATE_COMPARTMENT("H", "一人软包"),
    FIRST_CLASS_SLEEPER("I", "一等卧"),
    SECOND_CLASS_SLEEPER("J", "二等卧"),
    FIRST_CLASS("M", "一等"),
    SECOND_CLASS("O", "二等"),
    PREMIUM_CLASS("P", "特等"),
    MULTI_FUNCTION_SEAT("Q", "多功能座")
    ;

    private final String code;
    private final String displayName;

    SeatType(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static SeatType fromCode(String code) {
        for (SeatType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown seat type code: " + code);
    }
}
