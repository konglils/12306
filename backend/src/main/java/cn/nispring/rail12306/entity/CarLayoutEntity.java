package cn.nispring.rail12306.entity;

import cn.nispring.rail12306.model.layout.Layout;

import java.time.LocalDate;

public class CarLayoutEntity {

    private LocalDate trainDate;
    private Long trainId;
    private Long carId;
    private Layout layout;

    public CarLayoutEntity() {
    }

    public CarLayoutEntity(LocalDate trainDate, Long trainId, Long carId, Layout layout) {
        this.trainDate = trainDate;
        this.trainId = trainId;
        this.carId = carId;
        this.layout = layout;
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

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public Layout getLayout() {
        return layout;
    }

    public void setLayout(Layout layout) {
        this.layout = layout;
    }
}
