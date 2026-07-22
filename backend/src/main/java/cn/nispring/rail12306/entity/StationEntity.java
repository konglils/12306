package cn.nispring.rail12306.entity;

public class StationEntity {

    private Long id;
    private Long areaId;
    private String telecode;
    private String name;

    public StationEntity() {
    }

    public StationEntity(Long id, Long areaId, String telecode, String name) {
        this.id = id;
        this.areaId = areaId;
        this.telecode = telecode;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public String getTelecode() {
        return telecode;
    }

    public void setTelecode(String telecode) {
        this.telecode = telecode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
