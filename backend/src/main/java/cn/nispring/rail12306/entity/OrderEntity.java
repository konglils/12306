package cn.nispring.rail12306.entity;

import cn.nispring.rail12306.model.OrderStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class OrderEntity {

    private Long id;
    private Long userId;
    private LocalDate trainDate;
    private Long trainId;
    private Long fromStationId;
    private Long toStationId;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime expireAt;
    private LocalDateTime paidAt;

    public OrderEntity() {
    }

    public OrderEntity(Long id, Long userId, LocalDate trainDate, Long trainId, Long fromStationId, Long toStationId,
                       OrderStatus status, LocalDateTime createdAt, LocalDateTime expireAt, LocalDateTime paidAt) {
        this.id = id;
        this.userId = userId;
        this.trainDate = trainDate;
        this.trainId = trainId;
        this.fromStationId = fromStationId;
        this.toStationId = toStationId;
        this.status = status;
        this.createdAt = createdAt;
        this.expireAt = expireAt;
        this.paidAt = paidAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(LocalDateTime expireAt) {
        this.expireAt = expireAt;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }
}
