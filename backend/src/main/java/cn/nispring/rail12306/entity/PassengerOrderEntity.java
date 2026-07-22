package cn.nispring.rail12306.entity;

import cn.nispring.rail12306.model.DiscountType;
import cn.nispring.rail12306.model.SeatType;
import cn.nispring.rail12306.model.Sex;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PassengerOrderEntity {

    private Long id;
    private Long orderId;
    private Integer idType;
    private String idNo;
    private String name;
    private String phoneE164;
    private String email;
    private String countryCode;
    private LocalDate birthDate;
    private Sex sex;
    private LocalDate validThrough;
    private DiscountType discountType;
    private SeatType seatType;
    private Boolean hasSeat;
    private Integer seatIdx;
    private String seatName;
    private Integer price;
    private Boolean refunded;
    private LocalDateTime refundTime;
    private Integer refundPrice;

    public PassengerOrderEntity() {
    }

    public PassengerOrderEntity(Long id, Long orderId, Integer idType, String idNo, String name,
                                String phoneE164, String email, String countryCode, LocalDate birthDate,
                                Sex sex, LocalDate validThrough, DiscountType discountType,
                                SeatType seatType, Boolean hasSeat, Integer seatIdx, String seatName,
                                Integer price, Boolean refunded, LocalDateTime refundTime, Integer refundPrice) {
        this.id = id;
        this.orderId = orderId;
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
        this.seatType = seatType;
        this.hasSeat = hasSeat;
        this.seatIdx = seatIdx;
        this.seatName = seatName;
        this.price = price;
        this.refunded = refunded;
        this.refundTime = refundTime;
        this.refundPrice = refundPrice;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Integer getIdType() {
        return idType;
    }

    public void setIdType(Integer idType) {
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

    public Integer getSeatIdx() {
        return seatIdx;
    }

    public void setSeatIdx(Integer seatIdx) {
        this.seatIdx = seatIdx;
    }

    public String getSeatName() {
        return seatName;
    }

    public void setSeatName(String seatName) {
        this.seatName = seatName;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Boolean getRefunded() {
        return refunded;
    }

    public void setRefunded(Boolean refunded) {
        this.refunded = refunded;
    }

    public LocalDateTime getRefundTime() {
        return refundTime;
    }

    public void setRefundTime(LocalDateTime refundTime) {
        this.refundTime = refundTime;
    }

    public Integer getRefundPrice() {
        return refundPrice;
    }

    public void setRefundPrice(Integer refundPrice) {
        this.refundPrice = refundPrice;
    }
}
