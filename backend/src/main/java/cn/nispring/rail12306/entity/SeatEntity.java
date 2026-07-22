package cn.nispring.rail12306.entity;

import cn.nispring.rail12306.model.SeatType;

import java.time.LocalDate;

public class SeatEntity {

    private LocalDate trainDate;
    private Long trainId;
    private SeatType seatType;
    private Integer segmentIdx;
    private byte[] graph;

    public SeatEntity() {
    }

    public SeatEntity(LocalDate trainDate, Long trainId, SeatType seatType, Integer segmentIdx, byte[] graph) {
        this.trainDate = trainDate;
        this.trainId = trainId;
        this.seatType = seatType;
        this.segmentIdx = segmentIdx;
        this.graph = graph;
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

    public byte[] getGraph() {
        return graph;
    }

    public void setGraph(byte[] graph) {
        this.graph = graph;
    }
}
