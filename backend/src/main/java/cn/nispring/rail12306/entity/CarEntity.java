package cn.nispring.rail12306.entity;

public class CarEntity {

    private Long id;
    private String style;
    private String code;

    public CarEntity() {
    }

    public CarEntity(Long id, String style, String code) {
        this.id = id;
        this.style = style;
        this.code = code;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
