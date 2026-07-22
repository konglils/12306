package cn.nispring.rail12306.entity;

import cn.nispring.rail12306.model.DiscountType;
import cn.nispring.rail12306.model.IdType;
import cn.nispring.rail12306.model.PassengerStatus;
import cn.nispring.rail12306.model.Sex;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PassengerEntity {

    private Long userId;
    private Boolean isUser;
    private IdType idType;
    private String idNo;
    private String name;
    private String phoneE164;
    private String email;
    private String countryCode;
    private LocalDate birthDate;
    private Sex sex;
    private LocalDate validThrough;
    private DiscountType discountType;
    private PassengerStatus status;
    private LocalDateTime updatedAt;

    public PassengerEntity() {
    }

    public PassengerEntity(Long userId, Boolean isUser, IdType idType, String idNo, String name,
                           String phoneE164, String email, String countryCode, LocalDate birthDate,
                           Sex sex, LocalDate validThrough, DiscountType discountType,
                           PassengerStatus status, LocalDateTime updatedAt) {
        this.userId = userId;
        this.isUser = isUser;
        this.idType = idType;
        this.idNo = idNo;
        this.name = name;
        this.phoneE164 = phoneE164;
        this.email = email;
        this.countryCode = countryCode;
        this.birthDate = birthDate;
        this.sex = sex;
        this.validThrough = validThrough;
        this.discountType = discountType;
        this.status = status;
        this.updatedAt = updatedAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Boolean getIsUser() {
        return isUser;
    }

    public void setIsUser(Boolean isUser) {
        this.isUser = isUser;
    }

    public IdType getIdType() {
        return idType;
    }

    public void setIdType(IdType idType) {
        this.idType = idType;
    }

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneE164() {
        return phoneE164;
    }

    public void setPhoneE164(String phoneE164) {
        this.phoneE164 = phoneE164;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public LocalDate getValidThrough() {
        return validThrough;
    }

    public void setValidThrough(LocalDate validThrough) {
        this.validThrough = validThrough;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }

    public PassengerStatus getStatus() {
        return status;
    }

    public void setStatus(PassengerStatus status) {
        this.status = status;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
