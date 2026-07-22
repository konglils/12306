package cn.nispring.rail12306.entity;

import cn.nispring.rail12306.model.SeatType;

import java.time.LocalDate;

public class NoSeatEntity {

    private LocalDate trainDate;
    private Long trainId;
    private SeatType seatType;
    private Integer segmentIdx;
    private Integer remaining;

    public NoSeatEntity() {
    }

    public NoSeatEntity(LocalDate trainDate, Long trainId, SeatType seatType, Integer segmentIdx, Integer remaining) {
        this.trainDate = trainDate;
        this.trainId = trainId;
        this.seatType = seatType;
        this.segmentIdx = segmentIdx;
        this.remaining = remaining;
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

    public SeatType getSeatType() {
        return seatType;
    }

    public void setSeatType(SeatType seatType) {
        this.seatType = seatType;
    }

    public Integer getSegmentIdx() {
        return segmentIdx;
    }

    public void setSegmentIdx(Integer segmentIdx) {
        this.segmentIdx = segmentIdx;
    }

    public Integer getRemaining() {
        return remaining;
    }

    public void setRemaining(Integer remaining) {
        this.remaining = remaining;
    }
}
