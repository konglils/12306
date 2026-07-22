package cn.nispring.rail12306.entity;

import java.time.LocalDate;
import java.time.LocalTime;

public class StopEntity {

    private LocalDate trainDate;
    private Long trainId;
    private Integer stopIdx;
    private Long stationId;
    private String trainCode;
    private Integer arriveDay;
    private LocalTime arriveTime;
    private Integer startDay;
    private LocalTime startTime;

    public StopEntity() {
    }

    public StopEntity(LocalDate trainDate, Long trainId, Integer stopIdx, Long stationId,
                      String trainCode, Integer arriveDay, LocalTime arriveTime,
                      Integer startDay, LocalTime startTime) {
        this.trainDate = trainDate;
        this.trainId = trainId;
        this.stopIdx = stopIdx;
        this.stationId = stationId;
        this.trainCode = trainCode;
        this.arriveDay = arriveDay;
        this.arriveTime = arriveTime;
        this.startDay = startDay;
        this.startTime = startTime;
    }

    public LocalDate getTrainDate() {
        return trainDate;
    }

    public void setTrainDate(LocalDate trainDate) {
        this.trainDate = trainDate;
    }

    public Long getTrainId() {
        return trainId;
    }

    public void setTrainId(Long trainId) {
        this.trainId = trainId;
    }

    public Integer getStopIdx() {
        return stopIdx;
    }

    public void setStopIdx(Integer stopIdx) {
        this.stopIdx = stopIdx;
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }

    public String getTrainCode() {
        return trainCode;
    }

    public void setTrainCode(String trainCode) {
        this.trainCode = trainCode;
    }

    public Integer getArriveDay() {
        return arriveDay;
    }

    public void setArriveDay(Integer arriveDay) {
        this.arriveDay = arriveDay;
    }

    public LocalTime getArriveTime() {
        return arriveTime;
    }

    public void setArriveTime(LocalTime arriveTime) {
        this.arriveTime = arriveTime;
    }

    public Integer getStartDay() {
        return startDay;
    }

    public void setStartDay(Integer startDay) {
        this.startDay = startDay;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }
}
