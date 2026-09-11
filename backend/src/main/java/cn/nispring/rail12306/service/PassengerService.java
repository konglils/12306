package cn.nispring.rail12306.service;

import cn.nispring.rail12306.entity.PassengerEntity;
import cn.nispring.rail12306.exception.BusinessException;
import cn.nispring.rail12306.mapper.PassengerMapper;
import cn.nispring.rail12306.model.IdType;
import cn.nispring.rail12306.model.Passenger;
import cn.nispring.rail12306.model.PassengerStatus;
import cn.nispring.rail12306.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static cn.nispring.rail12306.util.Util.isValidChinaIdNo;

@Service
public class PassengerService {

    private final PassengerMapper passengerMapper;

    public PassengerService(PassengerMapper passengerMapper) {
        this.passengerMapper = passengerMapper;
    }

    public void addPassenger(User user, Passenger passenger) {
        PassengerEntity select = passengerMapper.selectById(user.id(), passenger.idType(), passenger.idNo());
        if (select != null) {
            throw new BusinessException(HttpStatus.CONFLICT, "乘车人已存在");
        }
        PassengerEntity entity = new PassengerEntity();
        entity.setUserId(user.id());
        entity.setIsUser(false);
        entity.setIdType(passenger.idType());
        entity.setIdNo(passenger.idNo());
        entity.setName(passenger.name());
        entity.setDiscountType(passenger.discountType());
        entity.setStatus(PassengerStatus.PENDING);

        if (Objects.requireNonNull(passenger.idType()) == IdType.CHINA_RESIDENT) {
            // TODO 验证手机号格式
            if (!isValidChinaIdNo(passenger.idNo())) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "身份证号格式错误");
            }
            // TODO 异步发送身份证号和姓名核验
            entity.setPhoneE164(passenger.phone());
        }

        passengerMapper.insert(entity);
    }
}
