package cn.nispring.rail12306.service;

import cn.nispring.rail12306.entity.PassengerEntity;
import cn.nispring.rail12306.exception.BusinessException;
import cn.nispring.rail12306.mapper.PassengerMapper;
import cn.nispring.rail12306.model.IdType;
import cn.nispring.rail12306.model.Passenger;
import cn.nispring.rail12306.model.PassengerStatus;
import cn.nispring.rail12306.model.User;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static cn.nispring.rail12306.util.Util.isValidChinaIdNo;
import static cn.nispring.rail12306.util.Util.parsePhoneE164;

@Service
public class PassengerService {

    private final PassengerMapper passengerMapper;

    public PassengerService(PassengerMapper passengerMapper) {
        this.passengerMapper = passengerMapper;
    }

    public List<Passenger> getPassenger(User user) {
        return passengerMapper.selectByUser(user.id()).stream().map(entity -> new Passenger(
                entity.getIsUser(),
                entity.getIdType(),
                entity.getIdNo(),
                entity.getName(),
                entity.getPhoneE164(),
                entity.getEmail(),
                entity.getCountryCode(),
                entity.getBirthDate(),
                entity.getSex(),
                entity.getValidThrough(),
                entity.getDiscountType(),
                entity.getStatus()
        )).toList();
    }

    public PassengerEntity makeEntity(Passenger passenger) {
        PassengerEntity entity = new PassengerEntity();
        entity.setIdType(passenger.idType());
        entity.setIdNo(passenger.idNo().toUpperCase());
        entity.setName(passenger.name().trim());
        entity.setDiscountType(passenger.discountType());
        entity.setStatus(PassengerStatus.PENDING);

        String phoneE164 = parsePhoneE164(passenger.phone());

        if (Objects.requireNonNull(passenger.idType()) == IdType.CHINA_RESIDENT) {
            if (phoneE164 == null) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "电话号码格式错误");
            }
            if (!isValidChinaIdNo(passenger.idNo())) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "身份证号格式错误");
            }
            entity.setPhoneE164(phoneE164);
        }

        return entity;
    }

    public void addPassenger(PassengerEntity entity) {
        try {
            passengerMapper.insert(entity);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(HttpStatus.CONFLICT, "乘车人已存在");
        }
    }

    public void updatePassenger(User user, Passenger passenger) {
        PassengerEntity entity = new PassengerEntity();
        entity.setUserId(user.id());
        entity.setIdType(passenger.idType());
        entity.setIdNo(passenger.idNo());
        entity.setDiscountType(passenger.discountType());

        if (passenger.idType().equals(IdType.CHINA_RESIDENT)) {
            entity.setPhoneE164(passenger.phone());
        }

        int nupdate = passengerMapper.updateById(entity);
        if (nupdate == 0) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "乘车人不存在");
        }
    }

    public void deletePassenger(User user, IdType idType, String idNo) {
        int ndelete = passengerMapper.deleteById(user.id(), idType, idNo);
        if (ndelete == 0) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "乘车人不存在");
        }
    }
}
