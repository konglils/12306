package cn.nispring.rail12306.model;

public enum IdType {

    CHINA_RESIDENT(1, "中国居民身份证"),
    HK_MO_RESIDENT(2, "港澳居民居住证"),
    TW_RESIDENT(3, "台湾居民居住证"),
    FOREIGNER_RESIDENT(4, "外国人永久居留身份证"),
    HK_MO_PASS(5, "港澳居民来往内地通行证（含非中国籍）"),
    TW_PASS(6, "台湾居民来往大陆通行证"),
    CHINA_PASSPORT(7, "中国护照"),
    FOREIGN_PASSPORT(8, "外国护照")
    ;

    private final int code;
    private final String displayName;

    IdType(int code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public int getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static IdType fromCode(int code) {
        for (IdType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown id type code: " + code);
    }
}
