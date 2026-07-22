package cn.nispring.rail12306.entity;

import cn.nispring.rail12306.model.SeatType;

import java.time.LocalDate;

public class PriceEntity {

    private LocalDate trainDate;
    private Long fromAreaId;
    private Long toAreaId;
    private Long trainId;
    private Long fromStationId;
    private Long toStationId;
    private Integer fromStopIdx;
    private Integer toStopIdx;
    private SeatType seatType;
    private Boolean hasSeat;
    private Integer price;

    public PriceEntity() {
    }

    public PriceEntity(LocalDate trainDate, Long fromAreaId, Long toAreaId, Long trainId,
                       Long fromStationId, Long toStationId, Integer fromStopIdx, Integer toStopIdx,
                       SeatType seatType, Boolean hasSeat, Integer price) {
        this.trainDate = trainDate;
        this.fromAreaId = fromAreaId;
        this.toAreaId = toAreaId;
        this.trainId = trainId;
        this.fromStationId = fromStationId;
        this.toStationId = toStationId;
        this.fromStopIdx = fromStopIdx;
        this.toStopIdx = toStopIdx;
        this.seatType = seatType;
        this.hasSeat = hasSeat;
        this.price = price;
    }

    public LocalDate getTrainDate() {
        return trainDate;
    }

    public void setTrainDate(LocalDate trainDate) {
        this.trainDate = trainDate;
    }

    public Long getFromAreaId() {
        return fromAreaId;
    }

    public void setFromAreaId(Long fromAreaId) {
        this.fromAreaId = fromAreaId;
    }

    public Long getToAreaId() {
        return toAreaId;
    }

    public void setToAreaId(Long toAreaId) {
        this.toAreaId = toAreaId;
    }

    public Long getTrainId() {
        return trainId;
    }

    public void setTrainId(Long trainId) {
        this.trainId = trainId;
    }

    public Long getFromStationId() {
        return fromStationId;
    }

    public void setFromStationId(Long fromStationId) {
        this.fromStationId = fromStationId;
    }

    public Long getToStationId() {
        return toStationId;
    }

    public void setToStationId(Long toStationId) {
        this.toStationId = toStationId;
    }

    public Integer getFromStopIdx() {
        return fromStopIdx;
    }

    public void setFromStopIdx(Integer fromStopIdx) {
        this.fromStopIdx = fromStopIdx;
    }

    public Integer getToStopIdx() {
        return toStopIdx;
    }

    public void setToStopIdx(Integer toStopIdx) {
        this.toStopIdx = toStopIdx;
    }

    public SeatType getSeatType() {
        return seatType;
    }

    public void setSeatType(SeatType seatType) {
        this.seatType = seatType;
    }

    public Boolean getHasSeat() {
        return hasSeat;
    }

    public void setHasSeat(Boolean hasSeat) {
        this.hasSeat = hasSeat;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}
