package cn.nispring._12306.model;

public enum PassengerStatus {

    PENDING(1, "待核验"),
    PASSED(2, "已通过"),
    ;

    private final int code;
    private final String displayName;

    PassengerStatus(int code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public int getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static PassengerStatus fromCode(int code) {
        for (PassengerStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown passenger status code: " + code);
    }
}
