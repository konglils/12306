package cn.nispring.rail12306.entity;

import java.time.LocalTime;

public class TrainStationEntity {

    private Long id;
    private Long trainId;
    private Long stationId;
    private String trainCode;
    private int sequence;
    private int arriveDay;
    private LocalTime arriveTime;
    private int startDay;
    private LocalTime startTime;

    public TrainStationEntity() {
    }

    public TrainStationEntity(Long id, Long trainId, Long stationId, String trainCode,
                              int sequence, int arriveDay, LocalTime arriveTime,
                              int startDay, LocalTime startTime) {
        this.id = id;
        this.trainId = trainId;
        this.stationId = stationId;
        this.trainCode = trainCode;
        this.sequence = sequence;
        this.arriveDay = arriveDay;
        this.arriveTime = arriveTime;
        this.startDay = startDay;
        this.startTime = startTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTrainId() {
        return trainId;
    }

    public void setTrainId(Long trainId) {
        this.trainId = trainId;
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

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public int getArriveDay() {
        return arriveDay;
    }

    public void setArriveDay(int arriveDay) {
        this.arriveDay = arriveDay;
    }

    public LocalTime getArriveTime() {
        return arriveTime;
    }

    public void setArriveTime(LocalTime arriveTime) {
        this.arriveTime = arriveTime;
    }

    public int getStartDay() {
        return startDay;
    }

    public void setStartDay(int startDay) {
        this.startDay = startDay;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }
}
